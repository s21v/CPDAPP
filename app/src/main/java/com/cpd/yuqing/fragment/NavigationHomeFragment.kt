package com.cpd.yuqing.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import com.cpd.yuqing.R

/**
 * 主页Fragment,包括一个FrameLayout和一个底部的栏目栏
 * Created by s21v on 2017/8/2.
 */
class NavigationHomeFragment : BaseFragment(), View.OnClickListener {
    //记录当前显示的Fragment类型
    private var currentFragmentTag: String

    init {
        currentFragmentTag = CHANNEL_NEWS_TAG
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home -> {
                Log.i(TAG, "click home")
                if (currentFragmentTag != CHANNEL_NEWS_TAG) {
                    hideAndShowFragment(currentFragmentTag, CHANNEL_NEWS_TAG)
                }
            }
            R.id.location -> {
                Log.i(TAG, "click location")
                if (currentFragmentTag != CHANNEL_LOCATION_TAG) {
                    hideAndShowFragment(currentFragmentTag, CHANNEL_LOCATION_TAG)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        val homeTv = rootView.findViewById<TextView>(R.id.home)
        homeTv.setOnClickListener(this)
        val locationTv = rootView.findViewById<TextView>(R.id.location)
        locationTv.setOnClickListener(this)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //设置toolbar
        initActionBar()
        //初始化fragment
        if (savedInstanceState != null)
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag", CHANNEL_NEWS_TAG)
        val fragmentManager = activity.supportFragmentManager
        var initFragment = fragmentManager.findFragmentByTag(currentFragmentTag)
        if (initFragment == null) {
            initFragment = when (currentFragmentTag) {
                CHANNEL_NEWS_TAG -> HomeNewsFragment()
                CHANNEL_LOCATION_TAG -> HomeLocationFragment()
                else -> null
            }
            fragmentManager.beginTransaction().replace(R.id.mainFragmentContent, initFragment, currentFragmentTag).commit()
        }
    }

    private fun hideAndShowFragment(hideFragmentTag: String, showFragmentTag: String) {
        val fragmentManager = activity.supportFragmentManager
        val hideFragment = fragmentManager.findFragmentByTag(hideFragmentTag)
        var showFragment = fragmentManager.findFragmentByTag(showFragmentTag)
        if (showFragment == null) {
            showFragment = when (showFragmentTag) {
                CHANNEL_NEWS_TAG -> HomeNewsFragment()
                CHANNEL_LOCATION_TAG -> HomeLocationFragment()
                else -> null
            }
            if (showFragment != null) {
                fragmentManager.beginTransaction()
                        .hide(hideFragment)
                        .add(R.id.mainFragmentContent, showFragment, showFragmentTag)
                        .commit()
                currentFragmentTag = showFragmentTag
            }
        } else {
            fragmentManager.beginTransaction()
                    .hide(hideFragment)
                    .show(showFragment)
                    .commit()
            currentFragmentTag = showFragmentTag
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("currentFragmentTag", currentFragmentTag)
    }

    companion object {
        val TAG = NavigationHomeFragment::class.java.simpleName!!
        val CHANNEL_NEWS_TAG = HomeNewsFragment::class.java.simpleName!!
        val CHANNEL_LOCATION_TAG = HomeLocationFragment::class.java.simpleName!!
    }
}