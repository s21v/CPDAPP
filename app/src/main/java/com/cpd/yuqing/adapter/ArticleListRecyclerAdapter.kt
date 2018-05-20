package com.cpd.yuqing.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article

/**
 * 文章列表适配器
 * Created by s21v on 2018/5/18.
 */
class ArticleListRecyclerAdapter(private val articleList: ArrayList<Article>, val context: Context) : RecyclerView.Adapter<ArticleListRecyclerAdapter.ArticleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(context)
        val root = inflater.inflate(R.layout.papertitle_layout, parent, false) as TextView
        root.gravity = Gravity.START
        root.textSize = 16f
        val lp = root.layoutParams as ViewGroup.MarginLayoutParams
        lp.bottomMargin = 2
        return ArticleViewHolder(root)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder?, position: Int) {
        holder?.tv!!.text = "${position + 1}：${articleList[position].title}"
    }

    inner class ArticleViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)
}