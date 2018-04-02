package com.cpd.yuqing.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.VideoListActivity
import com.cpd.yuqing.adapter.VideoListAdapter
import com.cpd.yuqing.db.vo.video.Channel
import com.cpd.yuqing.db.vo.video.News
import com.cpd.yuqing.retrofitInterface.IVideoNewsApi
import com.cpd.yuqing.util.RetrofitUtils
import kotlinx.android.synthetic.main.fragment_video_list.*
import kotlinx.android.synthetic.main.wait_page_layout.*
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
                        Log.i("onCompleted", "size:${data.size}")
                        list.adapter = VideoListAdapter(context, data)
                        empty.visibility = View.GONE
                        list.visibility = View.VISIBLE
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
        fun getInstance(channel: Channel): VideoListFragment{
            val fragment = VideoListFragment()
            val bundle = Bundle()
            bundle.putParcelable("channel", channel)
            fragment.arguments = bundle
            return fragment
        }
    }
}