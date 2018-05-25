package com.cpd.yuqing.db.vo.szb

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * 数字报文章类
 * Created by s21v on 2018/4/24.
 */
data class Article(var introTitle: String? = null, var title: String = "", var subTitle: String? = null,
                   var pointList: LinkedList<Point> = LinkedList(), var imgList: HashMap<String, String> = linkedMapOf(),
                   var content: String = "", var rectList: ArrayList<Rect> = arrayListOf()) : Parcelable {
    constructor(parcel: Parcel) : this() {
        introTitle = parcel.readString()
        title = parcel.readString()
        subTitle = parcel.readString()
        parcel.readList(pointList, Point::class.java.classLoader)
        imgList = parcel.readHashMap(String::class.java.classLoader) as HashMap<String, String>
        Log.i("read Parcel", "$imgList")
        content = parcel.readString()
        parcel.readTypedList(rectList, Rect.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(introTitle)
        parcel.writeString(title)
        parcel.writeString(subTitle)
        parcel.writeList(pointList)
        parcel.writeMap(imgList)
        parcel.writeString(content)
        parcel.writeTypedList(rectList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }
}