package com.cpd.yuqing.activity

import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Xml
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.db.vo.szb.Paper
import com.cpd.yuqing.fragment.PaperArticleListFragment
import com.cpd.yuqing.util.NetUtils.PAPERURL
import com.cpd.yuqing.util.OkHttpUtils
import com.cpd.yuqing.view.ShadeView
import kotlinx.android.synthetic.main.fragment_header.*
import kotlinx.android.synthetic.main.activity_paper_detail_1.*
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
class PaperActivity : AppCompatActivity(), ShadeView.OnArticleSelectedListener {
    override fun onArticleClick(article: Article) {
        // 转换到文章内容页面
        Log.i("PaperActivity", "onArticleClick article:$article")
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
        var statusBarHeight: Int = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        mPopupWindow!!.showAtLocation(shadeView, Gravity.TOP, 0, statusBarHeight + supportActionBar!!.height)
    }

    private lateinit var curPaper: Paper
    private var curArticleList: ArrayList<Article>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper_detail_1)
        if (savedInstanceState != null) {
            curPaper = savedInstanceState.getParcelable("curPaper")
            curArticleList = savedInstanceState.getParcelableArrayList<Article>("curArticleList")
        } else {
            curPaper = intent.getParcelableExtra("paper")
        }
        initToolBar()
        Glide.with(applicationContext).load(getImageUrl()).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
                shadeView.background = resource
            }
        })
        shadeView.setArticleSelectedListener(this)
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

    private fun getImageUrl(): String {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        return (PAPERURL + curPaper.type + "/" + simpleDateFormat.format(curPaper.date)
                + "/" + curPaper.imgPath)
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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?) {
                val xmlInputStream = response?.body()!!.byteStream()
                parserPaperXml(xmlInputStream)
                initData()
                xmlInputStream.close()
            }
        })
    }

    private fun parserPaperXml(xml: InputStream) {
        val xpp = Xml.newPullParser()
        xpp.setInput(xml, "utf-8")
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
                        val imgPath = xpp.getAttributeValue(null, "Name")
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
    }

    private fun initData() {
        shadeView.setData(curPaper, curArticleList!!)
        val drawerFragment = PaperArticleListFragment.getInstance(curPaper, curArticleList!!)
        supportFragmentManager.beginTransaction().replace(R.id.drawerFragmentContainer, drawerFragment).commit()
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View?) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
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
        }
        return super.onOptionsItemSelected(item)
    }
}