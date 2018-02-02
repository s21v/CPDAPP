package com.cpd.yuqing.adapter

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.FragmentNewsFavoriteBinding
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.view.OnNewsClickListener

class FavoriteNewsRecyclerViewAdapter(private val mValues: ArrayList<News>,
                                      private val mClickListener: OnNewsClickListener?,
                                      private val mLongClickListener: View.OnLongClickListener?,
                                      private val context: Context) :
        RecyclerView.Adapter<FavoriteNewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = (context as Activity).layoutInflater
        val dataBind = DataBindingUtil.inflate<FragmentNewsFavoriteBinding>(layoutInflater, R.layout.fragment_news_favorite, parent, false)
        return ViewHolder(dataBind)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBind.setVariable(BR.news, mValues[position])
        val root = holder.dataBind.root
        //设置监听器
        root.setOnClickListener {
            mClickListener?.onClick(mValues[position])
        }
        root.setOnLongClickListener(mLongClickListener)
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val dataBind: FragmentNewsFavoriteBinding) : RecyclerView.ViewHolder(dataBind.root)
}
