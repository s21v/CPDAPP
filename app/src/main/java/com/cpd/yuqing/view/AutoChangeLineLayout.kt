package com.cpd.yuqing.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import com.cpd.yuqing.R

/**
 * Created by s21v on 2017/6/12.
 */
class AutoChangeLineLayout : ViewGroup{
    var itemLRMargin: Int = 0
    var lineSpace: Int = 0
    var adapter: Adapter
    constructor(context: Context, adapter: Adapter): super(context) {
        this.adapter = adapter
    }

    constructor(context: Context, attrs: AttributeSet, adapter: Adapter) : super(context, attrs){
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.AutoChangeLineLayout)
        itemLRMargin = typeArray.getDimensionPixelSize(R.styleable.AutoChangeLineLayout_ItemLRMargin, 0)
        lineSpace = typeArray.getDimensionPixelSize(R.styleable.AutoChangeLineLayout_LineSpace, 0)
        this.adapter = adapter
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var lineWidth = 0
        var lineHeight = 0

        for(i in 0..childCount-1) {
            val child = getChildAt(i)
            if(child.visibility != View.GONE) { //计算子view的宽高
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                val childWidth = child.measuredWidth+itemLRMargin
                lineWidth += childWidth
                if(lineWidth >= widthSize) {
                    lineHeight += (child.measuredHeight + lineSpace)
                    lineWidth = childWidth
                }
                if(i == childCount-1)
                    lineHeight += (child.measuredHeight + lineSpace)
            }
        }
        if(heightMode == MeasureSpec.EXACTLY)
            setMeasuredDimension(widthSize, heightSize)
        else
            setMeasuredDimension(widthSize, lineHeight + lineSpace)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var rowWidth = l
        var rowHeight = t
        for (i in 0..childCount-1) {
            val child = getChildAt(i)
            if(child.visibility != View.GONE) {
                if((rowWidth+child.measuredWidth+itemLRMargin) > measuredWidth) {
                    rowWidth = l
                    rowHeight += (child.measuredHeight+lineSpace)
                }
                child.layout(rowWidth+itemLRMargin, rowHeight, rowWidth+itemLRMargin+child.measuredWidth, rowHeight+child.measuredHeight)
                rowWidth+=(child.measuredWidth + itemLRMargin)
            }
        }
    }

    companion object{
        private val TAG = "AutoChangeLineLayout"
    }
}