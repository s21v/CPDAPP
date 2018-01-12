package com.cpd.yuqing.db.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cpd.yuqing.db.vo.News

/**
 * Created by s21v on 2018/1/10.
 */
class NewsDao(context: Context) : Dao(context, 3) {
    companion object {
        private val TABLE_NAME = "news"
    }

    fun insert(news: News): Long {
        val db = mDataBaseHelper.writableDatabase
        val cv = ContentValues()
        cv.put("_id", news.news_id)
        cv.put("channelId", news.channel_id)
        cv.put("homePageTitle", news.homePageTitle)
        cv.put("contentPageTitle", news.contentPageTitle)
        cv.put("pubTime", news.pub_time)
        cv.put("source", news.source)
        cv.put("author", news.author)
        cv.put("poster", news.poster)
        cv.put("content", news.content)
        cv.put("url", news.url)
        cv.put("picUrls", news.picUrls)
        return db.insert(TABLE_NAME, null, cv)
    }

    fun selectOne(newsId: String): Cursor {
        val db = mDataBaseHelper.writableDatabase
        return db.query(TABLE_NAME, null, "_id=?", arrayOf(newsId), null,
                null, null)
    }

    fun selectRange(limit: Int, offset: Int): Cursor {
        val db = mDataBaseHelper.writableDatabase
        return db.query(TABLE_NAME, null, null, null, null,
                null, null, "$limit offset $offset")
    }

    fun delete(newsId: String): Int {
        val db = mDataBaseHelper.writableDatabase
        return db.delete(TABLE_NAME, "_id=?", arrayOf(newsId))
    }
}