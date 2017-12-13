package com.cpd.yuqing.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.data.News
import com.cpd.yuqing.databinding.News0picLayoutBinding
import com.cpd.yuqing.databinding.News1picLayoutBinding
import com.cpd.yuqing.databinding.News2picLayoutBinding
import com.cpd.yuqing.databinding.News3picLayoutBinding

/**
 * Created by s21v on 2017/6/27.
 */
class NewsListRecyclerViewAdapter(val context: Context, var dataList: ArrayList<News>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        when(viewType) {
            0 -> {
                val viewDataBinding: News0picLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.news_0pic_layout, parent, false)
                val viewHolder = ZeroPicViewHolder(viewDataBinding)
                return viewHolder
            }
            1 -> {
                val viewDataBinding: News1picLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.news_1pic_layout, parent, false)
                val viewHolder = OnePicViewHolder(viewDataBinding)
                return viewHolder
            }
            2 -> {
                val viewDataBinding: News2picLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.news_2pic_layout, parent, false)
                val viewHolder = TwoPicViewHolder(viewDataBinding)
                return viewHolder
            }
            3 -> {
                val viewDataBinding: News3picLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.news_3pic_layout, parent, false)
                val viewHolder = ThreePicViewHolder(viewDataBinding)
                return viewHolder
            } else ->
                return null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val news = dataList[position]
        when (holder) {
            is ZeroPicViewHolder -> {
                holder.viewDataBinding.setVariable(BR.news, news)
                holder.viewDataBinding.executePendingBindings()
            }
            is OnePicViewHolder -> {
                holder.viewDataBinding.setVariable(BR.news, news)
                holder.viewDataBinding.executePendingBindings()
                Glide.with(context).load(news.pictureUrlList!![0]).into(holder.viewDataBinding.image1)
            }
            is TwoPicViewHolder -> {
                holder.viewDataBinding.setVariable(BR.news, news)
                holder.viewDataBinding.executePendingBindings()
                Glide.with(context).load(news.pictureUrlList!![0]).into(holder.viewDataBinding.image1)
                Glide.with(context).load(news.pictureUrlList!![1]).into(holder.viewDataBinding.image2)
            }
            is ThreePicViewHolder -> {
                holder.viewDataBinding.setVariable(BR.news, news)
                holder.viewDataBinding.executePendingBindings()
                Glide.with(context).load(news.pictureUrlList!![0]).into(holder.viewDataBinding.image1)
                Glide.with(context).load(news.pictureUrlList!![1]).into(holder.viewDataBinding.image2)
                Glide.with(context).load(news.pictureUrlList!![2]).into(holder.viewDataBinding.image3)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val size = dataList[position].pictureUrlList!!.size
        return when(size) {
            in 0..3 -> size
            else -> 3
        }
    }

    class ZeroPicViewHolder(var viewDataBinding: News0picLayoutBinding) : RecyclerView.ViewHolder(viewDataBinding.root)

    class OnePicViewHolder(var viewDataBinding: News1picLayoutBinding) : RecyclerView.ViewHolder(viewDataBinding.root)

    class TwoPicViewHolder(var viewDataBinding: News2picLayoutBinding) : RecyclerView.ViewHolder(viewDataBinding.root)

    class ThreePicViewHolder(var viewDataBinding: News3picLayoutBinding) : RecyclerView.ViewHolder(viewDataBinding.root)
}