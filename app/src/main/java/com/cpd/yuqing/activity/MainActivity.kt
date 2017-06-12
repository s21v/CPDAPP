package com.cpd.yuqing.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.SearchView
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.NewsViewPagerAdapter
import com.cpd.yuqing.data.Channel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view.setNavigationItemSelectedListener { menuItem ->
            Snackbar.make(drawerLayout, menuItem.title, Snackbar.LENGTH_LONG).show()
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.START)
            true }
        tabLayout.setupWithViewPager(viewPage, true)
        ////////////////临时数据
        var list = ArrayList<Channel>()
        for(index in 1..5)
            list.add(Channel("舆情"+index))
        ////////////////
        viewPage.adapter = NewsViewPagerAdapter(supportFragmentManager, list)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.news_opt_menu, menu)
        //设置搜索
//        val menuItem = menu?.findItem(R.id.search)
//        val searchView = menuItem?.actionView as SearchView
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchInfo = searchManager.getSearchableInfo(componentName)
//        searchView?.setSearchableInfo(searchInfo)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(Gravity.START))
                    drawerLayout.closeDrawer(Gravity.START)
                else
                    drawerLayout.openDrawer(Gravity.START)
                return true
            }
            R.id.search -> {
//                startActivity(Intent(this, SearchActivity.class))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
