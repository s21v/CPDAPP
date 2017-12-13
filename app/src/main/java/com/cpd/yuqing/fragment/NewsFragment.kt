package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.NewsRecyclerViewAdapter
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.db.vo.Channel
import com.cpd.yuqing.util.GlideApp
import com.cpd.yuqing.util.OkHttpUtils
import com.cpd.yuqing.util.Url_IP_Utils
import com.cpd.yuqing.view.SampleLineItemDecoration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.news_channel_layout.*
import okhttp3.*
import java.io.IOException
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.wait_page_layout.*

/**
 * Created by s21v on 2017/6/12.
 */
class NewsFragment : Fragment() {
    private var channel: Channel? = null
    //分页加载每页的新闻数量
    private val pageSize = 20
    //当前页面
    private var currentPage = 1
    private var isLoading = false

    companion object {
        val TAG = NewsFragment::class.java.simpleName
        fun getInstance(args: Bundle): NewsFragment {
            val fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            channel = savedInstanceState.getParcelable("channel")
        } else {
            if (arguments != null) {
                channel = arguments.getParcelable("channel")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.news_channel_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (swipeRefresh.visibility == View.INVISIBLE)
            waitingPage.visibility = View.VISIBLE
        swipeRefresh.setOnRefreshListener { //下拉刷新
            //监听下拉刷新事件
            loading(1, object : Callback{
                override fun onFailure(call: Call?, e: IOException?) {
                    //todo:网络错误
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val status = response!!.header("status")
                    if ("success" == status) {
                        val dataList = Gson().fromJson<ArrayList<News>>(response.body()!!.string(),
                                object : TypeToken<ArrayList<News>>() {}.type)
                        activity.runOnUiThread {
                            (newsList.adapter as NewsRecyclerViewAdapter).refreshData(dataList)
                            newsList.adapter.notifyDataSetChanged()
                            currentPage = 1
                        }
                    } else{
                        //todo:刷新失败
                    }
                }
            })
            swipeRefresh.isRefreshing = false
        }
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        newsList.layoutManager = linearLayoutManager
        newsList.setHasFixedSize(false)
        newsList.addItemDecoration(SampleLineItemDecoration(activity,
                android.R.color.darker_gray, SampleLineItemDecoration.VERTICAL_LIST, 1))
        reloadPage.setOnClickListener { //初始化加载
            loading(currentPage, InitNewsCallback())
        }
    }

    override fun onResume() {
        super.onResume()
        newsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLayoutManager = recyclerView!!.layoutManager as LinearLayoutManager
                val lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                val adapter = recyclerView.adapter as NewsRecyclerViewAdapter
                val footViewPosition = adapter.getFootPosition()
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING, RecyclerView.SCROLL_STATE_SETTLING -> {
                        GlideApp.with(context).pauseRequests()
                        if (lastVisibleItemPosition == footViewPosition) {
                            if (lastCompletelyVisibleItemPosition != footViewPosition) {
                                adapter.notifyItemChanged(lastVisibleItemPosition, false)
                            }
                        }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        GlideApp.with(context).resumeRequests()
                        if (lastVisibleItemPosition == footViewPosition) {
                            if (lastCompletelyVisibleItemPosition == footViewPosition) {    //底部视图完全显示
                                if (!isLoading) {
                                    isLoading = true
                                    adapter.notifyItemChanged(lastVisibleItemPosition, true)
                                    //加载更多新闻
                                    object : Thread() {
                                        override fun run() {
                                            loading(currentPage+1, object : Callback {
                                                override fun onFailure(call: Call?, e: IOException?) {
                                                    //todo:网络错误
                                                }

                                                override fun onResponse(call: Call?, response: Response?) {
                                                    val status = response!!.header("status")
                                                    if ("success" == status) {
                                                        val dataList = Gson().fromJson<ArrayList<News>>(response.body()!!.string(),
                                                                object : TypeToken<ArrayList<News>>() {}.type)
                                                        currentPage++
                                                        activity.runOnUiThread {
                                                            adapter.addMoreNews(dataList)
                                                            isLoading = false
                                                        }
                                                    } else {
                                                        //todo 加载更多新闻失败
                                                    }
                                                }
                                            })
                                        }
                                    }.start()
                                }
                            }
                        }
                    }
                }
            }
        })
        if (swipeRefresh.visibility == View.INVISIBLE) {    //初始化加载
            loading(currentPage, InitNewsCallback())
        }
    }

    //分页加载新闻
    fun loading(page: Int, callback: Callback) {
        val formBody = FormBody.Builder().add("CID", channel!!.id)
                .add("limitNum", pageSize.toString())
                .add("page", page.toString())
                .build()
        val request = Request.Builder().url(Url_IP_Utils.NewsCommonUrl).post(formBody).build()
        OkHttpUtils.getOkHttpUtilInstance(activity)!!.httpConnection(request, callback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("channel", channel)
    }

    inner class InitNewsCallback : Callback{
        override fun onFailure(call: Call?, e: IOException?) {
            waitingPage.visibility = View.GONE
            reloadPage.visibility = View.VISIBLE
        }

        override fun onResponse(call: Call?, response: Response?) {
            val status = response!!.header("status")
            if ("success" == status) {
                val dataList = Gson().fromJson<ArrayList<News>>(response.body()!!.string(),
                        object : TypeToken<ArrayList<News>>() {}.type)
                activity.runOnUiThread {
                    newsList.adapter = NewsRecyclerViewAdapter(activity, dataList!!)
                    waitingPage.visibility = View.GONE
                    if (reloadPage.visibility == View.VISIBLE)
                        reloadPage.visibility = View.GONE
                    swipeRefresh.visibility = View.VISIBLE
                }
            } else {
                //todo: 首次下载新闻失败
                waitingPage.visibility = View.GONE
                reloadPage.visibility = View.VISIBLE
            }
        }
    }
}