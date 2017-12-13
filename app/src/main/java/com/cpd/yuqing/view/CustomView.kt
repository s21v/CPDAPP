package com.cpd.yuqing.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by s21v on 2017/8/24.
 */
class CustomView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs:AttributeSet) : super(context, attrs)

    var lastX: Float = 0f
    var lastY: Float = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var x = event!!.rawX
        var y = event!!.rawY
        when(event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                var offsetX = x - lastX
                var offsetY = y - lastY
                layout((left+offsetX).toInt(), (top+offsetY).toInt(),
                        (right+offsetX).toInt(), (bottom+offsetY).toInt())
                lastX = x
                lastY = y
            }
        }
        return true
    }
}