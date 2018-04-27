package com.cpd.yuqing.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.db.vo.szb.Paper
import com.cpd.yuqing.util.SplitRect
import java.util.*
import kotlin.math.abs

/**
 * 数字报遮罩层
 * Created by s21v on 2018/4/25.
 */
class ShadeView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // 数据
    private lateinit var paper: Paper
    private lateinit var articleList: ArrayList<Article>
    private var curTouchArticleIndex = -1
    // 画笔
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // 报纸大小到屏幕大小的缩放值
    private var scaleByX: Float = 1f
    private var scaleByY: Float = 1f
    private val scaledMaximumFlingVelocity: Int // Y轴上的最大滑动速度
    private val scaledTouchSlop: Int     // Android滑动阈值(系统认为最低的滑动距离)

    init {
        paint.style = Paint.Style.FILL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadeView)
        paint.color = typedArray.getColor(R.styleable.ShadeView_color, 0x738fee88)
        typedArray.recycle()
        val viewConfiguration = ViewConfiguration.get(context)
        scaledMaximumFlingVelocity = viewConfiguration.scaledMaximumFlingVelocity / 4
        scaledTouchSlop = viewConfiguration.scaledTouchSlop
    }

    fun setData(paper: Paper, articleList: ArrayList<Article>) {
        this.paper = paper
        this.articleList = articleList
        // 计算缩放值
        scaleByX = width.toFloat() / this.paper.width
        scaleByY = height.toFloat() / this.paper.height
        // 将点集合变成矩形集合，为以后判断是否触摸时使用
        for (article: Article in this.articleList) {
            if (article.rectList == null) {
                article.rectList = SplitRect.Split2Rect(article.pointList.clone() as LinkedList<Point>?)
            }
        }
    }

    // 遮罩图片及其画布
    private lateinit var cacheBitmap: Bitmap
    private lateinit var cacheCanvas: Canvas

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
        initShadeBitmap()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 绘制遮罩图片
        canvas?.drawBitmap(cacheBitmap, 0f, 0f, paint)
    }

    private var velocityTracker: VelocityTracker? = null
    private var lastTouchX: Float = 0.0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        acquireVelocityTracker(event!!)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
//                Log.i(TAG, "ACTION_DOWN")
                lastTouchX = touchX!!
                // 找到触摸点所在的文章
                val touchArticleIndex = getTouchArticleIndex(touchX, event.y)
                if (touchArticleIndex != curTouchArticleIndex) {
                    curTouchArticleIndex = touchArticleIndex
                    if (touchArticleIndex != -1)
                        drawShade(touchArticleIndex)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.computeCurrentVelocity(1000)
//                Log.i(TAG, "ACTION_MOVE v:${velocityTracker?.yVelocity!!}")
                val dx = touchX!! - lastTouchX
                lastTouchX = touchX
                // 滑动冲突
                if (abs(velocityTracker?.yVelocity!!) > scaledMaximumFlingVelocity
                        && abs(dx) < scaledTouchSlop) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false
                }
                val touchArticleIndex = getTouchArticleIndex(touchX, event.y)
                if (touchArticleIndex != curTouchArticleIndex) {
                    curTouchArticleIndex = touchArticleIndex
                    if (touchArticleIndex != -1)
                        drawShade(touchArticleIndex)
                }
            }
            MotionEvent.ACTION_UP -> {
//                Log.i(TAG, "ACTION_UP")
                releaseVelocityTracker()
                lastTouchX = touchX!!
                recycleShadeBitmap()
                postInvalidate()
                initShadeBitmap()
                curTouchArticleIndex = -1
            }
            MotionEvent.ACTION_CANCEL -> {
//                Log.i(TAG, "ACTION_CANCEL")
                releaseVelocityTracker()
                lastTouchX = touchX!!
                recycleShadeBitmap()
                postInvalidate()
                initShadeBitmap()
                curTouchArticleIndex = -1
            }
        }
        return true
    }

    // 回收遮罩层图片资源
    private fun recycleShadeBitmap() {
        if (!cacheBitmap.isRecycled)
            cacheBitmap.recycle()
    }

    // 初始化遮罩层图片资源
    private fun initShadeBitmap() {
        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        cacheCanvas = Canvas(cacheBitmap)
    }

    private fun drawShade(touchArticleIndex: Int) {
        recycleShadeBitmap()
        initShadeBitmap()
        // 绘制轨迹到bitmap上
        val path = Path()
        val article = articleList[touchArticleIndex]
        val startPoint = article.pointList[0]
        path.moveTo(startPoint.x * scaleByX, startPoint.y * scaleByY)
        for (i in 1 until article.pointList.size) {
            val pathPoint = article.pointList[i]
            path.lineTo(pathPoint.x * scaleByX, pathPoint.y * scaleByY)
        }
        path.close()
        cacheCanvas.drawPath(path, paint)
        // 将bitmap绘制到屏幕上
        invalidate()
    }

    // 判断触摸点是否在文章块内, 返回文章在队列中的位置，未找到返回-1
    private fun getTouchArticleIndex(touchX: Float, touchY: Float): Int {
        // 将触摸点坐标转换为报纸坐标
        val paperTouchX = touchX / scaleByX
        val paperTouchY = touchY / scaleByY
        // 判断是否在文章内
        for (article: Article in articleList) {
            for (rect: Rect in article.rectList!!) {
                if (paperTouchY > rect.top && paperTouchY < rect.bottom && paperTouchX > rect.left && paperTouchX < rect.right)
                    return articleList.indexOf(article)
            }
        }
        return -1
    }

    // 获取velocityTracker
    private fun acquireVelocityTracker(event: MotionEvent) {
        if (null == velocityTracker)
            velocityTracker = VelocityTracker.obtain()
        velocityTracker!!.addMovement(event)
    }

    // 释放velocityTracker
    private fun releaseVelocityTracker() {
        if (null != velocityTracker) {
            velocityTracker?.clear()
            velocityTracker?.recycle()
            velocityTracker = null
        }
    }

    companion object {
        private const val TAG = "ShadeView"
    }
}
