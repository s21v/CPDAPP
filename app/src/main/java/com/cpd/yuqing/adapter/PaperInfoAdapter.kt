package com.cpd.yuqing.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.PaperInfoDateBinding
import com.cpd.yuqing.databinding.PaperInfoItemBinding
import com.cpd.yuqing.db.vo.szb.Paper
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

/**
 * 数字报首页的RecyclerView的adapter
 * Created by s21v on 2018/5/7.
 */
class PaperInfoAdapter(private val context: Context, papers: ArrayList<Paper>,
                       val onPaperClickListerner: PaperClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val paperDates: ArrayList<String> = arrayListOf()
    private val paperInfoList: ArrayList<List<Paper>> = arrayListOf()

    init {
        if (papers.size != 0) {
            val map = papers.groupBy { simpleDateFormat.format(it.date) }
            paperDates.addAll(map.keys)
            paperInfoList.addAll(map.values)
        }
    }


    fun addMoreData(papers: ArrayList<Paper>) {
        val footerPosition = getFootPosition()
        val map = papers.groupBy { simpleDateFormat.format(it.date) }
        paperDates.addAll(map.keys)
        paperInfoList.addAll(map.values)
        notifyItemRangeInserted(footerPosition, map.keys.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val layoutInflater = LayoutInflater.from(context)
        return when (viewType) {
            TYPE_ITEM_DATE -> {
                val paperInfoDateBinding = DataBindingUtil
                        .inflate<PaperInfoDateBinding>(layoutInflater, R.layout.paper_info_date, parent, false)
                PaperDateVH(paperInfoDateBinding)
            }
            TYPE_ITEM_LINE -> {
                val paperInfoItemBinding = DataBindingUtil
                        .inflate<PaperInfoItemBinding>(layoutInflater, R.layout.paper_info_item, parent, false)
                PaperItemLineVH(paperInfoItemBinding)
            }
            TYPE_FOOTER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.footer_loadmore, parent, false)
                return FootViewHolder(view)
            }
            else -> null
        }
    }

    override fun getItemCount(): Int {
        return if (paperDates.size == 0) 0
        else {
            var paperLineCount = 0
            for (itemList: List<Paper> in paperInfoList) {
                paperLineCount += Math.round(itemList.size / 2f)
            }
            paperDates.size + paperLineCount + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (itemCount > 0) {
            if (position == itemCount - 1)
                return TYPE_FOOTER
            else {
                var curLineIndex = -1
                for (itemList: List<Paper> in paperInfoList) {
                    curLineIndex++
                    if (curLineIndex == position)
                        return TYPE_ITEM_DATE
                    else {
                        curLineIndex += Math.round(itemList.size / 2f)
                        if (position <= curLineIndex)
                            return TYPE_ITEM_LINE
                    }
                }
            }
        }
        return -1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            val flag: Boolean = try {
                payloads!![0] as Boolean
            } catch (e: Exception) {
                false
            }
            (holder as FootViewHolder).init(flag)
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder != null) {
            if (holder is PaperDateVH) {
                holder.dataBinding.setVariable(BR.date, getDateByPosition(position))
            } else if (holder is PaperItemLineVH) {
                val binding = holder.dataBinding as PaperInfoItemBinding
                val subPaperList: List<Paper>? = getSubPaperListByPosition(position)
                if (subPaperList != null && subPaperList.isNotEmpty()) {
                    if (subPaperList.size == 1)
                        binding.paperItem1.visibility = View.INVISIBLE
                    else
                        binding.paperItem1.visibility = View.VISIBLE
                    val al = ArrayList<Paper>(subPaperList)
                    binding.setVariable(BR.papers, al)
                    binding.setVariable(BR.clickListener, onPaperClickListerner)
                }
            }
        }
    }

    // 根据位置获得日期信息
    private fun getDateByPosition(position: Int): String {
        var curLine = -1
        for (i in 0 until paperInfoList.size) {
            curLine++
            if (position == curLine)
                return paperDates[i]
            else
                curLine += Math.round(paperInfoList[i].size / 2f)
        }
        return ""
    }

    // 根据位置获得一对数字报信息
    private fun getSubPaperListByPosition(position: Int): List<Paper>? {
        var curLine = -1
        for (i in 0 until paperInfoList.size) {
            val paperItemLineCount = Math.round(paperInfoList[i].size / 2f)
            curLine += (1 + paperItemLineCount)
            if (position > curLine)
                continue
            else {
                val def = curLine - position
                val startIndex = (paperItemLineCount - def - 1) * 2
                return if (startIndex + 2 <= paperInfoList[i].size)
                    paperInfoList[i].subList(startIndex, startIndex + 2)
                else
                    paperInfoList[i].subList(startIndex, startIndex + 1)
            }
        }
        return null
    }

    //获得foot元素的position
    fun getFootPosition(): Int = itemCount - 1

    inner class PaperItemLineVH(var dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    inner class PaperDateVH(var dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    interface PaperClickListener {
        fun onPaperClick(view: View, paper: Paper)
    }

    companion object {
        private const val TYPE_ITEM_DATE = 1
        private const val TYPE_ITEM_LINE = 2
        private const val TYPE_FOOTER = 3
    }
}