package com.cpd.yuqing.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.cpd.yuqing.R
import com.cpd.yuqing.fragment.LocationFragment
import com.cpd.yuqing.fragment.NewsMainFragment
import com.cpd.yuqing.util.NetUtils
import com.cpd.yuqing.util.OkHttpUtils
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //设置显示条件为true
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        //创建ActionBarDrawerToggle,添加监听
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.cloase)
        drawerToggle.syncState()
        drawerLayout.addDrawerListener(drawerToggle)
        //解决navigationView的item图标显示为灰色
        nav_view.itemIconTintList = null
        nav_view.setNavigationItemSelectedListener { menuItem ->
            Snackbar.make(drawerLayout, menuItem.title, Snackbar.LENGTH_LONG).show()
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.START)
            true
        }
        //初始的栏目
        supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContent, NewsMainFragment(), "homeFragment")
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

    //点击下方按钮栏中的按钮来改变栏目
    fun changeColumn(view: View) {
        when(view.id) {
            R.id.home -> {
                val fragment:Fragment? = supportFragmentManager.findFragmentByTag("homeFragment")
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragmentContent, fragment?: NewsMainFragment(), "homeFragment")
                        .commit()
            }
            R.id.location -> {
                val fragment:Fragment? = supportFragmentManager.findFragmentByTag("LocationFragment")
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragmentContent, fragment?:LocationFragment(), "LocationFragment")
                        .commit()
            }
        }
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
