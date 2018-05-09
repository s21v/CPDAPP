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
                   var pointList: LinkedList<Point> = LinkedList(), var imgList: LinkedHashMap<String, String> = linkedMapOf(),
                   var content: String = "", var rectList: ArrayList<Rect>? = null) : Parcelable {
    constructor(parcel: Parcel) : this() {
        introTitle = parcel.readString()
        title = parcel.readString()
        subTitle = parcel.readString()
        parcel.readList(pointList, Point::class.java.classLoader)
        val bundle = parcel.readBundle(LinkedHashMap::class.java.classLoader)
        imgList = bundle.getSerializable("imgList") as LinkedHashMap<String, String>
        Log.i("read Parcel","$imgList")
        content = parcel.readString()
        parcel.readTypedList(rectList, Rect.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(introTitle)
        parcel.writeString(title)
        parcel.writeString(subTitle)
        parcel.writeList(pointList)
        val bundle = Bundle()
        bundle.putSerializable("imgList", imgList)
        parcel.writeBundle(bundle)
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