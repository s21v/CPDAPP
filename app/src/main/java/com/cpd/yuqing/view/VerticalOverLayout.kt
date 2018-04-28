package com.cpd.yuqing.view

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import java.security.MessageDigest

/**
 * Created by s21v on 2018/4/27.
 */
class VerticalOverLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    // child view高度
    val heightSize: Int
    init {
        // 获得屏幕高度
        val dm = DisplayMetrics()
        val wm: WindowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        val screenHeight = dm.heightPixels
        // 获得手机状态栏高度
        val resourceID = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        var statusBarHeight = 0
        if (resourceID > 0)
            statusBarHeight = context.resources.getDimensionPixelSize(resourceID)
        heightSize = screenHeight - statusBarHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY))
            totalHeight += child.measuredHeight
        }
        val finalHeightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY)
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                getDefaultSize(suggestedMinimumHeight, finalHeightMeasureSpec))
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