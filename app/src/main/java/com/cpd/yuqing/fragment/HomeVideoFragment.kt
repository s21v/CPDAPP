package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.VideoHomeRecyclerViewAdapter
import com.cpd.yuqing.db.vo.video.Channel
import com.cpd.yuqing.retrofitInterface.IVideoChannelApi
import com.cpd.yuqing.util.RetrofitUtils
import kotlinx.android.synthetic.main.fragment_home_video.*
import retrofit2.Retrofit
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * 首页底部导航栏的视频栏目页
 * Created by s21v on 2018/3/6.
 */
class HomeVideoFragment : Fragment() {
    private lateinit var retrofit: Retrofit
    private var videoChannels: ArrayList<Channel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrofit = RetrofitUtils.getInstance(context)!!.retrofitInstance
        if (null != savedInstanceState)
            videoChannels = savedInstanceState.getParcelableArrayList<Channel>("channelList")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home_video, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (null == videoChannels)
            refleshChannel(null, null)
        else {
            val llm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            newsList.layoutManager = llm
            newsList.adapter = VideoHomeRecyclerViewAdapter(context, videoChannels!!)
            newsList.setHasFixedSize(true)
        }
        swipeRefresh.setOnRefreshListener {
            refleshChannel({
                swipeRefresh.isRefreshing = false
            }, { swipeRefresh.isRefreshing = false })
        }
    }

    // 滚动数据
    fun scrollToFirstPosition() {
        newsList.scrollToPosition(0)
    }

    // 刷新数据
    private fun refleshChannel(onCompletedFun: (() -> Unit)?, onErrorFun: (() -> Unit)?) {
        val videoChannelApi = retrofit.create(IVideoChannelApi::class.java)
        //下载栏目
        videoChannelApi.getVideoChannelOrSubject(GET_ALL_CHANNEL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<Channel>>() {
                    override fun onNext(t: ArrayList<Channel>?) {
                        videoChannels = t
                        Log.i(TAG, "onNext() channels :${videoChannels?.size ?: "栏目返回为null"}")
                    }

                    override fun onCompleted() {
                        Log.i(TAG, "onCompleted()")
                        val llm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        newsList.layoutManager = llm
                        newsList.adapter = VideoHomeRecyclerViewAdapter(context, videoChannels!!)
                        newsList.setHasFixedSize(true)
                        if (onCompletedFun != null)
                            onCompletedFun()
                    }

                    override fun onError(e: Throwable?) {
                        Log.i(TAG, "onError(),${e?.message ?: "null"}")
                        // todo 错误处理页面
                        if (onErrorFun != null)
                            onErrorFun()
                    }
                })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (null != videoChannels)
            outState!!.putParcelableArrayList("channelList", videoChannels)
    }

    companion object {
        private const val GET_ALL_CHANNEL: String = "getAllChannel"
        private const val TAG: String = "HomeVideoFragment"
    }
}