package com.cpd.yuqing.activity

import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.util.Xml
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.db.vo.szb.Paper
import com.cpd.yuqing.fragment.PaperArticleListFragment
import com.cpd.yuqing.fragment.PaperContentFragment
import com.cpd.yuqing.fragment.PaperDetailFragment
import com.cpd.yuqing.util.NetUtils.PAPERURL
import com.cpd.yuqing.util.OkHttpUtils
import com.cpd.yuqing.view.ShadeView
import kotlinx.android.synthetic.main.fragment_header.*
import kotlinx.android.synthetic.main.activity_paper_detail.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.ArrayList

/**
 * 显示报纸版面
 * Created by s21v on 2018/5/14.
 */
class PaperActivity : AppCompatActivity(), ShadeView.OnArticleSelectedListener, ShadeView.OnArticleClickedListener {
    override fun onArticleClick(article: Article) {
        if (currentFragmentTag == TAG_MAIN)
        // 转到文章内容页面
            hideAndShowFragment(TAG_MAIN, TAG_CONTENT, article)
        else {
            if (drawerLayout.isDrawerOpen(Gravity.END))
                drawerLayout.closeDrawer(Gravity.END)
            // 切换新闻
            val fragment = supportFragmentManager.findFragmentByTag(TAG_CONTENT) as PaperContentFragment
            fragment.changeArticle(article)
        }
    }

    override fun onSelectedFinish() {
        // 隐藏弹出框
        if (mPopupWindow != null && mPopupWindow!!.isShowing)
            mPopupWindow!!.dismiss()
    }

    private var mPopupWindow: PopupWindow? = null

