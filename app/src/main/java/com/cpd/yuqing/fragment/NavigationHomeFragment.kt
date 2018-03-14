package com.cpd.yuqing.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import com.cpd.yuqing.R
import com.cpd.yuqing.view.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * 主页Fragment,包括一个FrameLayout和一个底部的栏目栏
 * Created by s21v on 2017/8/2.
 */
class NavigationHomeFragment : BaseFragment() {
    //记录当前显示的Fragment类型
    private var currentFragmentTag: String

    init {
        currentFragmentTag = CHANNEL_NEWS_TAG
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //设置toolbar
        initActionBar()
        //初始化fragment
        if (savedInstanceState != null)
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag", CHANNEL_NEWS_TAG)
        Log.i(TAG, "onActivityCreated() $currentFragmentTag")
        val fragmentManager = activity.supportFragmentManager
        var initFragment = fragmentManager.findFragmentByTag(currentFragmentTag)
        if (initFragment == null) {
            initFragment = when (currentFragmentTag) {
                CHANNEL_NEWS_TAG -> HomeNewsFragment()
                CHANNEL_LOCATION_TAG -> HomeLocationFragment()
                CHANNEL_VIDEO_TAG -> HomeVideoFragment()
                else -> null
            }
            fragmentManager.beginTransaction().replace(R.id.mainFragmentContent, initFragment, currentFragmentTag).commit()
        }
        //设置底部导航栏点击事件
        bottomNavigation.itemIconTintList = null    //显示图标的原始图片
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottomNavHome -> {
                    if (currentFragmentTag != CHANNEL_NEWS_TAG) {
                        hideAndShowFragment(currentFragmentTag, CHANNEL_NEWS_TAG)
                        true
                    } else
                        false
                }
                R.id.bottomNavLocation -> {
                    if (currentFragmentTag != CHANNEL_LOCATION_TAG) {
                        hideAndShowFragment(currentFragmentTag, CHANNEL_LOCATION_TAG)
                        true
                    } else
                        false
                }
                R.id.bottomNavVideo -> {
                    if (currentFragmentTag != CHANNEL_VIDEO_TAG) {
                        hideAndShowFragment(currentFragmentTag, CHANNEL_VIDEO_TAG)
                        return@setOnNavigationItemSelectedListener true
                    } else
                        return@setOnNavigationItemSelectedListener false
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = when (currentFragmentTag) {
            CHANNEL_NEWS_TAG -> R.id.bottomNavHome
            CHANNEL_LOCATION_TAG -> R.id.bottomNavLocation
            CHANNEL_VIDEO_TAG -> R.id.bottomNavVideo
            else -> 0
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
                CHANNEL_VIDEO_TAG -> HomeVideoFragment()
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
        Log.i(TAG, "onSaveInstanceState() $currentFragmentTag")
        outState!!.putString("currentFragmentTag", currentFragmentTag)
    }

    companion object {
        val TAG = NavigationHomeFragment::class.java.simpleName!!
        val CHANNEL_NEWS_TAG = HomeNewsFragment::class.java.simpleName!!
        val CHANNEL_LOCATION_TAG = HomeLocationFragment::class.java.simpleName!!
        val CHANNEL_VIDEO_TAG = HomeVideoFragment::class.java.simpleName!!
    }
}