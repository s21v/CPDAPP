package com.cpd.yuqing.db.dao

import android.content.ContentValues
import android.content.Context
import com.cpd.yuqing.db.vo.Channel
import java.util.ArrayList

/**
 * Created by s21v on 2017/11/10.
 */

class ChannelDao private constructor(context: Context) : Dao(context, 2) {

    //向数据库中写入
    fun insert(channel: Channel): Long {
        val result: Long
        val db = mDataBaseHelper.writableDatabase
        val cv = ContentValues()
        cv.put("_id", channel.id)
        cv.put("name", channel.name)
        cv.put("sortNum", channel.sortNum)
        result = db.insert("channel", null, cv)
        return result
    }

    //插入栏目列表
    fun insertChannelList(channels: ArrayList<Channel>): Long {
        var result: Long = 0
        val db = mDataBaseHelper.writableDatabase
        db.beginTransaction()   //开启事务
        try {
            for (channel in channels) {
                val cv = ContentValues()
                cv.put("_id", channel.id)
                cv.put("name", channel.name)
                cv.put("sortNum", channel.sortNum)
                result += db.insert("channel", null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return result
    }

    //栏目的可见性变化(包括 是否显示在栏目条上、栏目条上的位置变化等)
    fun updateChannel(channels: ArrayList<Channel>): Int {
        var result = 0
        val db = mDataBaseHelper.writableDatabase
        db.beginTransaction()
        try {
            for (channel in channels) {
                val cv = ContentValues()
                cv.put("sortNum", channel.sortNum)
                result += db.update("channel", cv, "_id=?", arrayOf(channel.id))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return result
    }

    //查询所有栏目信息
    fun queryAll(): ArrayList<Channel>? {
        var result: ArrayList<Channel>? = null
        val db = mDataBaseHelper.writableDatabase
        val cursor = db.query("channel", null, null, null, null, null, "sortNum DESC")
        if (cursor.moveToFirst()) {
            result = ArrayList()
            do {
                result.add(Channel(cursor.getString(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("sortNum"))))
            } while (cursor.moveToNext())
            cursor.close()
        }
        return result
    }

    //按显示条件查询栏目
    fun queryByIsShow(isShowOnBar: Boolean): ArrayList<Channel>? {
        var result: ArrayList<Channel>? = null
        val db = mDataBaseHelper.writableDatabase
        val cursor = if (isShowOnBar) {
            db.query("channel", null, "sortNum>=0", null, null, null, "sortNum DESC")
        } else {
            db.query("channel", null, "sortNum<0", null, null, null, "_id DESC")
        }
        if (cursor.moveToFirst()) {
            result = ArrayList()
            do {
                result.add(Channel(cursor.getString(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("sortNum"))))
            } while (cursor.moveToNext())
            cursor.close()
        }
        return result
    }

    //删除数据库中的项
    fun delete(channel: Channel): Int {
        val db = mDataBaseHelper.writableDatabase
        return db.delete("channel", "_id=?", arrayOf(channel.id))
    }

    companion object {
        private var instance: ChannelDao? = null

        fun getInstance(context: Context): ChannelDao {
            if (instance == null) {
                synchronized(ChannelDao::class.java) {
                    if (instance == null) {
                        instance = ChannelDao(context)
                    }
                }
            }
            instance!!.openDB(context)
            return instance!!
        }
    }
}
