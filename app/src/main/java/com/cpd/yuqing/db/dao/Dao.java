package com.cpd.yuqing.db.dao;

import android.content.Context;

import com.cpd.yuqing.db.DataBaseHelper;

import static com.cpd.yuqing.db.DataBaseHelper.DATABASE_VERSION;

/**
 * Created by s21v on 2017/11/10.
 */

public class Dao {
    protected DataBaseHelper mDataBaseHelper;

    public Dao(Context context){
        mDataBaseHelper = new DataBaseHelper(context, DataBaseHelper.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void closeDB () {
        if(mDataBaseHelper != null) {
            mDataBaseHelper.close();
            mDataBaseHelper = null;
        }
    }

    public void openDB (Context context) {
        if (mDataBaseHelper == null) {
            mDataBaseHelper = new DataBaseHelper(context, DataBaseHelper.DATABASE_NAME, null, DATABASE_VERSION);
        }
    }

    public boolean isDBOpen() {
        return mDataBaseHelper != null;
    }
}
