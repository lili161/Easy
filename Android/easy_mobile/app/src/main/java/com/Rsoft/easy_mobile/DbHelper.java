package com.Rsoft.easy_mobile;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DbHelper extends SQLiteOpenHelper {
    public static final String Db_name="ip.db";
    public static final String Table_name="addr";
    public static final int db_version=1;
    public static String ip_addr="ipaddr";
    public static String db_id="id";
    public DbHelper( Context context) {
        super(context, Db_name, null, db_version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+
                Table_name+
                "( id integer primary key autoincrement,"+
                ip_addr+" varchar )";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
