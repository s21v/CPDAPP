package com.cpd.yuqing.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.MediaController
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.FragmentVedioContentBinding
import com.cpd.yuqing.db.vo.video.News
import kotlinx.android.synthetic.main.fragment_vedio_content.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * 视频新闻内容页
 * Created by s21v on 2018/3/19.
 */
class VideoContentFragment : Fragment() {
    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        news = if (savedInstanceState != null)
            savedInstanceState.getParcelable("news")
        else
            arguments.getParcelable("news")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentVedioContentBinding>(inflater, R.layout.fragment_vedio_content, container, false)
        dataBinding.setVariable(BR.news, news)
        dataBinding.executePendingBindings()
        return dataBinding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mMediaController = MediaController(context)
        videoView.setVideoPath(news.videoUrl)
        videoView.setMediaController(mMediaController)
        mMediaController.setMediaPlayer(videoView)
        //设置WebView
        contentWebView.isVerticalFadingEdgeEnabled = false
        contentWebView.setBackgroundColor(0)
        //设置webView,支持JavaScript
        val webSettings = contentWebView.settings
        webSettings.javaScriptEnabled = true
        //添加本地css文件
        val css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/videoContent.css\">"
        val baseUrl = "http://v.cpd.com.cn/"
        news.content = css + news.content
        //设置webview 显示内容 loadDataWithBaseURL()可以避免中文乱码
        contentWebView.loadDataWithBaseURL(baseUrl, news.content, "text/html", "utf-8", null)
        //加载本地js文件
        contentWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String): Unit = try {
                val sb = StringBuilder()
                context.assets.open("content.js")
                        .buffered()
                        .reader(Charset.forName("utf-8"))
                        .use {
                            it.forEachLine {
                                sb.append(it)
                            }
                        }
                view.loadUrl("javascript:" + sb.toString())
                //切换白天\夜间模式
                val contentSettings = context.getSharedPreferences("contentSetting", MODE_PRIVATE)
                val isNightMode = contentSettings.getBoolean("isNightMode", false)
                if (!isNightMode) {
                } else view.loadUrl(String.format("javascript:switchNightMode(%b)", isNightMode))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    companion object {
        fun getInstance(news: News): VideoContentFragment {
            val bundle = Bundle()
            bundle.putParcelable("news", news)
            val fragment = VideoContentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}