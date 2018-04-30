package com.cpd.yuqing.view

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Scroller

/**
 * Created by s21v on 2018/4/27.
 */
class VerticalOverLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    // child view高度
    private val heightSize: Int
    // 手势
    private val mGestureDetector: GestureDetector
    // 滚动
    private val mScroller: Scroller
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
        // 手势监听
        val gestureListener = MyGestureListrener()
        mGestureDetector = GestureDetector(context, gestureListener)
        // 滚动相关
        mScroller = Scroller(context)
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

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return mGestureDetector.onTouchEvent(event)
//    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            (parent as View).scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    // 当前显示的子View
    private var currentChildIndex = 0

    inner class MyGestureListrener(): GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            Log.i("MyGestureListrener", "onDown")
            parent.requestDisallowInterceptTouchEvent(true)
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.i("MyGestureListrener", "onFling")
            if (currentChildIndex == 0 && velocityY<0) {    // 当报纸页面显示时，手指上滑显示版面信息页面
                Log.i("MyGestureListrener", "手指上滑显示版面信息页面")
                mScroller.startScroll(0, getChildAt(currentChildIndex).top, 0, heightSize, 500)
                invalidate()
                currentChildIndex = 1
                return true
            }
            else if (currentChildIndex == 1 && velocityY>0) {    // 当版面信息页面显示时, 手指下滑显示报纸页面
                Log.i("MyGestureListrener", "手指下滑显示报纸页面")
                mScroller.startScroll(0, getChildAt(currentChildIndex).top, 0, -heightSize, 500)
                invalidate()
                currentChildIndex = 0
                return true
            } else {
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }

        }

    }
}