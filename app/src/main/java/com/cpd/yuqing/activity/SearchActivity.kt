package com.cpd.yuqing.activity

import android.animation.*
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.activity_search.*
import java.util.stream.Collectors

/**
 * Created by s21v on 2017/6/12.
 */
class SearchActivity : AppCompatActivity() {
    val searchedKeyword = "searchedKeyWord"
    var sharedPreference: SharedPreferences ?= null
    var keywords: MutableSet<String> ?= null
    var searchView: SearchView ?= null
    var isTagShow:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        //获得保存的关键词
        sharedPreference = getSharedPreferences("search_prefer", Context.MODE_PRIVATE)
        keywords = sharedPreference?.getStringSet(searchedKeyword, null)
        if (keywords == null)
            keywords = HashSet<String>()
        //加载已搜索过的关键词数据
        tagLayout.adapter = searchTagAdapter()
        //清除已搜索的关键词
        clearTagImageButton.setOnClickListener {
            keywords!!.clear()
            tagLayout.adapter?.notifyDataSetChanged()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if(Intent.ACTION_SEARCH == intent.action) {
            val query = intent.extras.getString(SearchManager.QUERY)
            //------------------- 模拟搜索 ------------------
            val resultList = dataList.stream().filter({ it.contains(query) }).collect(Collectors.toList())
            //-------------------------------------------------
            resultListView.adapter = searchResultAdapter(query, resultList)
            if(isTagShow) {
                //使用动画，切换到搜索结果页面
                val alphaOut = ObjectAnimator.ofFloat(listOf(historyTv, tagLayout, clearTagImageButton), "alpha", 1f, 0f)
                val translationXOut_historyTv = ObjectAnimator.ofFloat(historyTv, "translationX", historyTv.translationX, -historyTv.width.toFloat())
                val translationXOut_tagLayout = ObjectAnimator.ofFloat(tagLayout, "translationX", tagLayout.translationX, -tagLayout.width.toFloat())
                val translationXOut_clearTagImageButton = ObjectAnimator.ofFloat(clearTagImageButton, "translationX", clearTagImageButton.translationX, clearTagImageButton.width.toFloat())
                translationXOut_tagLayout.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        keywords?.add(query)
                        tagLayout.adapter?.notifyDataSetChanged()
                    }
                })
                val searched_tag_out = AnimatorSet()
                searched_tag_out.duration = 500
                searched_tag_out.playTogether(listOf(alphaOut, translationXOut_tagLayout, translationXOut_historyTv, translationXOut_clearTagImageButton))

                val alphaIn = ObjectAnimator.ofFloat(resultListView, "alpha", 0f, 1f)
                val translationYIn = ObjectAnimator.ofFloat(resultListView, "translationY", -resultListView.height.toFloat(), 0f)
                val search_result_in = AnimatorSet()
                search_result_in.duration = 1000
                search_result_in.playTogether(listOf(alphaIn, translationYIn))
                search_result_in.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationStart(animation: Animator?) {
                        resultListView.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animation: Animator?) {
                        isTagShow = false
                    }
                })

                val animatorSet = AnimatorSet()
                animatorSet.playSequentially(listOf(searched_tag_out, search_result_in))
                animatorSet.start()
            }
        }
    }

    override fun onPause() {
        //保存搜索过的关键词
        sharedPreference!!.edit().putStringSet(searchedKeyword, keywords).apply()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        //加载搜索配置
        val searchMenuItem = menu.findItem(R.id.search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView = searchMenuItem.actionView as SearchView
        searchView?.setSearchableInfo(searchableInfo)
        searchView?.isIconified = false  //让SearchView默认是展开状态
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{  //设置监听器监听输入
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false    //使用SearchView自己的提交逻辑
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                if(TextUtils.isEmpty(p0)) {
                    //没有输入关键词的话，使用过度动画切换到已搜索的标签页
                    if(!isTagShow) {
                        val alphaIn = ObjectAnimator.ofFloat(listOf(historyTv, tagLayout, clearTagImageButton), "alpha", 0f, 1f)
                        val translationXIn_historyTv = ObjectAnimator.ofFloat(historyTv, "translationX", historyTv.translationX, 0f)
                        val translationXIn_tagLayout = ObjectAnimator.ofFloat(tagLayout, "translationX", tagLayout.translationX, 0f)
                        val translationXIn_clearTagImageButton = ObjectAnimator.ofFloat(clearTagImageButton, "translationX", clearTagImageButton.translationX, clearTagImageButton.translationX-clearTagImageButton.width.toFloat())
                        val searched_tag_in = AnimatorSet()
                        searched_tag_in.duration = 500
                        searched_tag_in.playTogether(listOf(alphaIn, translationXIn_historyTv, translationXIn_tagLayout, translationXIn_clearTagImageButton))
                        searched_tag_in.addListener(object : AnimatorListenerAdapter(){
                            override fun onAnimationStart(animation: Animator?) {
                                resultListView.visibility = View.GONE
                            }
                            override fun onAnimationEnd(animation: Animator?) {
                                isTagShow = true
                            }
                        })

                        val alphaOut = ObjectAnimator.ofFloat(resultListView, "alpha", 1f, 0f)
                        val translationYOut = ObjectAnimator.ofFloat(resultListView, "translationY", resultListView.translationY, -resultListView.height.toFloat())
                        val searchResultOut = AnimatorSet()
                        searchResultOut.duration = 1000
                        searchResultOut.playTogether(listOf(alphaOut, translationYOut))

                        val animatorSet = AnimatorSet()
                        animatorSet.playSequentially(listOf(searchResultOut, searched_tag_in))
                        animatorSet.start()
                    }
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //----------------------------------------- 临时数据 ----------------------------------
    val dataList = listOf("Android App开发入门", "精彩绝伦的Android UI设计", "第一行代码 Android 第二版"
            , "Android基础教程", "Android编程权威指南", "Java网络编程", "Java特种兵", "精通Android", "Android系统软件开发"
            , "疯狂Java讲义", "疯狂Android讲义", "Java核心技术 卷1基础知识")
    //------------------------------------------------------------------------------------------

    //结果列表的适配器
    inner class searchResultAdapter(var keyword: String, var resultList: List<String>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: TextView = if (convertView == null) {
                layoutInflater.inflate(android.R.layout.simple_list_item_1, null, false) as TextView
            } else {
                convertView as TextView
            }
            //给关键词标记颜色
            val currentString = getItem(position) as String
            val start = currentString.indexOf(keyword)
            val end = start+(keyword.length)
            val spannableString = SpannableString(currentString)
            spannableString.setSpan(ForegroundColorSpan(Color.RED), start, end, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            view.text = spannableString
            return view
        }

        override fun getItem(position: Int): Any {
            return resultList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return resultList.size
        }
    }

    //已搜索标签的适配器
    inner class searchTagAdapter : BaseAdapter(){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val textView: TextView
            if(convertView == null) {
                textView = layoutInflater.inflate(R.layout.tag_item, parent, false) as TextView
            } else {
                textView = convertView as TextView
            }
            textView.text = keywords!!.toList()[position]
            //点击标签执行查询
            textView.setOnClickListener { searchView?.setQuery(keywords!!.toList()[position], true) }
            return textView
        }

        override fun getItem(position: Int): Any {
            return keywords!!.toList()[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return keywords?.size?:0
        }
    }
}