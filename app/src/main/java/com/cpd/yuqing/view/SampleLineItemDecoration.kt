package com.cpd.yuqing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by s21v on 2017/11/21.
 */
class SampleLineItemDecoration(context: Context, colorRes: Int, orientation: Int, lineSize: Int, private val isHomeNewsPage: Boolean)
    : RecyclerView.ItemDecoration() {
    private val mOrientation:Int = orientation
    private val mLineSize = lineSize
    private val mFillColor:Int = context.resources.getColor(colorRes, null)

    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        when(mOrientation) {
            HORIZONTAL_LIST -> drawVerticalDividerLine(c, parent, state)
            VERTICAL_LIST -> drawHorizontalDividerLine(c, parent, state)
        }
    }

    //绘制水平分割线
    private fun drawHorizontalDividerLine(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = mFillColor
        val left = parent!!.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        val startIndex = if (isHomeNewsPage) 1 else 0
        for (i in startIndex until childCount-1) {
            val childView = parent.getChildAt(i)
            val layoutParams = childView.layoutParams as RecyclerView.LayoutParams
            val top = childView.bottom + layoutParams.topMargin + layoutParams.bottomMargin
            val bottom = top + mLineSize
            c!!.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    //绘制垂直分割线
    private fun drawVerticalDividerLine(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = mFillColor
        val top = parent!!.paddingTop
        val bottom = parent.height - parent.bottom
        val childCount = parent.childCount
        val startIndex = if (isHomeNewsPage) 1 else 0
        for (i in startIndex until childCount-1) {
            val childView = parent.getChildAt(i)
            val layoutParams = childView.layoutParams as RecyclerView.LayoutParams
            val left = childView.width + layoutParams.leftMargin + layoutParams.rightMargin
            val right = left + mLineSize
            c!!.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        when(mOrientation) {
            HORIZONTAL_LIST -> outRect!!.set(0, 0, mLineSize, 0)
            VERTICAL_LIST -> outRect!!.set(0, 0, 0, mLineSize)
        }
    }

    companion object {
        const val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL    //水平列表
        const val VERTICAL_LIST = LinearLayoutManager.VERTICAL    //垂直列表
    }
}