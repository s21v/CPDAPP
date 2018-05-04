package com.cpd.yuqing.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.cpd.yuqing.fragment.NewsListFragment
import com.cpd.yuqing.db.vo.Channel

/**
 * Created by s21v on 2017/6/12.
 */
class NewsViewPagerAdapter(fm: FragmentManager, private val channels: ArrayList<Channel>) : FragmentStatePagerAdapter(fm){
    override fun getItem(p0: Int): Fragment {
        val args = Bundle()
        args.putParcelable("channel", channels[p0])
        return NewsListFragment.getInstance(args)
    }

    override fun getCount(): Int = channels.size

    override fun getPageTitle(position: Int): CharSequence {
        return channels[position].name
    }
}
