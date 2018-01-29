package com.cpd.yuqing.activity

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import com.cpd.yuqing.R
import com.cpd.yuqing.db.dao.NewsDao
import kotlinx.android.synthetic.main.activity_slider_opration.*
import kotlin.properties.Delegates

class SliderOperationActivity : AppCompatActivity() {
    private var operationType by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider_opration)
        operationType = savedInstanceState?.getInt("operationType") ?:
                intent.getIntExtra("operationType", 1)
        when(operationType) {
            NewsDao.Companion.TYPE_FAVORITE -> {
                toolbar.title = resources.getString(R.string.myfavorite)
                //加载fragment
            }
            NewsDao.Companion.TYPE_THUMBUP -> {
                //操作同上
                toolbar.title = resources.getString(R.string.mythumbup)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        //加载搜索配置
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        val searchMenuItem = menu!!.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchableInfo)
        return super.onCreateOptionsMenu(menu)
    }
}
