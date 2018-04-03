package com.cpd.yuqing.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.TextView
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.VideoListAdapter
import com.cpd.yuqing.db.vo.video.Channel
import com.cpd.yuqing.db.vo.video.News
import com.cpd.yuqing.retrofitInterface.IVideoNewsApi
import com.cpd.yuqing.util.RetrofitUtils
import kotlinx.android.synthetic.main.footer_loadmore.*
import kotlinx.android.synthetic.main.fragment_video_list.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * fragment:视频列表
 * Created by s21v on 2018/3/28.
 */
class VideoListFragment : ListFragment() {
    private lateinit var channel: Channel
    private lateinit var videoNewApi: IVideoNewsApi
    private var currentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            channel = savedInstanceState.getParcelable("channel")
            currentPage = savedInstanceState.getInt("currentPage")
        } else {
            channel = arguments.getParcelable("channel")
            currentPage = 1
        }
        videoNewApi = RetrofitUtils.getInstance(context)?.retrofitInstance?.create(IVideoNewsApi::class.java)!!
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_video_list, container, false)
        videoNewApi.getNewsByPage(channel.id, currentPage, COUNT_IN_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<News>>() {
                    lateinit var data: ArrayList<News>
                    override fun onNext(t: ArrayList<News>?) {
                        data = t!!
                    }

                    override fun onCompleted() {
                        val footView = inflater?.inflate(R.layout.footer_loadmore, null)
                        val refreshTv = footView?.findViewById<TextView>(R.id.refreshText)
                        val loadProgressBar = footView?.findViewById<ProgressBar>(R.id.refreshProgressBar)
                        list.setOnScrollListener(object : AbsListView.OnScrollListener {
                            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}

                            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                                        list.bottom == list.getChildAt(list.childCount - 1).bottom) {
                                    //更新View
                                    refreshTv?.text = "正在刷新,请稍后..."
                                    loadProgressBar?.visibility = View.VISIBLE
                                    // 执行上拉刷新操作
                                    videoNewApi.getNewsByPage(channel.id, currentPage + 1, COUNT_IN_PAGE)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object : Subscriber<ArrayList<News>>() {
                                                lateinit var data: ArrayList<News>
                                                override fun onNext(t: ArrayList<News>?) {
                                                    data = t!!
                                                }

                                                override fun onCompleted() {
                                                    Log.i("onCompleted", "data size : ${data.size}")
                                                    (listAdapter as VideoListAdapter).data.addAll(data)
                                                    (listAdapter as VideoListAdapter).notifyDataSetChanged()
                                                    currentPage++
                                                    //更新View
                                                    refreshTv?.text = "上拉刷新"
                                                    loadProgressBar?.visibility = View.GONE
                                                }

                                                override fun onError(e: Throwable?) {
                                                    //更新View
                                                    loadProgressBar?.visibility = View.GONE
                                                    refreshTv?.text = "无更多新闻"
                                                    footView?.visibility = View.GONE
                                                }

                                            })
                                }
                            }

                        })
                        list.addFooterView(footView, null, false)
                        listAdapter = VideoListAdapter(context, data)
                        if (list.lastVisiblePosition != data.size - 1)    //对于包含多条目的列表页设置上拉刷新
                            refreshTv?.text = "上拉刷新"
                        else
                            refreshTv?.text = ""
                        empty.visibility = View.GONE
                        swipeRefresh.visibility = View.VISIBLE
                    }

                    override fun onError(e: Throwable?) {
                        Log.i("onError", e?.message)
                    }

                })
        return root
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("channel", channel)
        outState?.putInt("currentPage", currentPage)
    }

    companion object {
        private const val COUNT_IN_PAGE = 20
        fun getInstance(channel: Channel): VideoListFragment {
            val fragment = VideoListFragment()
            val bundle = Bundle()
            bundle.putParcelable("channel", channel)
            fragment.arguments = bundle
            return fragment
        }
    }
}