package com.cpd.yuqing.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.activity_search.*

/**
 * Created by s21v on 2017/6/12.
 */
class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        ListView
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(Intent.ACTION_SEARCH.equals(intent?.action)) {
            val query = intent?.extras?.getString(SearchManager.QUERY)
            Toast.makeText(this, query, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchableInfo)
        return super.onCreateOptionsMenu(menu)
    }
}