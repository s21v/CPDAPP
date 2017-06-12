package com.cpd.yuqing.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Created by s21v on 2017/6/12.
 */
class AutoChangeLineLayout : ViewGroup{
    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for(i in 0..childCount-1) {
            val child = getChildAt(i)
            if(child.visibility != View.GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var rowWidth = l
        var rowHeight = t
        for (i in 0..childCount-1) {
            val child = getChildAt(i)
            val childlayoutParams = child.layoutParams as ViewGroup.MarginLayoutParams
            if(rowWidth + child.measuredWidth + childlayoutParams.leftMargin + childlayoutParams.rightMargin> measuredWidth) {
                rowWidth = 0
                rowHeight += child.measuredHeight+childlayoutParams.topMargin+childlayoutParams.bottomMargin
            }
            Log.i(TAG,"i:"+i+", rowWidth:"+rowWidth+", child.measuredWidth"+child.measuredWidth+
                    ", childlayoutParams.leftMargin:"+childlayoutParams.leftMargin+
                    ", rowHeight:"+rowHeight+", child.measuredHeight:"+child.measuredHeight+
                    ", childlayoutParams.bottomMargin:"+childlayoutParams.bottomMargin)
            child.layout(rowWidth+childlayoutParams.leftMargin, rowHeight+childlayoutParams.topMargin,
                    rowWidth+child.measuredWidth+childlayoutParams.rightMargin, rowHeight+child.measuredHeight+childlayoutParams.bottomMargin)
            rowWidth+=childlayoutParams.leftMargin+child.measuredWidth+childlayoutParams.rightMargin
        }
    }

    companion object{
        private val TAG = "AutoChangeLineLayout"
    }
}