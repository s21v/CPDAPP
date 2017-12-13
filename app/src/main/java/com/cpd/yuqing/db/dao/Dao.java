package com.cpd.yuqing.db.dao;

import android.content.Context;

import com.cpd.yuqing.db.DataBaseHelper;

/**
 * Created by s21v on 2017/11/10.
 */

public class Dao {
    protected DataBaseHelper mDataBaseHelper;

    public Dao(Context context){
        mDataBaseHelper = new DataBaseHelper(context, DataBaseHelper.DATABASE_NAME, null);
    }

    public void closeDB () {
        if(mDataBaseHelper != null) {
            mDataBaseHelper.close();
            mDataBaseHelper = null;
        }
    }

    public void openDB (Context context) {
        if (mDataBaseHelper == null) {
            mDataBaseHelper = new DataBaseHelper(context, DataBaseHelper.DATABASE_NAME, null);
        }
    }

    public boolean isDBOpen() {
        return mDataBaseHelper != null;
    }
}
