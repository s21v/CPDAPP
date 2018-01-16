package com.cpd.yuqing.db.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cpd.yuqing.db.vo.News

/**
 * Created by s21v on 2018/1/10.
 */
class NewsDao(context: Context) : Dao(context) {
    companion object {
        private val TABLE_NAME = "news"
        val TYPE_FAVORITE = 1   //收藏
        val TYPE_THUMBUP = 2    //点赞
    }

    fun operation(news: News, type: Int, value: Int): Int {
        //检查新闻是否已保存在数据库中
        val result: Int;
        val db = mDataBaseHelper.writableDatabase
        val cursor = db.query(TABLE_NAME, null, "_id=?", arrayOf(news.news_id), null,
                null, null)
        if (cursor.moveToFirst()) {
            //数据库中存在该新闻，更新新闻字段
            val cv = ContentValues()
            when(type) {
                TYPE_FAVORITE -> cv.put("favorite", value)
                TYPE_THUMBUP -> cv.put("thumbUp", value)
            }
            result = db.update(TABLE_NAME, cv, "_id=?", arrayOf(news.news_id))
        } else {
            //数据库中不存在该新闻，添加这条新闻
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
            when(type) {
                TYPE_FAVORITE -> cv.put("favorite", value)
                TYPE_THUMBUP -> cv.put("thumbUp", value)
            }
            result = db.insert(TABLE_NAME, null, cv).toInt()
        }
        db.close()
        return result
    }

    fun selectOne(newsId: String): Cursor {
        val db = mDataBaseHelper.writableDatabase
        return db.query(TABLE_NAME, null, "_id=?", arrayOf(newsId), null,
                null, null)
    }

    fun selectRange(limit: Int, offset: Int, type:Int): Cursor? {
        val db = mDataBaseHelper.writableDatabase
        return when(type) {
            TYPE_FAVORITE -> db.query(TABLE_NAME, null, "favorite=1", null,
                    null, null, null, "$limit offset $offset")
            TYPE_THUMBUP -> db.query(TABLE_NAME, null, "thumbUp=1", null,
                    null, null, null, "$limit offset $offset")
            else -> null
        }
    }

    fun delete(newsId: String): Int {
        val db = mDataBaseHelper.writableDatabase
        return db.delete(TABLE_NAME, "_id=? and favorite=0 and thumbUp=0", arrayOf(newsId))
    }
}