package com.cpd.yuqing.db.vo.video

import android.os.Parcel
import android.os.Parcelable

/**
 * 视频新闻类
 * Created by s21v on 2018/3/5.
 */

data class News(var id: Int = 0, var title: String? = null, var newsUrl: String? = null, var source: String? = null,
                var author: String? = null, var publishTime: String? = null, var thumbIconUrl: String? = null,
                var videoUrl: String? = null, var content: String? = null, var channelId: Int = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeInt(id)
        p0?.writeString(title)
        p0?.writeString(newsUrl)
        p0?.writeString(source)
        p0?.writeString(author)
        p0?.writeString(publishTime)
        p0?.writeString(thumbIconUrl)
        p0?.writeString(videoUrl)
        p0?.writeString(content)
        p0?.writeInt(channelId)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getSourceStr() = "视频来源：$source"

    fun posterMix(): String = "责任编辑：$author"

    fun formatPubTime(): String = "发布时间：" + publishTime!!.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

    fun minFormatPubTime(): String = publishTime!!.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

    companion object CREATOR : Parcelable.Creator<News> {
        override fun createFromParcel(parcel: Parcel): News {
            return News(parcel)
        }

        override fun newArray(size: Int): Array<News?> {
            return arrayOfNulls(size)
        }
    }

}