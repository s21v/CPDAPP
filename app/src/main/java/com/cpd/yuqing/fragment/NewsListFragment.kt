package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.NewsRecyclerViewAdapter
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.db.vo.Channel
import com.cpd.yuqing.util.GlideApp
import com.cpd.yuqing.util.NetUtils
import com.cpd.yuqing.util.OkHttpUtils
import com.cpd.yuqing.view.SampleLineItemDecoration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.news_channel_layout.*
import okhttp3.*
import java.io.IOException
import kotlin.collections.ArrayList

/**
 * Created by s21v on 2017/6/12.
 */
class NewsListFragment : Fragment() {
    private var channel: Channel? = null
    //分页加载每页的新闻数量
    private val pageSize = 20
    //当前页面
    private var currentPage = 1
    private var isLoading = false

    companion object {
        val TAG = NewsListFragment::class.java.simpleName!!
        fun getInstance(args: Bundle): NewsListFragment {
            val fragment = NewsListFragment()
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
        //下拉刷新
        swipeRefresh.setOnRefreshListener {
            //下拉刷新
            //监听下拉刷新事件
            loading(1, object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Toast.makeText(context, "新闻下载出错", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val status = response!!.header("status")
                    if ("success" == status) {
                        val dataList = Gson().fromJson<ArrayList<News>>(response.body()!!.string(),
                                object : TypeToken<ArrayList<News>>() {}.type)
                        activity.runOnUiThread {
                            (newsList.adapter as NewsRecyclerViewAdapter).refreshData(dataList)
                            currentPage = 1
                        }
                    } else {
                        activity.runOnUiThread {
                            Toast.makeText(context, "新闻解析出错", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
            swipeRefresh.isRefreshing = false
        }
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        newsList.layoutManager = linearLayoutManager
        newsList.adapter = NewsRecyclerViewAdapter(context, null)
        newsList.setHasFixedSize(false)
        newsList.addItemDecoration(SampleLineItemDecoration(activity,
                R.color.line_divider, SampleLineItemDecoration.VERTICAL_LIST, 1, true))
        newsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val llm = recyclerView!!.layoutManager as LinearLayoutManager
                val lastCompletelyVisibleItemPosition = llm.findLastCompletelyVisibleItemPosition()
                val lastVisibleItemPosition = llm.findLastVisibleItemPosition()
                val adapter = recyclerView.adapter as NewsRecyclerViewAdapter
                val footViewPosition = adapter.getFootPosition()
                if (footViewPosition != 0)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING, RecyclerView.SCROLL_STATE_SETTLING -> {
                            GlideApp.with(context).pauseRequests()  //滚动时停止加载图片
                            if (lastVisibleItemPosition == footViewPosition) {
                                if (lastCompletelyVisibleItemPosition != footViewPosition) {
                                    adapter.notifyItemChanged(lastVisibleItemPosition, false)
                                }
                            }
                        }
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            GlideApp.with(context).resumeRequests() //滚动停止后加载图片
                            if (lastVisibleItemPosition == footViewPosition) {
                                if (lastCompletelyVisibleItemPosition == footViewPosition) {    //底部视图完全显示
                                    if (!isLoading) {
                                        isLoading = true
                                        adapter.notifyItemChanged(lastVisibleItemPosition, true)
                                        //加载更多新闻
                                        object : Thread() {
                                            override fun run() {
                                                loading(currentPage + 1, object : Callback {
                                                    override fun onFailure(call: Call?, e: IOException?) {
                                                        Toast.makeText(context, "新闻下载出错", Toast.LENGTH_SHORT).show()
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
                                                            Toast.makeText(context, "新闻解析出错", Toast.LENGTH_SHORT).show()
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
        //初始化加载
        loading(currentPage, InitNewsCallback())
    }

    //分页加载新闻
    fun loading(page: Int, callback: Callback) {
        val formBody = FormBody.Builder().add("CID", channel!!.id)
                .add("limitNum", pageSize.toString())
                .add("page", page.toString())
                .build()
        val request = Request.Builder().url(NetUtils.NewsCommonUrl).post(formBody).build()
        OkHttpUtils.getOkHttpUtilInstance(activity)!!.httpConnection(request, callback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("channel", channel)
    }

    inner class InitNewsCallback : Callback {
        override fun onFailure(call: Call?, e: IOException?) {
            Log.i(TAG, "新闻下载失败")
            activity.runOnUiThread {
                newsList.adapter.notifyItemChanged(0, arrayOf(currentPage, InitNewsCallback(), this@NewsListFragment::loading))
            }
        }

        override fun onResponse(call: Call?, response: Response?) {
            val status = response!!.header("status")
            if ("success" == status) {
                val dataList = Gson().fromJson<ArrayList<News>>(response.body()!!.string(),
                        object : TypeToken<ArrayList<News>>() {}.type)
                activity.runOnUiThread {
                    (newsList.adapter as NewsRecyclerViewAdapter).refreshData(dataList)
                }
            } else {
                Log.i(TAG, "新闻下载失败")
                activity.runOnUiThread {
                    newsList.adapter.notifyItemChanged(0, arrayOf(currentPage, InitNewsCallback(), this@NewsListFragment::loading))
                }
            }
        }
    }
}