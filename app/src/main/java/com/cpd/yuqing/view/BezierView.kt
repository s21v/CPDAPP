package com.cpd.yuqing.view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.cpd.yuqing.R
import kotlin.properties.Delegates

/**
 * Created by s21v on 2018/1/19.
 */
class BezierView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val firstWaveLengthRatio:Float
    private val secondWaveLengthRatio:Float
    private val firstPaint:Paint
    private val secondPaint:Paint
    //波长
    private var firstWaveLength by Delegates.notNull<Float>()
    private var secondWaveLength by Delegates.notNull<Float>()
    private val animator1: ValueAnimator
    private val animator2: ValueAnimator
    private val animatorSet: AnimatorSet
    private var offset1 = 0
    private var offset2 = 0
    private val firstBezierPath = Path()
    private val secondBezierPath = Path()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BezierView)
        val firstColor = typedArray.getColor(R.styleable.BezierView_firstColor, resources.getColor(R.color.colorPrimary, null))
        val secondColor = typedArray.getColor(R.styleable.BezierView_secondColor, resources.getColor(R.color.colorPrimaryDark, null))
        val firstAnimationDuration = typedArray.getInt(R.styleable.BezierView_firstAnimationDuration, 10)
        val secondAnimationDuration = typedArray.getInt(R.styleable.BezierView_secondAnimationDuration, 4)
//        firstLeft = typedArray.getFloat(R.styleable.BezierView_firstWaveLeft, -1f)
        firstWaveLengthRatio = typedArray.getFloat(R.styleable.BezierView_firstWaveLengthRatio, 0.5f)
        secondWaveLengthRatio = typedArray.getFloat(R.styleable.BezierView_secondWaveLengthRatio, 0.5f)
        typedArray.recycle()
        //初始化画笔
        firstPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        firstPaint.style = Paint.Style.FILL_AND_STROKE
        firstPaint.isDither = true
        firstPaint.color = firstColor
        secondPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        secondPaint.style = Paint.Style.FILL_AND_STROKE
        secondPaint.isDither = true
        secondPaint.color = secondColor
        //初始化动画
        animator1 = ValueAnimator()
        animator1.duration = (firstAnimationDuration*1000).toLong()
        animator1.repeatCount = ValueAnimator.INFINITE
        animator1.interpolator = LinearInterpolator()
        animator1.addUpdateListener {
            offset1 = it.animatedValue as Int
            invalidate()
        }
        animator2 = ValueAnimator()
        animator2.duration = (secondAnimationDuration*1000).toLong()
        animator2.repeatCount = ValueAnimator.INFINITE
        animator2.interpolator = LinearInterpolator()
        animator2.addUpdateListener {
            offset2 = it.animatedValue as Int
            invalidate()
        }
        animatorSet = AnimatorSet()
        animatorSet.playTogether(animator1, animator2)
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun startAnimator() {
        if (animatorSet.isPaused)
            animatorSet.resume()
        else
            animatorSet.start()
    }

    fun stopAnimator() {
        animatorSet.pause()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        firstWaveLength = firstWaveLengthRatio * width
        secondWaveLength = secondWaveLengthRatio * width
        animator1.setIntValues(0, firstWaveLength.toInt())
        animator2.setIntValues(0, secondWaveLength.toInt())
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        firstBezierPath.reset()
        secondBezierPath.reset()
        val firstHalfWaveLength = firstWaveLength /2
        val secondHalfWaveLength = secondWaveLength /2
        val waveHeight = (height/2).toFloat()
        val waveSize: Int = (Math.ceil((width/ firstWaveLength).toDouble())+1).toInt()
        firstBezierPath.moveTo(-firstWaveLength + offset1, waveHeight)
        secondBezierPath.moveTo(-secondWaveLength + offset2, waveHeight)
        for (i in 0 .. waveSize) {
            firstBezierPath.rQuadTo(firstHalfWaveLength/2, -waveHeight, firstHalfWaveLength, 0f)
            firstBezierPath.rQuadTo(firstHalfWaveLength/2, waveHeight, firstHalfWaveLength, 0f)
            secondBezierPath.rQuadTo(secondHalfWaveLength/2, -waveHeight, secondHalfWaveLength, 0f)
            secondBezierPath.rQuadTo(secondHalfWaveLength/2, waveHeight, secondHalfWaveLength, 0f)
        }
        firstBezierPath.lineTo(width.toFloat(), 0f)
        firstBezierPath.lineTo(-firstWaveLength + offset1, 0f)
        secondBezierPath.lineTo(width.toFloat(), 0f)
        secondBezierPath.lineTo(-secondWaveLength + offset2, 0f)
        firstBezierPath.close()
        secondBezierPath.close()

        canvas!!.drawPath(firstBezierPath, firstPaint)
        canvas!!.drawPath(secondBezierPath, secondPaint)
    }

    companion object {
        val TAG = BezierView::class.java.simpleName
    }
}