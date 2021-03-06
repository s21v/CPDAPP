package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.VideoHomeRecyclerViewAdapter
import com.cpd.yuqing.db.vo.video.Channel
import kotlinx.android.synthetic.main.fragment_home_video.*

/**
 * 视频栏目首页
 * Created by s21v on 2018/3/7.
 */
class VideoHomeFragment : Fragment() {
    private lateinit var channels: ArrayList<Channel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channels = if (null != savedInstanceState)
            savedInstanceState.getParcelableArrayList("videoChannels")
        else
            arguments.getParcelableArrayList("videoChannels")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_video, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val llm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        newsList.layoutManager = llm
        newsList.adapter = VideoHomeRecyclerViewAdapter(context, channels)
        newsList.setHasFixedSize(true)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArrayList("videoChannels", channels)
    }

    companion object {
        private const val TAG = "VideoHomeFragment"

        fun getInstance(channels: ArrayList<Channel>): VideoHomeFragment {
            val videoHomeFragment = VideoHomeFragment()
            val args = Bundle()
            args.putParcelableArrayList("videoChannels", channels)
            videoHomeFragment.arguments = args
            return videoHomeFragment
        }
    }
}