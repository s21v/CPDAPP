package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.fragment_home_paper.*

/**
 * 电子版首页
 * Created by s21v on 2018/5/4.
 */
class HomePaperFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_paper, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.setupWithViewPager(viewPage, false)
        viewPage.adapter = PaperViewPagerAdapter(childFragmentManager)
    }

    inner class PaperViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val paper = arrayOf("人民公安报", "交通周刊", "消防周刊")
        private val type = arrayOf("szb", "jtzk", "xfzk")

        override fun getItem(position: Int): Fragment {
            return PaperListFragment.getInstance(type[position])
        }

        override fun getCount(): Int {
            return paper.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return paper[position]
        }
    }
}