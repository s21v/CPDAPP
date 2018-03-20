package com.cpd.yuqing.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import com.cpd.yuqing.db.vo.video.Channel
import com.cpd.yuqing.fragment.VideoHomeFragment

/**
 * 视频栏目首页的ViewPagerAdapter
 * Created by s21v on 2018/3/7.
 */
class VideoViewPagerAdapter(fm: FragmentManager, private val videoChannels: ArrayList<Channel>): FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if (0 == position)
            return VideoHomeFragment.getInstance(videoChannels)
        else
            //todo...
            return VideoHomeFragment.getInstance(videoChannels)
    }

    override fun getCount(): Int = 1//videoChannels.size + 1

    override fun getPageTitle(position: Int): CharSequence {
        return if (0 == position) "首页"
            else videoChannels[position - 1].name!!
    }
}