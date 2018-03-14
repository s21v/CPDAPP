package com.cpd.yuqing.db.vo.video

import android.os.Parcel
import android.os.Parcelable

/**
 * 视频新闻的栏目类
 * Created by s21v on 2018/3/5.
 */

data class Channel(var id: Int = 0, var name: String? = null,
                   var parentChannelId: Int = 0, var url: String? = null,
                   var subject_img_url: String? = null) : Parcelable {
    
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(parentChannelId)
        parcel.writeString(url)
        parcel.writeString(subject_img_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}
