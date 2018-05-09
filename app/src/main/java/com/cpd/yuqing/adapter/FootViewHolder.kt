package com.cpd.yuqing.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.cpd.yuqing.R

/**
 * 底部刷新的RecyclerView.ViewHolder，对应的layout_id: R.layout.footer_loadmore
 * Created by s21v on 2018/5/9.
 */
class FootViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.refreshProgressBar)
    private val textView = view.findViewById<TextView>(R.id.refreshText)

    fun init(isNeedLoadShow: Boolean) = if (isNeedLoadShow) {
        progressBar.visibility = View.VISIBLE
        textView.text = "正在加载..."
    } else {
        progressBar.visibility = View.INVISIBLE
        textView.text = "上拉加载更多新闻"
    }
}