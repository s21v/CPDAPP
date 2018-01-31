package com.cpd.yuqing.fragment

import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_header.*

/**
 * fragment的基础类、主要负责设置toolbar、navigationView
 * Created by s21v on 2018/1/27.
 */
abstract class BaseFragment : Fragment() {
    protected var mActionBarTitle: CharSequence? = null

    protected fun initActionBar() {
        //设置toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val supportActionBar = (activity as AppCompatActivity).supportActionBar
        //设置home图标显示箭头按钮、可点击
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setHomeButtonEnabled(true)
        if (mActionBarTitle != null)
            supportActionBar.title = mActionBarTitle
        //创建ActionBarDrawerToggle,添加监听
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawerLayout)
        val drawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open, R.string.cloase) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                (activity as MainActivity).startBezierViewAnimator()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                (activity as MainActivity).stopBezierViewAnimator()
            }
        }
        drawerToggle.syncState()
        drawerLayout.addDrawerListener(drawerToggle)
    }
}