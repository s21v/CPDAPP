package com.cpd.yuqing.fragment

import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.db.vo.szb.Paper
import kotlinx.android.synthetic.main.fragment_home_paper.*
import org.xmlpull.v1.XmlPullParser
import java.util.*

/**
 * 数字报页面
 * Created by s21v on 2018/4/23.
 */
class HomePaperFragment : Fragment() {
    private lateinit var curPaper: Paper
    private lateinit var curArticleList: ArrayList<Article>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            curPaper = savedInstanceState.getParcelable("curPaper")
            curArticleList = savedInstanceState.getParcelableArrayList<Article>("curArticleList")
        } else {
            curPaper = getPaper()
            curArticleList = arrayListOf()
        }
        parserPaperXml()
//        Log.i(TAG, "curPaper:$curPaper")
//        Log.i(TAG, "curArticleList:$curArticleList")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_paper, container, false)
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart()")
        // 获得宽高、避免为0
        shadeView.post {
            shadeView.setData(curPaper, curArticleList)
        }
        // 除去多余的组件
        if (parentFragment is NavigationHomeFragment)
            (parentFragment as NavigationHomeFragment).hideBar()
    }

    // fragment hide\show 时调用
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.i(TAG, "onHiddenChanged()")
        if (!hidden) {
            // 获得宽高、避免为0
            shadeView.post {
                shadeView.setData(curPaper, curArticleList)
            }
            // 除去多余的组件
            if (parentFragment is NavigationHomeFragment)
                (parentFragment as NavigationHomeFragment).hideBar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("curPaper", curPaper)
        outState?.putParcelableArrayList("curArticleList", curArticleList)
    }

    private fun getPaper(): Paper {
        return Paper("01", "要闻", "szb", Date(), "testszb.xml",
                "paper_01.jpg", false)
    }

    private fun parserPaperXml() {
        val xpp = Xml.newPullParser()
        xpp.setInput(activity.assets.open("testszb.xml"), "utf-8")
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
                    curArticleList.add(tmpArticle!!)
                }
            eventType = xpp.next()
        }
    }

    companion object {
        private const val TAG = "HomePaperFragment"
    }
}