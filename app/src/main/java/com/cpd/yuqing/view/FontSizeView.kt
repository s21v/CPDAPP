package com.cpd.yuqing.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Canvas
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import com.cpd.yuqing.R
import kotlin.properties.Delegates

/**
 * Created by s21v on 2017/12/28.
 */

class FontSizeView : ViewGroup{
    private lateinit var fontSizeValue: IntArray
    private val fontTextView: ArrayList<TextView> = arrayListOf()
    private var fontTextViewPosition: ArrayList<Int> = arrayListOf()
    private lateinit var line: View
    private var lineTopSpaceSize: Float by Delegates.notNull()
    private lateinit var slider: View
    private var mSliderDragHelper: ViewDragHelper by Delegates.notNull()
    private var currentSliderPosition: Int by Delegates.notNull()
    var callBack: SliderPositionChangeListener by Delegates.notNull()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //设置属性
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.FontSizeView)
        val fontSizeNameRes = typeArray.getResourceId(R.styleable.FontSizeView_fontSizeNameArray, R.array.FontSizeName)
        val fontSizeValueRes = typeArray.getResourceId(R.styleable.FontSizeView_fontSizeValueArray, R.array.FontSizeValue)
        val sliderBackground = typeArray.getResourceId(R.styleable.FontSizeView_sliderBackground, R.color.colorPrimaryDark)
        val textSize = typeArray.getDimension(R.styleable.FontSizeView_fontTextSize, 10f)
        lineTopSpaceSize = typeArray.getDimension(R.styleable.FontSizeView_lineTopSpaceSize, 8f)
        val lineHeight = typeArray.getDimension(R.styleable.FontSizeView_lineHeight, 1f)
        val lineColor = typeArray.getColor(R.styleable.FontSizeView_lineColor, resources.getColor(R.color.colorPrimary, null))
        typeArray.recycle()
        val fontSizeName = resources.getStringArray(fontSizeNameRes)
        fontSizeValue = resources.getIntArray(fontSizeValueRes)
        setWillNotDraw(false)
        //加载TextView
        for (i in 0 until fontSizeName.size) {
            val textView = TextView(context)
            textView.text = fontSizeName[i]
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            textView.gravity = Gravity.CENTER
            textView.setTextColor(resources.getColor(R.color.textColorPrimary, null))
            fontTextView.add(textView)
            addView(textView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        }
        //加载滑块的line
        line = View(context)
        line.setBackgroundColor(lineColor)
        line.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, lineHeight.toInt())
        line.isFocusableInTouchMode = false
        addView(line)
        //加载滑块
        slider = View(context)
        slider.background = resources.getDrawable(sliderBackground, null)
        addView(slider)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mSliderDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this@FontSizeView)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val totalWidth = mWidthSize - paddingLeft - paddingRight
        //测量TextView的大小
        for (i in 0 until fontTextView.size) {
            fontTextView[i].measure(MeasureSpec.makeMeasureSpec(totalWidth/fontTextView.size, MeasureSpec.getMode(widthMeasureSpec)),
                    heightMeasureSpec)
        }
        //测量滑块和线的大小
        val drawableBg = slider.background
        val measureSpecWidth = MeasureSpec.makeMeasureSpec(drawableBg.intrinsicWidth*3, MeasureSpec.EXACTLY)
        val measureSpecHeight = MeasureSpec.makeMeasureSpec(drawableBg.intrinsicHeight*3, MeasureSpec.EXACTLY)
        val offsetWidth = fontTextView[0].measuredWidth/2 - drawableBg.intrinsicWidth*1.5 //intrinsicWidth*1.5 为 measureSpecWidth的一半
        line.measure(MeasureSpec.makeMeasureSpec((totalWidth-offsetWidth*2).toInt(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(line.layoutParams.height, MeasureSpec.EXACTLY))
        slider.measure(measureSpecWidth, measureSpecHeight)
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            val totalHeight = fontTextView[0].measuredHeight + lineTopSpaceSize + line.measuredHeight +
                    (slider.measuredHeight-line.measuredHeight)/2 +
                    paddingTop + paddingBottom
            setMeasuredDimension(mWidthSize, totalHeight.toInt())
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //布局TextView
        for (i in 0 until fontTextView.size) {
            val child = fontTextView[i]
            val left = paddingLeft + i*child.measuredWidth
            child.layout(left, paddingTop, left + child.measuredWidth, paddingTop + child.measuredHeight)
            if (fontTextViewPosition.size != fontTextView.size)
                fontTextViewPosition.add(left+child.measuredWidth/2)
            else
                fontTextViewPosition[i] = left+child.measuredWidth/2
        }
        val fontTextViewWidth = fontTextView[0].measuredWidth   //textView的宽度
        //布局line
        val lineLeft = fontTextView[0].left + fontTextViewWidth/2 - slider.measuredWidth/2
        val lineRight = fontTextView[fontTextView.size-1].left+fontTextViewWidth/2 + slider.measuredWidth/2
        val lineTop = paddingTop + fontTextView[0].measuredHeight + lineTopSpaceSize
        line.layout(lineLeft, lineTop.toInt(), lineRight, (lineTop+line.measuredHeight).toInt())
        //布局滑块view
        val mWidth = slider.measuredWidth
        //根据当前位置设置滑块的位置
        currentSliderPosition = context.getSharedPreferences("contentSetting", MODE_PRIVATE)
                .getInt("currentFontSizePosition", fontTextView.size/2)
        val sliderLeft = fontTextView[currentSliderPosition].left+(fontTextViewWidth-mWidth)/2
        val sliderTop = paddingTop + fontTextView[0].measuredHeight + lineTopSpaceSize - slider.measuredHeight/2
        slider.layout(sliderLeft, sliderTop.toInt(), sliderLeft+mWidth, (sliderTop+slider.measuredHeight).toInt())
        currentSliderPosition = fontTextView.size/2
    }

    override fun onDraw(canvas: Canvas?) {
        dispatchDraw(canvas)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mSliderDragHelper.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mSliderDragHelper.processTouchEvent(event)
        return true
    }

    interface SliderPositionChangeListener{
        fun onSliderPositionChange(currentSliderValue: Int)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //滑动设置
        mSliderDragHelper = ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View?, pointerId: Int): Boolean {
                return child == slider
            }

            override fun clampViewPositionHorizontal(child: View?, left: Int, dx: Int): Int {
                val minLeft = line.left
                val maxRight = line.right-child!!.measuredWidth
                return Math.min(Math.max(left, minLeft), maxRight)
            }

            override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {
                return child!!.top
            }

            override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
                val sliderCenterPosition = releasedChild!!.left + releasedChild.measuredWidth / 2
                for (i in 0 until fontTextViewPosition.size) {
                    if (sliderCenterPosition <= fontTextViewPosition[i]) {
                        currentSliderPosition =
                                if (i != 0)
                                    if (Math.abs(sliderCenterPosition - fontTextViewPosition[i])
                                            < Math.abs(sliderCenterPosition - fontTextViewPosition[i - 1]))
                                        i
                                    else
                                        (i - 1)
                                else
                                    0
                        mSliderDragHelper.smoothSlideViewTo(releasedChild,
                                fontTextViewPosition[currentSliderPosition] - releasedChild.measuredWidth / 2,
                                releasedChild.top)
                        ViewCompat.postInvalidateOnAnimation(this@FontSizeView)
                        callBack.onSliderPositionChange(fontSizeValue[currentSliderPosition])
                        break
                    }
                }
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //持久化保存
        context.getSharedPreferences("contentSetting", MODE_PRIVATE)
                .edit()
                .putInt("currentFontSizePosition", currentSliderPosition)
                .putInt("currentFontSizeValue", fontSizeValue[currentSliderPosition])
                .apply()
    }

    companion object {
        val TAG: String = FontSizeView::class.java.simpleName
    }
}
