package com.cpd.yuqing.data

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

/**
 * POJO : 新闻实体类
 * Created by s21v on 2017/6/27.
 * @param newsID : 新闻ID
 * @param channelID : 栏目ID
 * @param homePageTitle : 栏目页标题
 * @param contentPageTitle : 内容页标题
 * @param publishTime : 发布时间
 * @param source : 来源
 * @param author : 作者
 * @param poster : 责任编辑
 * @param pictureUrlList : 图片列表
 * @param content : 内容
 * @param contentUrlStr : 内容页链接
 * @param commentCount : 评论数
 */
data class News(val newsID: String="", val channelID: String="",
                var homePageTitle: String="", val contentPageTitle: String="",
                var publishTime: String="", var source: String="",
                val author: String="", val poster: String="",
                val content: String="", val contentUrlStr: String="",
                var commentCount: Int=0,
                var pictureUrlList: ArrayList<String>? = null) : Parcelable {

    fun getFormatCommentCount(): String = when {
        commentCount<1000 -> Integer.toString(commentCount)
        else -> "999+"
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(newsID)
        dest.writeString(channelID)
        dest.writeString(homePageTitle)
        dest.writeString(contentPageTitle)
        dest.writeString(publishTime)
        dest.writeString(source)
        dest.writeString(author)
        dest.writeString(poster)
        dest.writeString(content)
        dest.writeString(contentUrlStr)
        dest.writeInt(commentCount)
        dest.writeStringList(pictureUrlList)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<News> = object : Parcelable.Creator<News> {
            override fun createFromParcel(`in`: Parcel): News {
                val result = News(`in`.readString(), `in`.readString(), `in`.readString(),
                        `in`.readString(), `in`.readString(), `in`.readString(), `in`.readString(),
                        `in`.readString(), `in`.readString(), `in`.readString(), `in`.readInt())
                result.pictureUrlList = arrayListOf()
                `in`.readStringList(result.pictureUrlList)
                return result
            }

            override fun newArray(size: Int): Array<News> = newArray(size)
        }
    }
}
