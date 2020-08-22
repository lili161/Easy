package com.Rsoft.easy_mobile;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class socket extends Thread {
    private Socket socket=null;
    private Context context;
    public void get_context(Context context){
        this.context=context;
    }

    public socket(String ip_add) throws IOException, InterruptedException {
        System.out.println("line in");
        this.socket=new Socket(ip_add,3721);
        this.socket.setKeepAlive(true);
        if(socket!=null){
            System.out.println("connect success!");
//                recive();
//            System.out.println("after recive!");
        }else{
            System.out.println("failed");
        }
    }

    public void send(String msg) throws IOException {
        OutputStream outputStream=socket.getOutputStream();
        OutputStreamWriter writer=new OutputStreamWriter(outputStream);
        writer.write(msg);
        writer.flush();
        System.out.println("already send"+msg);
    }
    public void send_file(File file,String name,long size) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(file);
        DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
        dataOutputStream.write(new String("file_name@"+file.getName()+"@"+file.length()).getBytes("utf-8"));
        dataOutputStream.flush();
//        dataOutputStream.write(new String(String.valueOf(file.length())).getBytes("utf-8"));
//        dataOutputStream.flush();
        byte [] bytes=new byte[1024];
        int length=0;
        while((length=fileInputStream.read(bytes,0,bytes.length))!=-1){
            dataOutputStream.write(bytes,0,length);
            dataOutputStream.flush();
        }
        Log.d("TIP:","文件传输完成！");
    }
    public String recive() throws IOException {
       InputStream inputStream= socket.getInputStream();
        InputStreamReader stream_reader =new InputStreamReader(inputStream,"utf-8");
        BufferedReader reader=new BufferedReader(stream_reader);
         String all="";
//        reader.read(CharBuffer.wrap(msg));
//        System.out.println( reader.readLine().length());
       int lines= Integer.parseInt(reader.readLine());
        for(int i=0;i<lines;i++){
            all+=reader.readLine();
        }
        System.out.println("all"+all);
        return all;
    }
    public void recive_file(String file_name,int buffer_size) throws IOException, InterruptedException {

        String save_path=String.valueOf(Environment.getExternalStoragePublicDirectory("easy_mobile"));
        byte[] buffer =new byte[buffer_size];
        File file=Environment.getExternalStoragePublicDirectory("easy_mobile");
        File file1=new File(Environment.getExternalStoragePublicDirectory("easy_mobile")+"/"+file_name);

        if(!file.exists()){
            file.mkdirs();
            file1.createNewFile();
        }else{
            if(!file1.exists()){
                file1.createNewFile();
            }
        }

        InputStream inputStream= socket.getInputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(save_path+"/"+file_name)));
        DataInputStream dataInputStream=new DataInputStream(inputStream);

        dataInputStream.readFully(buffer);
        sleep((long)2000);
        dataOutputStream.write(buffer,0,buffer_size);
        dataOutputStream.flush();;
        dataOutputStream.close();

//        dataInputStream.close();
    }


}
