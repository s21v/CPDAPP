package com.cpd.yuqing.activity

import android.content.Context
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import com.cpd.yuqing.R
import com.cpd.yuqing.fragment.NavigationHomeFragment
import com.cpd.yuqing.fragment.NavigationFavoriteFragment
import com.cpd.yuqing.util.NetUtils
import com.cpd.yuqing.util.OkHttpUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var currentFragmentTag = NAV_HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null)
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag")
        //解决navigationView的item图标显示为灰色
        nav_view.itemIconTintList = null
        //处理和NavigationView相关的操作
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
            //主页
                R.id.home -> {
                    if (currentFragmentTag != NAV_HOME) {
                        val hideFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
                        var showFragment: NavigationHomeFragment? = supportFragmentManager.findFragmentByTag(NAV_HOME) as NavigationHomeFragment?
                        if (showFragment != null) {
                            supportFragmentManager.beginTransaction().hide(hideFragment).show(showFragment).commit()
                        } else {
                            showFragment = createFragment(NAV_HOME) as NavigationHomeFragment?
                            supportFragmentManager.beginTransaction()
                                    .hide(hideFragment)
                                    .add(R.id.curNavigationFragmentContent, showFragment, NAV_HOME)
                                    .commit()
                        }
                        menuItem.isChecked = true
                        currentFragmentTag = NAV_HOME
                    }
                }
            //查看收藏
                R.id.myfavorite -> {
                    if (currentFragmentTag != NAV_FAVORITE) {
                        //待隐藏的fragment
                        val hideFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
                        //待显示的fragment
                        var showFragment: NavigationFavoriteFragment? = supportFragmentManager.findFragmentByTag(NAV_FAVORITE) as NavigationFavoriteFragment?
                        if (showFragment != null) {
                            supportFragmentManager.beginTransaction().hide(hideFragment).show(showFragment).commit()
                        } else {
                            showFragment = createFragment(NAV_FAVORITE) as NavigationFavoriteFragment?
                            supportFragmentManager.beginTransaction()
                                    .hide(hideFragment)
                                    .add(R.id.curNavigationFragmentContent, showFragment, NAV_FAVORITE)
                                    .commit()
                        }
                        menuItem.isChecked = true
                        currentFragmentTag = NAV_FAVORITE
                    }
                }
            //查看点赞
                R.id.mythumbUp -> {
                }
            //切换模式
                R.id.switch_night_mode -> {
                    //获取当前UI模式
                    //kotlin 中并没有为位操作提供操作符,而是用and(与) or(或) xor(异或) inv(按位取反) shl(左移) shr(右移) ushr(无符号右移)
                    val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    if (Configuration.UI_MODE_NIGHT_YES == mode) {
                        //关闭夜间模式
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        getSharedPreferences("contentSetting", MODE_PRIVATE).edit()
                                .putBoolean("isNightMode", false).apply()
                    } else {
                        //开启夜间模式
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        getSharedPreferences("contentSetting", MODE_PRIVATE).edit()
                                .putBoolean("isNightMode", true).apply()
                    }
                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
                    //应用新模式
                    recreate()
                }
            }

            drawerLayout.closeDrawer(Gravity.START)
            true
        }
        //初始化导航栏
        nav_view.setCheckedItem(R.id.home)
        //检查当前栏目是否存在
        if (supportFragmentManager.findFragmentByTag(currentFragmentTag) == null) {
            Log.i(TAG, "当前栏目不存在")
            val fragment = createFragment(currentFragmentTag)
            //初始的首页新闻栏目
            supportFragmentManager.beginTransaction()
                    .add(R.id.curNavigationFragmentContent, fragment!!, currentFragmentTag)
                    .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("currentFragmentTag", currentFragmentTag)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
        //activity中只处理打开关闭侧边栏的主导航项
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(Gravity.START))
                    drawerLayout.closeDrawer(Gravity.START)
                else
                    drawerLayout.openDrawer(Gravity.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        //检查栏目是否更新
        //获取本地栏目信息
        val sharedPreferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE)
        val localChannelVersion = sharedPreferences.getInt("localChannelListVersion", 0)
        //获取远程栏目信息
        val formBody = FormBody.Builder().add("m", "version").build()
        val request4Version = Request.Builder().url(NetUtils.ChannelCommonUrl).post(formBody).build()
        OkHttpUtils.getOkHttpUtilInstance(this)!!.httpConnection(request4Version, object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?) {
                val channelVersion = Integer.valueOf(response!!.body()!!.string())
                if (channelVersion > localChannelVersion) {
                    sharedPreferences.edit()
                            .putBoolean("needUpdate", true)
                            .putInt("localChannelListVersion", channelVersion)
                            .apply()
                } else
                    sharedPreferences.edit().putBoolean("needUpdate", false).apply()
            }
        })
        super.onStop()
    }

    private fun createFragment(fragmentTag: String): Fragment? {
        return when (fragmentTag) {
            NAV_HOME -> NavigationHomeFragment()
            NAV_FAVORITE -> NavigationFavoriteFragment.newInstance("我的收藏")
            else -> null
        }
    }

    fun startBezierViewAnimator() {
        bezierView.startAnimator()
    }

    fun stopBezierViewAnimator() {
        bezierView.stopAnimator()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val NAV_HOME = "nav_home"
        private const val NAV_FAVORITE = "nav_favorite"
    }
}
