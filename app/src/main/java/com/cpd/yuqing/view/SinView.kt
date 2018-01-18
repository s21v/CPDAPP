package com.cpd.yuqing.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import com.cpd.yuqing.R


/**
 * Created by s21v on 2017/8/30.
 */
class SinView//动画
(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mFirstSinColor: Int
    private var mSecondSinColor: Int
    private var mHandler: Handler? = null
    private var progress: Int = 0
    private var data: SparseArray<SparseArray<PointF>> = SparseArray(360)

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.SinView)
        mFirstSinColor = typeArray.getColor(R.styleable.SinView_firstSinColor,
                context.resources.getColor(android.R.color.holo_orange_dark))
        mSecondSinColor = typeArray.getColor(R.styleable.SinView_secondSinColor,
                context.resources.getColor(android.R.color.holo_orange_light))
        typeArray.recycle()

        mHandler = @SuppressLint("HandlerLeak")
        object : Handler(){
            override fun handleMessage(msg: Message?) {
                if (msg?.what == 0) {
                    invalidate()
                    mHandler?.sendEmptyMessageDelayed(0, 60L)
                }
            }
        }

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        //设置参数
        val centerY = height / 2
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = 1f
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = mFirstSinColor
        //计算图形
        val path1 = Path()
        val path2 = Path()
        val path3 = Path()
        path2.moveTo(0f, centerY.toFloat())
        path3.moveTo(0f, centerY.toFloat())
        if(data[progress % 360] != null) {
            val sparseArray = data[progress % 360] as SparseArray<PointF>
            for(i in 0 .. width) {
                val path1Y = sparseArray[i].x
                val path2Y = sparseArray[i].y
                path1.lineTo(i.toFloat(), path1Y)
                path2.lineTo(i.toFloat(), path2Y)
                path3.lineTo(i.toFloat(), path1Y)
            }
        } else {
            val pointData = SparseArray<PointF>(width)
            for(i in 0 .. width) {
                val path1Y = centerY - Math.sin(0.5*degreeToRad(i+progress))*centerY
                val path2Y = centerY - Math.cos(0.5*degreeToRad(i+progress+180))*centerY
                val point = PointF(path1Y.toFloat(), path2Y.toFloat())
                pointData.setValueAt(i, point)
                path1.lineTo(i.toFloat(), path1Y.toFloat())
                path2.lineTo(i.toFloat(), path2Y.toFloat())
                path3.lineTo(i.toFloat(), path1Y.toFloat())
            }
            data.setValueAt(progress%360, pointData)
        }

        path1.lineTo(width.toFloat(),0f)
        path1.close()
        path2.lineTo(width.toFloat(),centerY.toFloat())
        path3.lineTo(width.toFloat(),centerY.toFloat())
        path2.op(path3, Path.Op.XOR)
        //绘制图形
        canvas?.drawPath(path1, paint)
        paint.color = mSecondSinColor
        canvas?.drawPath(path2, paint)
        //动画效果
        progress+=1
    }

    /**
     * 角度转换成弧度
     * @param degree
     * @return
     */
    private fun degreeToRad(degree: Int): Double {
        return degree * Math.PI / 180
    }

    companion object {
//        val TAG = SinView::class.java.simpleName
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler?.removeMessages(0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mHandler?.sendEmptyMessage(0)
    }
}