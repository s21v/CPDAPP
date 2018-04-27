package com.cpd.yuqing.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Created by s21v on 2018/4/27.
 */
class VerticalOverLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i("VerticalOverLayout", "heightMeasureSpec size:${MeasureSpec.getSize(heightMeasureSpec)}")
        var totalHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, heightMeasureSpec)
            totalHeight += child.measuredHeight
            Log.i("VerticalOverLayout", "child $i, height:${child.measuredHeight}")
        }
        val finalHeightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY)
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                getDefaultSize(suggestedMinimumHeight, finalHeightMeasureSpec))
        Log.i("VerticalOverLayout", "measuredHeight:$measuredHeight")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.i("VerticalOverLayout", "l:$l, t:$t, r:$r, b:$b, width:$width, height:$height")
        var top = t
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(l, top, r, top+child.measuredHeight)
            top += child.measuredHeight
        }
    }

//    var lastY = 0f
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lastY = event.y
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val offsetY = event.y - lastY
//                layout(left, (top+offsetY).toInt(), right, (bottom+offsetY).toInt())
//            }
//        }
//        return true
//    }
}