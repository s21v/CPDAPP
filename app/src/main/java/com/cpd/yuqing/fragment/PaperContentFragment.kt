package com.cpd.yuqing.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.util.NetUtils
import kotlinx.android.synthetic.main.fragment_paper_content.*
import java.io.IOException
import java.nio.charset.Charset

/**
 * 报纸新闻内容
 * Created by s21v on 2018/5/22.
 */
class PaperContentFragment : Fragment() {
    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        article = if (savedInstanceState != null)
            savedInstanceState.getParcelable("article")
        else
            arguments.getParcelable("article")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_paper_content, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        // 配置viewPager
        id_viewpager.pageMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4f, context.resources.displayMetrics).toInt()

        // ------------------------ WebView ----------------------
        //不显示滚动条
        contentWebView.isVerticalScrollBarEnabled = false
        //设置透明背景
        contentWebView.setBackgroundColor(0)
        //设置webView,支持JavaScript
        val webSettings = contentWebView.settings
        webSettings.javaScriptEnabled = true
    }

    override fun onStart() {
        super.onStart()
        //加载本地js文件
        contentWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String): Unit = try {
                val sb = StringBuilder()
                context.assets.open("paperContent.js")   //放在之前的生命周期方法中会报错 context is null
                        .buffered()
                        .reader(Charset.forName("utf-8"))
                        .use {
                            it.forEachLine {
                                sb.append(it)
                            }
                        }
                view.loadUrl("javascript:" + sb.toString())
                //切换白天\夜间模式
                val contentSettings = context.getSharedPreferences("contentSetting", Context.MODE_PRIVATE)
                val isNightMode = contentSettings.getBoolean("isNightMode", false)
                if (!isNightMode) {
                } else view.loadUrl(String.format("javascript:switchNightMode(%b)", isNightMode))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("article", article)
    }

    private fun initView() {
        // 设置引题
        if (article.introTitle.isNullOrEmpty())
            introTitleTv.visibility = View.GONE
        else {
            introTitleTv.visibility = View.VISIBLE
            introTitleTv.text = Html.fromHtml(article.introTitle)
        }
        // 设置标题
        titleTv.text = Html.fromHtml(article.title)
        // 设置副题
        if (article.subTitle.isNullOrEmpty())
            subTitleTv.visibility = View.GONE
        else {
            subTitleTv.visibility = View.VISIBLE
            subTitleTv.text = Html.fromHtml(article.subTitle)
        }
        // 设置图片列表
        val imgHashMap = article.imgList
        if (imgHashMap.isEmpty()) {
            gallery.visibility = View.GONE
        } else {
            gallery.visibility = View.VISIBLE
            val imgListArray = arrayListOf<ImageView>()
            // 设置ImageView
            for (i in imgHashMap.keys) {
                val imageView = ImageView(context)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(context).load(i).into(imageView)
                imgListArray.add(imageView)
            }
            if (imgListArray.size > 1) {
                //设置viewpager首尾重复项,达到循环的效果
                val firstImageView = ImageView(context)
                firstImageView.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(context).load(imgHashMap.keys.last()).into(firstImageView)
                imgListArray.add(0, firstImageView)
                val endImageView = ImageView(context)
                endImageView.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(context).load(imgHashMap.keys.first()).into(endImageView)
                imgListArray.add(endImageView)
            } else if (imgListArray.size == 1) {
                // 检查图片说明是否存在, 不存在则隐藏文本栏
                if (imgHashMap.values.first().isEmpty())
                    title_tv.visibility = View.GONE
            }
            id_viewpager.adapter = object : PagerAdapter() {
                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    container.addView(imgListArray[position])
                    return imgListArray[position]
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any?) {
                    container.removeView(imgListArray[position])
                }

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view === `object`
                }

                override fun getCount(): Int {
                    return imgListArray.size
                }
            }
            if (imgListArray.size > 1) {
                id_viewpager.clearOnPageChangeListeners()
                id_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    var currentPosition = id_viewpager.currentItem
                    override fun onPageScrollStateChanged(state: Int) {
                        if (ViewPager.SCROLL_STATE_IDLE != state)
                            return
                        if (currentPosition == 0)
                            id_viewpager.setCurrentItem(imgListArray.size - 2, false)
                        else if (currentPosition == imgListArray.size - 1)
                            id_viewpager.setCurrentItem(1, false)
                    }

                    /**
                     * 在非第一页与最后一页时:
                     * 滑动到下一页，position为当前页位置；滑动到上一页：position为当前页-1;
                     * positionOffset 滑动到下一页，[0,1)区间上变化；滑动到上一页：(1,0]区间上变化;
                     * positionOffsetPixels这个和positionOffset很像：滑动到下一页，[0,宽度)区间上变化；滑动到上一页：(宽度,0]区间上变化
                     *
                     * 第一页时：滑动到上一页position=0 ，其他基本为0
                     *
                     * 最后一页时：滑动到下一页position为当前页位置，其他两个参数为0
                     */
                    /**
                     * 在非第一页与最后一页时:
                     * 滑动到下一页，position为当前页位置；滑动到上一页：position为当前页-1;
                     * positionOffset 滑动到下一页，[0,1)区间上变化；滑动到上一页：(1,0]区间上变化;
                     * positionOffsetPixels这个和positionOffset很像：滑动到下一页，[0,宽度)区间上变化；滑动到上一页：(宽度,0]区间上变化
                     *
                     * 第一页时：滑动到上一页position=0 ，其他基本为0
                     *
                     * 最后一页时：滑动到下一页position为当前页位置，其他两个参数为0
                     */
                    /**
                     * 在非第一页与最后一页时:
                     * 滑动到下一页，position为当前页位置；滑动到上一页：position为当前页-1;
                     * positionOffset 滑动到下一页，[0,1)区间上变化；滑动到上一页：(1,0]区间上变化;
                     * positionOffsetPixels这个和positionOffset很像：滑动到下一页，[0,宽度)区间上变化；滑动到上一页：(宽度,0]区间上变化
                     *
                     * 第一页时：滑动到上一页position=0 ，其他基本为0
                     *
                     * 最后一页时：滑动到下一页position为当前页位置，其他两个参数为0
                     */
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                    override fun onPageSelected(position: Int) {
                        currentPosition = position
                        when (currentPosition) {
                            0 -> title_tv.text = Html.fromHtml(article.imgList.values.last())
                            imgListArray.size - 1 -> title_tv.text = Html.fromHtml(article.imgList.values.first())
                            else -> title_tv.text = Html.fromHtml(article.imgList.values.elementAt(currentPosition - 1))
                        }
                    }
                })
                id_viewpager.offscreenPageLimit = imgListArray.size
                id_viewpager.currentItem = imgListArray.size / 2
            }
        }
        // 设置webView内容
        contentWebView.loadDataWithBaseURL(NetUtils.PAPERURL, CSS + article.content, "text/html", "utf-8", null)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.i(TAG, "onHiddenChanged()")
        if (hidden)
        // 清空webview中的内容、否则WebView的loadDataWithBaseURL重复加载的时候会有问题
            contentWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
    }

    fun changeArticle(article: Article) {
        if (this.article != article) {
            // 清空webview中的内容、否则WebView的loadDataWithBaseURL重复加载的时候会有问题
            contentWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            this.article = article
            initView()
        }
    }

    companion object {
        fun getInstance(article: Article): PaperContentFragment {
            val args = Bundle()
            args.putParcelable("article", article)
            val fragment = PaperContentFragment()
            fragment.arguments = args
            return fragment
        }

        //添加本地css文件
        private const val CSS = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/paperContent.css\">"
        private const val TAG = "PaperContentFragment"
    }
}