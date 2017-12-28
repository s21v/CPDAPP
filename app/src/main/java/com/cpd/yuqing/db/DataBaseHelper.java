package com.cpd.yuqing.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by s21v on 2017/4/20.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DataBaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "CpdNews_db";
    private static final String CREATE_USER_TABLE = "create table user" +
            "(_id integer primary key autoincrement," +
            "nickname text not null," +
            "phoneNum text not null," +
            "password text not null" +
            ")";
    //创建栏目表
    private static final String CREATE_CHANNEL_TABEL = "create table channel" +
            "(_id varchar(15) primary key," +
            "name text not null," +
            "sortNum int)";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CHANNEL_TABEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists channel");
        onCreate(db);
    }
}
