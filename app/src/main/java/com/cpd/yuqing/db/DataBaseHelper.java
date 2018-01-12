package com.cpd.yuqing.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by s21v on 2017/4/20.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DataBaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "CpdNews_db";
    //创建用户表
    private static final String CREATE_USER_TABLE = "create table user" +
            "(_id integer primary key autoincrement," +
            "nickname text not null," +
            "phoneNum text not null," +
            "password text not null" +
            ")";

    //创建栏目表
    private static final String CREATE_CHANNEL_TABLE = "create table channel" +
            "(_id varchar(15) primary key," +
            "name text not null," +
            "sortNum int)";

    //创建新闻表
    private static final String CREATE_NEWS_TABLE = "create table news (_id text primary key, " +
            "channelId text not null, homePageTitle text, contentPageTitle text, " +
            "pubTime text, source text, author text, poster text, content text, " +
            "url text not null, picUrls text," +
            "FOREIGN KEY (channelId) REFERENCES channel (_id) )";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建立用户表,存储用户信息
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2: //创建栏目表，用于存储栏目信息
                db.execSQL(CREATE_CHANNEL_TABLE);
                break;
            case 3: //创建新闻表，用于存储收藏的新闻
                db.execSQL(CREATE_NEWS_TABLE);
                break;
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            switch (oldVersion) {
                case 3:
                    db.execSQL("drop table if exists news");
                case 2:
                    db.execSQL("drop table if exists channel");
                    onCreate(db);
                    break;
            }
        }
    }
}
