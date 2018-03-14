package com.cpd.yuqing.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.databinding.News0picLayoutBinding
import com.cpd.yuqing.databinding.News1picLayoutBinding
import com.cpd.yuqing.util.GlideApp
import com.cpd.yuqing.view.GalleryView
import com.cpd.yuqing.view.OnNewsClickListener
import okhttp3.Callback


/**
 * Created by s21v on 2017/11/22.
 */
class NewsRecyclerViewAdapter(val context: Context, dataList: ArrayList<News>?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var galleryData: ArrayList<News> = arrayListOf()
    private var listData: ArrayList<News> = arrayListOf()
    private val newsClickListener: OnNewsClickListener

    companion object {
        private const val noPicViewType = 1
        private const val hasPicViewType = 2
        private const val footViewType = 3
        private const val headerViewType = 4
        private const val emptyViewType = 5
        private const val galleryMaxSize = 3  //画廊控件的最大容量
    }

    init {
        if (dataList != null)
            refreshData(dataList)
        newsClickListener = OnNewsClickListener(context)
    }

    //刷新新闻
    fun refreshData(newData: ArrayList<News>) {
        listData.clear()
        galleryData.clear()
        var i = 0
        while (i < newData.size) {
            if (newData[i].picUrls.isNotEmpty() && galleryData.size<galleryMaxSize) {
                galleryData.add(newData[i])
            } else {
                listData.add(newData[i])
            }
            i++
        }
        notifyDataSetChanged()
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
        return if (listData.isEmpty())  //没有数据，显示等待视图
            1
        else {
            if (galleryData.isEmpty())
                listData.size + 1  //没有画廊
            else
                listData.size + 2  //有画廊
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if (listData.isNotEmpty())  //更新FootView中的文字
            if ((galleryData.isNotEmpty() && position == listData.size+1)
                    || (galleryData.isEmpty() && position == listData.size)) {
                val flag: Boolean = try {
                    payloads!![0] as Boolean
                }catch (e: Exception) {
                    false
                }
                (holder as FootViewHolder).init(flag)
            } else
                super.onBindViewHolder(holder, position, payloads)
        else {  //变换empty页面 从waiting到reload
            if (position == 0 && payloads!!.isNotEmpty()) {
                (holder as EmptyViewHolder).showReloadPage()
                val args = payloads[0] as Array<*>
                @Suppress("UNCHECKED_CAST")
                val function = args[2] as (page: Int, callback: Callback) -> Unit
                val arg1 = args[0] as Int
                val arg2 = args[1] as Callback
                holder.reloadPage.setOnClickListener {
                    function(arg1, arg2)
                    holder.showWaiting()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (listData.isNotEmpty())
            if (galleryData.isNotEmpty() && position > 0 && position <= listData.size) {
                val news = listData[position - 1]
                when (holder) {
                    is NoPicViewHolder -> {
                        holder.viewDataBinding.setVariable(BR.news, news)
                        holder.viewDataBinding.executePendingBindings()
                        holder.viewDataBinding.root.setOnClickListener { newsClickListener.onClick(news) }
                    }
                    is HasPicViewHolder -> {
                        holder.viewDataBinding.setVariable(BR.news, news)
                        holder.viewDataBinding.executePendingBindings()
                        GlideApp.with(context).load(news.picUrls.split(" ")[0]).into(holder.viewDataBinding.image1)
                        holder.viewDataBinding.root.setOnClickListener { newsClickListener.onClick(news) }
                    }
                }
            } else if (galleryData.isEmpty() && position<listData.size) {
                val news = listData[position]
                when (holder) {
                    is NoPicViewHolder -> {
                        holder.viewDataBinding.setVariable(BR.news, news)
                        holder.viewDataBinding.executePendingBindings()
                        holder.viewDataBinding.root.setOnClickListener { newsClickListener.onClick(news) }
                    }
                    is HasPicViewHolder -> {
                        holder.viewDataBinding.setVariable(BR.news, news)
                        holder.viewDataBinding.executePendingBindings()
                        GlideApp.with(context).load(news.picUrls.split(" ")[0]).into(holder.viewDataBinding.image1)
                        holder.viewDataBinding.root.setOnClickListener { newsClickListener.onClick(news) }
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
            emptyViewType -> {
                val view = LayoutInflater.from(context).inflate(R.layout.wait_page_layout, parent, false)
                return EmptyViewHolder(view)
            }
        }
        return null
    }

    override fun getItemViewType(position: Int): Int{
        return if (listData.isEmpty())
            emptyViewType
        else {
            if (galleryData.isEmpty()) {    //没有画廊
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

    class EmptyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val waitingPage: RelativeLayout = view.findViewById(R.id.waitingPage)
        val reloadPage: ImageButton = view.findViewById(R.id.reloadPage)

        fun showReloadPage() {
            waitingPage.visibility = View.GONE
            reloadPage.visibility = View.VISIBLE
        }

        fun showWaiting() {
            waitingPage.visibility = View.VISIBLE
            reloadPage.visibility = View.GONE
        }
    }
}