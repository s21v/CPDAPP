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
    public static final int DATABASE_VERSION = 4;

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
            "url text not null, picUrls text, " +
            "favorite integer default 0, thumbUp integer default 0, " +
            "FOREIGN KEY (channelId) REFERENCES channel (_id) )";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", "onCreate");
        //建立用户表,存储用户信息
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CHANNEL_TABLE);
        db.execSQL(CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DBHelper", "onUpgrade, newVersion:"+newVersion);
        switch (newVersion) {
            case 4:
                //添加字段表示是否收藏 0表示否 1表示是
                db.execSQL("alter table news add column favorite integer default 0");
                //添加字段表示是否点赞
                db.execSQL("alter table news add column thumbUp integer default 0");
                break;
        }
//        db.execSQL("drop table if exists news");
//        db.execSQL("drop table if exists channel");
//        db.execSQL("drop table if exists user");
//        onCreate(db);
    }
}
