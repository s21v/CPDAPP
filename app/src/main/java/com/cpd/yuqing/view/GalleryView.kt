package com.cpd.yuqing.view

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.util.DensityUtils
import com.cpd.yuqing.util.GlideApp

/**
 * Created by s21v on 2017/7/3.
 */
class GalleryView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    private var textSize: Float = 0f
    private var barBackgroundColor: Int = 0
    private var viewPage: ViewPager? = null
    private var textView: TextView? = null
    private var textColor: Int = 0
    private var radioGroup: RadioGroup? = null
    var data: ArrayList<News>? = null
    private val newsOnClickListener: OnNewsClickListener

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.galleryView)
        textSize = typeArray.getDimension(R.styleable.galleryView_textSize, 10f)
        barBackgroundColor = typeArray.getColor(R.styleable.galleryView_barBackground, 0)
        textColor = typeArray.getColor(R.styleable.galleryView_textColor, 0)
        typeArray.recycle()
        newsOnClickListener = OnNewsClickListener(context)
    }

    fun addChildView() {
        //添加viewPage
        viewPage = ViewPager(context)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(viewPage, layoutParams)
        viewPage?.adapter = MyPagerAdapter()
        viewPage?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                radioGroup?.clearCheck()
                val child = radioGroup?.getChildAt(position) as RadioButton
                child.isChecked = true
                textView!!.text = data!![position].homePageTitle
            }
        })
        //添加标题栏
        val linearLayout = LinearLayout(context)
        linearLayout.setBackgroundColor(barBackgroundColor)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val bottomBarLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        bottomBarLayoutParams.addRule(ALIGN_PARENT_BOTTOM)
        addView(linearLayout, bottomBarLayoutParams)
        //添加标题
        textView = TextView(context)
        textView?.text = data!![0].homePageTitle    //初始化
        textView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textView!!.setTextColor(textColor)
        textView!!.paint.isFakeBoldText = true
        textView!!.setPadding(0,4,0,4)
        val textViewLayoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT)
        textViewLayoutParams.weight = 1f
        linearLayout.addView(textView, textViewLayoutParams)
        //添加单选按钮
        radioGroup = RadioGroup(context)
        radioGroup!!.orientation = RadioGroup.HORIZONTAL
        val radioGroupLayoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        radioGroupLayoutParams.gravity = Gravity.CENTER_VERTICAL
        linearLayout.addView(radioGroup, radioGroupLayoutParams)
        var i = 0
        while (i < data!!.size) {
            val radioButton = RadioButton(context)
            val radioButtonLayoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            radioButtonLayoutParams.leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics).toInt()
            radioButtonLayoutParams.rightMargin = radioButtonLayoutParams.leftMargin
            radioButton.layoutParams = radioButtonLayoutParams
            radioButton.buttonDrawable = context.resources.getDrawable(R.drawable.radiobutton_dot, null)
            radioButton.background = null
            radioButton.setOnClickListener(RadioButtonOnClickListener(i))
            radioGroup!!.addView(radioButton)
            i++
        }
        val radioButton = radioGroup!!.getChildAt(0) as RadioButton
        radioButton.isChecked = true
    }

    inner class RadioButtonOnClickListener(private val position: Int) : OnClickListener{
        override fun onClick(v: View?) {
            textView?.text = data!![position].homePageTitle
            viewPage?.setCurrentItem(position, true)
        }
    }

    inner class MyPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val imageView = ImageView(context)
            imageView.layoutParams = ViewPager.LayoutParams()
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            GlideApp.with(context).load(data!![position].picUrls.split(" ")[0]).into(imageView)
            imageView.setOnClickListener { newsOnClickListener.onClick(data!![position]) }
            container?.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object` as View?)
        }

        override fun getPageTitle(position: Int): CharSequence = data!![position].homePageTitle

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

        override fun getCount(): Int = data!!.size
    }
}