    override fun onArticleSelected(article: Article) {
        // 弹出标题
        if (mPopupWindow != null && mPopupWindow!!.isShowing)
            mPopupWindow!!.dismiss()
        val titleTv = LayoutInflater.from(this).inflate(R.layout.papertitle_layout, null) as TextView
        titleTv.text = article.title
        mPopupWindow = PopupWindow(titleTv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // 获得状态栏高度
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        mPopupWindow!!.showAtLocation(mainFragmentContainer, Gravity.TOP, 0, statusBarHeight + supportActionBar!!.height)
    }

    private lateinit var curPaper: Paper
    private var curArticleList: ArrayList<Article>? = null
    private var currentFragmentTag = TAG_MAIN

    private fun hideAndShowFragment(hideFragmentTag: String, showFragmentTag: String, article: Article?) {
        val fragmentManager = supportFragmentManager
        val hideFragment = fragmentManager.findFragmentByTag(hideFragmentTag)
        var showFragment = fragmentManager.findFragmentByTag(showFragmentTag)
        if (showFragment == null) {
            showFragment = when (showFragmentTag) {
                TAG_MAIN -> PaperDetailFragment.getInstance(curPaper, curArticleList!!)
                TAG_CONTENT -> PaperContentFragment.getInstance(article!!)
                else -> null
            }
            if (showFragment != null) {
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .hide(hideFragment)
                        .add(R.id.mainFragmentContainer, showFragment, showFragmentTag)
                        .commit()
                currentFragmentTag = showFragmentTag
            }
        } else {
            if (showFragment is PaperContentFragment) {
                showFragment.changeArticle(article!!)
            }
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(hideFragment)
                    .show(showFragment)
                    .commit()
            currentFragmentTag = showFragmentTag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper_detail)
        if (savedInstanceState != null) {
            curPaper = savedInstanceState.getParcelable("curPaper")
            curArticleList = savedInstanceState.getParcelableArrayList<Article>("curArticleList")
        } else {
            curPaper = intent.getParcelableExtra("paper")
        }
        initToolBar()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
    }

    override fun onStart() {
        super.onStart()
        if (curArticleList == null) {
            curArticleList = arrayListOf()
            downLoadPaperXml()
        } else {
            initData()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("curPaper", curPaper)
        if (curArticleList != null && curArticleList!!.isNotEmpty())
            outState?.putParcelableArrayList("curArticleList", curArticleList)
    }

    private fun getImageUrl(imgPath: String): String {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        return (PAPERURL + curPaper.type + "/" + simpleDateFormat.format(curPaper.date)
                + "/" + imgPath)
    }

    private fun initToolBar() {
        toolbar.title = when (curPaper.type) {
            "szb" -> "人民公安报"
            "jtzk" -> "交通周刊"
            "xfzk" -> "消防周刊"
            else -> ""
        }
        val simpleDateFormat = SimpleDateFormat("yyyy年M月dd日")
        val paperDateStr = simpleDateFormat.format(curPaper.date)
        val paperDesc = curPaper.getDesc()
        toolbar.subtitle = "$paperDateStr\t$paperDesc"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun downLoadPaperXml() {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        val xmlUrl = (PAPERURL + curPaper.type + "/" + simpleDateFormat.format(curPaper.date)
                + "/" + curPaper.xmlPath)
        val request = Request.Builder().url(xmlUrl).build()
        OkHttpUtils.getOkHttpUtilInstance(applicationContext)!!.httpConnection(request, object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Toast.makeText(this@PaperActivity, "数据下载出错，请检查网络", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                val xmlInputStream = response?.body()!!.byteStream()
                parserPaperXml(xmlInputStream)
            }
        })
    }

    private fun parserPaperXml(inputStream: InputStream) {
        val xpp = Xml.newPullParser()
        xpp.setInput(inputStream, "utf-8")
        var eventType = xpp.eventType
        var tmpArticle: Article? = null
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                when (xpp.name) {
                    "PageInfo" -> {
                        // 读取版面属性
                        curPaper.width = xpp.getAttributeValue(null, "Width").toInt()
                        curPaper.height = xpp.getAttributeValue(null, "Height").toInt()
                    }
                    "Article" -> {
                        tmpArticle = Article()
                    }
                    "IntroTitle" -> {
                        val introTitle = xpp.nextText()
                        tmpArticle?.introTitle = introTitle
                    }
                    "Title" -> {
                        val title = xpp.nextText()
                        tmpArticle?.title = title
                    }
                    "SubTitle" -> {
                        val subTitle = xpp.nextText()
                        tmpArticle?.subTitle = subTitle
                    }
                    "Content" -> {
                        val content = xpp.nextText()
                        tmpArticle?.content = content
                    }
                    "Image" -> {
                        val imgPath = getImageUrl(xpp.getAttributeValue(null, "Name"))
                        val imgContent = xpp.nextText()
                        tmpArticle?.imgList!![imgPath] = imgContent
                    }
                    "Point" -> {
                        val point = Point(xpp.getAttributeValue(null, "X").toInt(),
                                xpp.getAttributeValue(null, "Y").toInt())
                        tmpArticle?.pointList!!.add(point)
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG)
                if (xpp.name == "Article") {
                    curArticleList!!.add(tmpArticle!!)
                }
            eventType = xpp.next()
        }
        initData()
        inputStream.close()
    }

    private fun initData() {
        // 加载报纸版面fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, PaperDetailFragment.getInstance(curPaper, curArticleList!!), TAG_MAIN)
                .commit()
        currentFragmentTag = TAG_MAIN
        // 加载抽屉fragment
        val drawerFragment = PaperArticleListFragment.getInstance(curPaper, curArticleList!!)
        supportFragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, drawerFragment, TAG_DRAWER).commit()
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View?) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
                // 抽屉导航关闭时开启底层滑动
                if (currentFragmentTag == TAG_MAIN) {
                    val mainFragment = supportFragmentManager.findFragmentByTag(TAG_MAIN) as PaperDetailFragment
                    mainFragment.openTouch()
                }
            }

            override fun onDrawerOpened(drawerView: View?) {
                // 抽屉导航打开时应禁止底层的滑动
                if (currentFragmentTag == TAG_MAIN) {
                    val mainFragment = supportFragmentManager.findFragmentByTag(TAG_MAIN) as PaperDetailFragment
                    mainFragment.closeTouch()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        MenuInflater(this).inflate(R.menu.paper_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.articleList) {
            // 打开侧边栏
            drawerLayout.openDrawer(Gravity.END)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END)
            return true
        } else if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END)
            return
        }
        if (currentFragmentTag == TAG_CONTENT) {
            hideAndShowFragment(TAG_CONTENT, TAG_MAIN, null)
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val TAG_DRAWER = "drawerFragment"
        private const val TAG_MAIN = "mainDetailFragment"
        private const val TAG_CONTENT = "mainContentFragment"
    }
}