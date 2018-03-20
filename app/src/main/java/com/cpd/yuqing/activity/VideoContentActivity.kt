package com.cpd.yuqing.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.video.News
import com.cpd.yuqing.fragment.VideoContentFragment
import kotlinx.android.synthetic.main.activity_video_content.*

/**
 * 视频内容页Activity
 * Created by s21v on 2018/3/20.
 */
class VideoContentActivity : AppCompatActivity() {
    private lateinit var mNews: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_content)
        mNews = if (savedInstanceState != null)
            savedInstanceState.getParcelable("news")
        else
            intent.getParcelableExtra("news")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentVideoContentContainer, VideoContentFragment.getInstance(mNews))
                .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("news", mNews)
    }
}