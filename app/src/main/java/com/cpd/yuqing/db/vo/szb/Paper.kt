package com.cpd.yuqing.db.vo.szb

import android.os.Parcel
import android.os.Parcelable

/**
 * 报纸版面类
 * Created by s21v on 2018/4/25.
 */
data class Paper(val number: String, val name: String, val type: String, val date: String, val XmlPath: String,
                 val imgPath: String, val isWide: Boolean, var width: Int = 0, var height: Int = 0) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(number)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(date)
        parcel.writeString(XmlPath)
        parcel.writeString(imgPath)
        parcel.writeByte(if (isWide) 1 else 0)
        parcel.writeInt(width)
        parcel.writeInt(height)
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
}