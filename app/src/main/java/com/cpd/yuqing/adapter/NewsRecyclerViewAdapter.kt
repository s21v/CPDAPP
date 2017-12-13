package com.cpd.yuqing.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.databinding.News0picLayoutBinding
import com.cpd.yuqing.databinding.News1picLayoutBinding
import com.cpd.yuqing.util.GlideApp
import com.cpd.yuqing.view.GalleryView


/**
 * Created by s21v on 2017/11/22.
 */
class NewsRecyclerViewAdapter(val context: Context, dataList: ArrayList<News>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var galleryData: ArrayList<News> = arrayListOf()
    private var listData: ArrayList<News> = arrayListOf()
    private val noPicViewType = 1
    private val hasPicViewType = 2
    private val footViewType = 3
    private val headerViewType = 4
    private val galleryMaxSize = 3
    init {
        refreshData(dataList)
    }

    //刷新新闻
    fun refreshData(newData: ArrayList<News>) {
        var i = 0
        while (i < newData.size) {
            if (newData[i].picUrls.isNotEmpty() && galleryData.size<galleryMaxSize) {
                galleryData.add(newData[i])
            } else {
                listData.add(newData[i])
            }
            i++
        }
    }

    //加载更多新闻
    fun addMoreNews(newData: ArrayList<News>) {
        val footPosition = getFootPosition()
        listData.addAll(newData)
        notifyItemRangeInserted(footPosition, newData.size)
    }

    //获得foot元素的position
    fun getFootPosition(): Int{
        return if (galleryData.isEmpty())
            listData.size
        else
            listData.size + 1
    }

    override fun getItemCount(): Int{
        return if (galleryData.isEmpty())
            listData.size+1  //没有画廊
        else
            listData.size+2  //有画廊
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if ((galleryData.isNotEmpty() && position == listData.size+1) || (galleryData.isEmpty() && position == listData.size)) {
            val flag: Boolean = try {
                payloads!![0] as Boolean
            }catch (e: Exception) {
                false
            }
            (holder as FootViewHolder).init(flag)
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (galleryData.isNotEmpty() && position > 0 && position <= listData.size) {
            val news = listData[position - 1]
            when (holder) {
                is NoPicViewHolder -> {
                    holder.viewDataBinding.setVariable(BR.news, news)
                    holder.viewDataBinding.executePendingBindings()
                }
                is HasPicViewHolder -> {
                    holder.viewDataBinding.setVariable(BR.news, news)
                    holder.viewDataBinding.executePendingBindings()
                    GlideApp.with(context).load(news.picUrls.split(" ")[0]).into(holder.viewDataBinding.image1)
                }
            }
        } else if (galleryData.isEmpty() && position<listData.size) {
            val news = listData[position]
            when (holder) {
                is NoPicViewHolder -> {
                    holder.viewDataBinding.setVariable(BR.news, news)
                    holder.viewDataBinding.executePendingBindings()
                }
                is HasPicViewHolder -> {
                    holder.viewDataBinding.setVariable(BR.news, news)
                    holder.viewDataBinding.executePendingBindings()
                    GlideApp.with(context).load(news.picUrls.split(" ")[0]).into(holder.viewDataBinding.image1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder?{
        when(viewType) {
            noPicViewType -> {
                val noPicDataBinding = DataBindingUtil.inflate<News0picLayoutBinding>(LayoutInflater.from(context),
                        R.layout.news_0pic_layout, parent, false)
                return NoPicViewHolder(noPicDataBinding)
            }
            hasPicViewType -> {
                val hasPicDataBinding = DataBindingUtil.inflate<News1picLayoutBinding>(LayoutInflater.from(parent.context),
                        R.layout.news_1pic_layout, parent, false)
                return HasPicViewHolder(hasPicDataBinding)
            }
            footViewType -> {
                val view = LayoutInflater.from(context).inflate(R.layout.footer_loadmore, parent, false)
                return FootViewHolder(view)
            }
            headerViewType -> {
                val view = LayoutInflater.from(context).inflate(R.layout.header_news_list, parent, false) as GalleryView
                view.data = galleryData
                view.addChildView()
                return HeaderViewHolder(view)
            }
        }
        return null
    }

    override fun getItemViewType(position: Int): Int{
        return if (galleryData.isEmpty()) {    //没有画廊
            when {
                position == listData.size -> footViewType
                listData[position].picUrls.isEmpty() -> noPicViewType
                else -> hasPicViewType
            }
        } else {    //有画廊
            when {
                position == 0 -> headerViewType
                position == listData.size+1 -> footViewType
                listData[position-1].picUrls.isEmpty() -> noPicViewType
                else -> hasPicViewType
            }
        }
    }

    class NoPicViewHolder(var viewDataBinding: News0picLayoutBinding) : RecyclerView.ViewHolder(viewDataBinding.root)

    class HasPicViewHolder(var viewDataBinding: News1picLayoutBinding) : RecyclerView.ViewHolder(viewDataBinding.root)

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

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}