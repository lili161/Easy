package com.Rsoft.easy_mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    public static int Flag=0;//用于判断是不是PC端的IP
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    public static socket socket;
    public Context context;
   public static String  maybe_ip="0.0.0.0";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

//用于验证是否有内存读取权限
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            int permission1 = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //在onCreate()方法中调用该方法即可

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        verifyStoragePermissions(this);
        dbHelper = new DbHelper(this);
        database = dbHelper.getWritableDatabase();
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("easy_mobile")));
//    File file1=Environment.getExternalStoragePublicDirectory("easy_mobile1");
        if (!file.exists()) {
            boolean a = false;
            a = file.mkdirs();
            //            File b= this.context.getExternalFilesDir("test");
            System.out.println(a + "-----------------------------already create " + file.getPath());
        } else {
            System.out.println("-----------------------------exist" + file.getPath());
        }
//        if(!file1.exists()){
//            boolean x=false;
//            x= file1.mkdirs();
//            System.out.println("-------------------xxxx-"+x);
//        }else{
//            System.out.println("-------------------xxxx-"+"have");
//        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        String buffer;
        context = getApplicationContext();
        buffer = query();
        if (buffer!=null) {
            EditText editText = findViewById(R.id.editText3);
            editText.setText(buffer);
        }
    }

    @SuppressLint("WrongConstant")
    public void on_click(View v) throws IOException {
        if (v.getId() == R.id.button) {
           lianjie_click();

        }
    }

    public void db_insert(String ipaddr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.ip_addr, ipaddr);
        database.insert(DbHelper.Table_name, null, contentValues);
    }

    public void db_up_date(String ipaddr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.ip_addr, ipaddr);
        database.update(DbHelper.Table_name, contentValues, DbHelper.db_id + "=?", new String[]{"1"});
    }

    public String query() {
        Cursor cursor = database.query(DbHelper.Table_name, new String[]{DbHelper.ip_addr}, DbHelper.db_id + "= ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() == 0){
            cursor.close();
            return null;
    }else

    {
        cursor.moveToFirst();
        String data = cursor.getString(0);
            cursor.close();
        return data;
    }
}

public int get_Connect_state(){
        int mobile=0;
        int wifi=1;
        int none=-1;
    ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
    if(Settings.Global.getInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,0)==0) {
        if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
            return wifi;
        } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
            return mobile;
        } else {
            return none;
        }
    }else{
        Toast.makeText(context,"飞行模式功能无法使用！",0).show();
        return none;
    }

}
    public String get_ip_addr(Context context){
        WifiManager wifiManager= (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
//        wifiInfo.getIpAddress();

        return int2ip(wifiInfo.getIpAddress());
    }
    public  String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }
    public void help_me_choose(View v) throws IOException {
        if(v.getId()==R.id.button4){
            if(get_Connect_state()==-1){
                Toast.makeText(this, "您并没有连接网络！", 0).show();
            }else if(get_Connect_state()==0){
                Toast.makeText(this, "您连接的不是WIFI！", 0).show();
            }else if(get_Connect_state()==1){
//                Toast.makeText(this, get_ip_addr(this), 0).show();
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           Looper.prepare();
                           test_ping(get_ip_addr(context));
                            Looper.loop();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }).start();
            }
        }
    }
    public void test_ping(String my_ip) throws IOException {
//        Integer.parseInt(my_ip.split(".")[3]);

        int timeout=10;
        String before_body=my_ip.split("\\.")[0]+"."+my_ip.split("\\.")[1]+"."+my_ip.split("\\.")[2]+".";
        for(int i=2;i<=255;i++) {
            if (Flag == 0) {
                if (i == Integer.parseInt(my_ip.split("\\.")[3])) {
                    continue;
                } else {
                    if (InetAddress.getByName(before_body + String.valueOf(i)).isReachable(timeout)) {
                        maybe_ip = before_body + String.valueOf(i);
//                    System.out.println("789   "+before_body+String.valueOf(i));
                        maybe_ip = before_body + String.valueOf(i);
                        EditText editText = findViewById(R.id.editText3);
                        editText.setText(maybe_ip);

                        lianjie_click();
                        break;

//                    Toast.makeText(context,before_body+String.valueOf(i)+"   12",0).show();
                    }

                }
            }else
                break;
        }
    }
    public void lianjie_click(){
        EditText et = findViewById(R.id.editText3);
        final String ip_add = et.getText().toString().trim();
        Toast.makeText(this, "ip：" + ip_add, 0).show();
//            Thread t1=new Thread(new socket(ip_add));
//            t1.start();
        // Android 4.0 之后不能在主线程中请求HTTP请求
        int i = 0;
        Cursor cursor1 = database.rawQuery("select COUNT(*) from " + DbHelper.Table_name, new String[]{});
        cursor1.moveToFirst();
        i = cursor1.getInt(0);
        cursor1.close();
        if (i >= 1) {
//            Toast.makeText(this, "有数据", 0).show();
            EditText editText= findViewById(R.id.editText3);
            db_up_date(editText.getText().toString());
        } else {

//            Toast.makeText(this, "没数据", 0).show();
            EditText editText= findViewById(R.id.editText3);
            db_insert(editText.getText().toString());
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        socket = new socket(ip_add);
                    }catch (java.net.ConnectException e){
                        Flag=0;
                    }
                    while (true) {
                        Flag=1;
                        if (socket.recive().contains("file_name")) {
                            System.out.println("is file");
                        } else {
                            socket.send("yes_its_me");
                            System.out.println("isnt file");
                            Intent intent = new Intent(MainActivity.this, server_activity.class);
                            startActivity(intent);
                            return;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void web_click(View v){
        Intent intent =new Intent(this,Browser.class);
        startActivity(intent);
    }

}
