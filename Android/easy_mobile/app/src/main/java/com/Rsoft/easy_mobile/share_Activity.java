package com.Rsoft.easy_mobile;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.Base64;

public class share_Activity extends AppCompatActivity {
    private TextView curPathTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_);

    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        ContentResolver contentResolver=this.getContentResolver();
        if(bundle!=null){
            Uri uri= (Uri) bundle.get(Intent.EXTRA_STREAM);
            if(uri!=null){

                try {
                    InputStream is=contentResolver.openInputStream(uri);
                    File file =new File(String.valueOf(Environment.getExternalStoragePublicDirectory("easy_mobile/share_file")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

//                File file=new File(String.valueOf(uri));

//                Cursor cursor= contentResolver.query(uri,null,null,null,null);
//                cursor.moveToFirst();
//                Toast.makeText(this,cursor.getString(cursor.getColumnIndexOrThrow("flags")),100000).show();
//on: column '_data' does not exist. Available columns: [document_id, mime_type, _display_name, last_modified, flags, _size]
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                    ImageView imageView = findViewById(R.id.imageView2);
//                    imageView.setImageBitmap(bitmap);
//                    if(imageView!=null)
//                    Toast.makeText(this,"123",100000).show();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


            }
        }
//        Toast.makeText(this,intent.getStringExtra())
        if(server_activity.socket!=null){

        }else{

        }
    }

}
