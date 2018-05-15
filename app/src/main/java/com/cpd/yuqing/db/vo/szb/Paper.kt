package com.cpd.yuqing.db.vo.szb

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * 报纸版面类
 * Created by s21v on 2018/4/25.
 */
data class Paper(var number: String? = null, var name: String? = null, var type: String? = null,
                 var date: Date? = null, var xmlPath: String? = null, var imgPath: String? = null,
                 var isWide: Boolean = false, var width: Int = 0, var height: Int = 0,
                 var thumbPath: String? = null, var fullImgPath: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this() {
        number = parcel.readString()
        name = parcel.readString()
        type = parcel.readString()
        val bundle = parcel.readBundle(Date::class.java.classLoader)
        date = bundle.getSerializable("date") as Date
        xmlPath = parcel.readString()
        imgPath = parcel.readString()
        isWide = parcel.readByte() != 0.toByte()
        width = parcel.readInt()
        height = parcel.readInt()
        thumbPath = parcel.readString()
        fullImgPath = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(number)
        parcel.writeString(name)
        parcel.writeString(type)
        val bundle = Bundle()
        bundle.putSerializable("date", date)
        parcel.writeBundle(bundle)
        parcel.writeString(xmlPath)
        parcel.writeString(imgPath)
        parcel.writeByte(if (isWide) 1 else 0)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(thumbPath)
        parcel.writeString(fullImgPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Paper> {
        override fun createFromParcel(parcel: Parcel): Paper {
            return Paper(parcel)
        }

        override fun newArray(size: Int): Array<Paper?> {
            return arrayOfNulls(size)
        }
    }

    fun getDesc(): String = "${number}版：$name"
}