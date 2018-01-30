package com.cpd.yuqing.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import com.cpd.yuqing.CpdnewsApplication
import com.cpd.yuqing.R
import com.cpd.yuqing.db.dao.NewsDao
import com.cpd.yuqing.fragment.NavigationHomeFragment
import com.cpd.yuqing.fragment.NavigationFavoriteFragment
import com.cpd.yuqing.util.NetUtils
import com.cpd.yuqing.util.OkHttpUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //解决navigationView的item图标显示为灰色
        nav_view.itemIconTintList = null
        //处理和NavigationView相关的操作
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                //查看收藏
                R.id.myfavorite -> {
                    title = menuItem.title
                    //获得数据库中的数据
                    val dao = NewsDao(this)
                    val favoriteData = dao.selectAll(CpdnewsApplication.getCurrentUser().id, NewsDao.TYPE_FAVORITE)
                    var fragment: NavigationFavoriteFragment? = supportFragmentManager.findFragmentByTag("favorite") as NavigationFavoriteFragment?
                    if (fragment != null){
                        Log.i(TAG, "fragment 存在")
                        fragment!!.data = favoriteData
                        supportFragmentManager.beginTransaction().show(fragment)
                    } else {
                        Log.i(TAG, "fragment 不存在")
                        fragment = NavigationFavoriteFragment.newInstance(favoriteData)
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.curNavigationFragmentContent, fragment, "favorite")
                                .addToBackStack("favorite")
                                .commit()
                    }
                }
                //查看点赞
                R.id.mythumbUp -> {}
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.START)
            true
        }
        //初始化导航栏
        nav_view.setCheckedItem(R.id.home)
        //初始的首页新闻栏目
        supportFragmentManager.beginTransaction()
                .replace(R.id.curNavigationFragmentContent, NavigationHomeFragment(), "homeFragment")
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
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
        OkHttpUtils.getOkHttpUtilInstance(this)!!.httpConnection(request4Version, object:Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?) {
                val channelVersion = Integer.valueOf(response!!.body()!!.string())
                if (channelVersion > localChannelVersion) {
                    sharedPreferences.edit()
                            .putBoolean("needUpdate", true)
                            .putInt("localChannelListVersion", channelVersion)
                            .commit()
                } else
                    sharedPreferences.edit().putBoolean("needUpdate", false).commit()
            }
        })
        super.onStop()
    }

    fun startBezierViewAnimator() {
        bezierView.startAnimator()
    }

    fun stopBezierViewAnimator() {
        bezierView.stopAnimator()
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
