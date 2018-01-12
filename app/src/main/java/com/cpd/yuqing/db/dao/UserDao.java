package com.cpd.yuqing.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.cpd.yuqing.db.vo.User;
import java.util.ArrayList;

/**
 * 对用户表的CRUD
 * Created by s21v on 2017/4/20.
 */
public class UserDao extends Dao{
    private static UserDao mInstance;

    private UserDao(Context context) {
        super(context, 1);
    }

    //单例模式
    public static UserDao getInstance(Context context) {
        if(mInstance == null) {
            synchronized (UserDao.class) {
                if (mInstance == null)
                    mInstance = new UserDao(context);
            }
        } else {
            mInstance.openDB(context);
        }
        return mInstance;
    }
    
    public long insert (User user) {
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nickname", user.getNickname());
        cv.put("phoneNum", user.getPhoneNum());
        cv.put("password", user.getPassword());
        long result = db.insert("user", null, cv);
        return result;
    }
    
    public long insertAll (ArrayList<User> users) {
        long result = 0;
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        for (User u : users) {
            ContentValues cv = new ContentValues();
            cv.put("nickname", u.getNickname());
            cv.put("phoneNum", u.getPhoneNum());
            cv.put("password", u.getPassword());
            result += db.insert("user", null, cv);
        }
        return result;
    }
    
    public int delete (String where, String[] args) {
        int result;
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        result =db.delete("user", where, args);
        return result;
    }
    
    public int update (User user, String where, String[] args) {
        ContentValues cv = new ContentValues();
        cv.put("nickname", user.getNickname());
        cv.put("phoneNum", user.getPhoneNum());
        cv.put("password", user.getPassword());
        return update(cv, where, args);
    }

    public int update (ContentValues cv, String where, String[] args) {
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        int result = db.update("user", cv, where, args);
        return result;
    }
    
    public ArrayList<User> query4ArrayList (String where, String[] args) {
        ArrayList<User> userList = new ArrayList<>();
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("user", null, where, args, null, null, null);
        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            user.setPhoneNum(cursor.getString(cursor.getColumnIndex("phoneNum")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            userList.add(user);
        }
        cursor.close();
        return userList;
    }

    public Cursor query4Cursor (String where, String[] args) {
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        Cursor result = db.query("user", null, where, args, null, null, null);
//        result.setNotificationUri(mContext.getContentResolver(), UserContentProvider.USER_CHANGE_SIGNAL);
        return result;
    }
}
