package com.Rsoft.easy_mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.Rsoft.easy_mobile.R;
import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileBrowser;
import com.aditya.filebrowser.FileChooser;

import java.io.File;
import java.io.IOException;

public class server_activity extends AppCompatActivity {
        private int PICK_FOLD_REQUEST=1;
        public  static  socket socket;
        private  ClipboardManager clipboard;
        private ClipData clipData;
        public Context context;
        public static String file_name;
        private String content_choose="";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_service);
             socket= MainActivity.socket;
            context=getApplication();
            socket.get_context(context);
        }

        @Override
        protected void onStart() {
            super.onStart();

            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        while(true) {
                            final String file_name;
                            final int buffer_size;
                            EditText et= findViewById( R.id.rec_text);
                            String text=socket.recive();
                            System.out.println("-------------------------"+text);
//                            Looper.prepare();
                            if(text.contains(("file_name"))){
                                file_name=text.split("@")[1];
                                buffer_size=Integer.parseInt(text.split("@")[2]);
                                System.out.println("------------------"+file_name+" "+buffer_size);
                                server_activity.file_name=file_name;
                                socket.send("ok_rec_file");

//                                new Thread(new Runnable(){
//                                    @Override
//                                    public void run() {
                                        try {
                                            socket.recive_file(file_name,buffer_size);

//                                            to_Dialog("文件传输完成");
                                            new Thread(){
                                                @Override
                                                public void run() {
                                                    Looper.prepare();
                                                    to_Dialog("文件 "+file_name+" 接收完成");
                                                    Looper.loop();
                                                    Looper.myLooper().quit();

                                                }
                                            }.start();


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
//                                Looper.loop();
//                                    }
//                                }).start();
                            }else {
                                et.setText(text);
                            }
                            }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        @SuppressLint("WrongConstant")
        public void send_msg(View v) throws IOException {
            if(v.getId()==R.id.send_msg){
                EditText et=findViewById(R.id.editText2);
                 final String msg=et.getText().toString();
                if(content_choose.startsWith("file:///")){
//                    Toast.makeText(this,"is a file and file name is :"+content_choose,0).show();
                    // put the method of send file in here
                File file =new File(content_choose.substring(7));
                if(file.exists()){
//                    Toast.makeText(this,"文件存在！",0).show();
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                socket.send_file(file,msg,file.length());
                                Looper.prepare();
                                to_Dialog(et.getText()+"已发送");
                                et.setText("");
                                Looper.loop();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                    Log.d("file absolutely path",content_choose.substring(7));
                    content_choose="";
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket.send(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }
        public void copy_to_clipboad(View v){
            clipboard=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                EditText editText =findViewById(R.id.rec_text);
                String text=editText.getText().toString();
            System.out.println("99999999999999999"+" "+text);
                clipData=ClipData.newPlainText("text",text);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(this,"已复制到剪贴板",Toast.LENGTH_SHORT).show();

        }
        public void to_Dialog(String msg){
            Dialog(msg);
        }
        public void Dialog(String msg){
            Toast.makeText(this.context,msg, Toast.LENGTH_SHORT).show();
        }



        public void testButton(View v){
//            Intent intent = new Intent(this,share_Activity.class);
            Intent intent = new Intent(this, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE,Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            startActivityForResult(intent,PICK_FOLD_REQUEST);
        }
        @SuppressLint("WrongConstant")
        public void Inital_derectory(View v){
            Intent intent =new Intent(this,FileBrowser.class);
            intent.putExtra(Constants.INITIAL_DIRECTORY,new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"easy_mobile").getAbsolutePath());
            startActivity(intent);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_FOLD_REQUEST&&data!=null){
                Uri file0= data.getData();
                String file=Uri.decode(file0.toString());
//                Toast.makeText(this, file, Toast.LENGTH_SHORT).show();
                EditText e=findViewById(R.id.editText2);
                e.setText(file.split("/")[file.split("/").length-1]);
                content_choose=file;
//                Toast.makeText(this, ((Uri) file).toString(), Toast.LENGTH_SHORT).show();
//                EditText e=findViewById(R.id.editText2);
//                e.setText(file.toString().split("/")[file.toString().split("/").length-1]);
//                content_choose=file.toString();
        }
    }
    public void web_click(View v){
        Intent intent =new Intent(this,Browser.class);
        startActivity(intent);
    }
}