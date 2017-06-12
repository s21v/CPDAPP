package com.cpd.yuqing.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by s21v on 2017/6/12.
 */
class NewsViewPagerAdapter : FragmentStatePagerAdapter{
    override fun getItem(p0: Int): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(fm: FragmentManager): super(fm)

    override fun getPageTitle(position: Int): CharSequence {
        return super.getPageTitle(position)
    }
}
