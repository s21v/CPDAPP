package com.cpd.yuqing.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.cpd.yuqing.data.Channel
import com.cpd.yuqing.fragment.NewsFragment

/**
 * Created by s21v on 2017/6/12.
 */
class NewsViewPagerAdapter(fm: FragmentManager, channels: ArrayList<Channel>) : FragmentStatePagerAdapter(fm){
    val channels: ArrayList<Channel> = channels

    override fun getItem(p0: Int): Fragment {
        var args = Bundle()
        args.putString("channelName", channels[p0].name)
        return NewsFragment.getInstance(args)
    }

    override fun getCount(): Int {
        return channels.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return channels[position].name
    }
}
