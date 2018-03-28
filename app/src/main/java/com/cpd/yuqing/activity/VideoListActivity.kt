package com.cpd.yuqing.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.video.Channel
import com.cpd.yuqing.fragment.VideoListFragment
import kotlinx.android.synthetic.main.fragment_header.*

/**
 * 视频新闻的列表页
 * Created by s21v on 2018/3/27.
 */
class VideoListActivity :  AppCompatActivity(){
    private lateinit var channel: Channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channel = if (savedInstanceState != null)
            savedInstanceState.getParcelable("channel")
        else
            intent.getParcelableExtra("channel")
        setContentView(R.layout.activity_video_list)
        //设置actionbar
        toolbar.title = channel.name
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        //设置fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                VideoListFragment.getInstance(channel)).commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("channel", channel)
    }
}