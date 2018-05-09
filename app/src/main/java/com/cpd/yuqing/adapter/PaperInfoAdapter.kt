package com.cpd.yuqing.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.PaperInfo4itemBinding
import com.cpd.yuqing.databinding.PaperInfo8itemBinding
import com.cpd.yuqing.db.vo.szb.Paper
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

/**
 * 数字报首页的RecyclerView的adapter
 * Created by s21v on 2018/5/7.
 */
class PaperInfoAdapter(private val context: Context, var papers: ArrayList<Paper>?,
                       val onPaperClickListerner: PaperClickListener)
    : RecyclerView.Adapter<PaperInfoAdapter.PaperInfoVH>() {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val paperDates: ArrayList<String> = arrayListOf()
    private val paperInfoList: ArrayList<List<Paper>> = arrayListOf()

    init {
        if (papers != null) {
            val map = papers!!.groupBy { simpleDateFormat.format(it.date) }
            paperDates.addAll(map.keys)
            paperInfoList.addAll(map.values)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PaperInfoVH? {
        val layoutInflater = LayoutInflater.from(context)
        return when (viewType) {
            TYPE_8ITEM -> {
                val paperInfo8itemBinding = DataBindingUtil
                        .inflate<PaperInfo8itemBinding>(layoutInflater, R.layout.paper_info_8item, parent, false)
                PaperInfoVH(paperInfo8itemBinding)
            }
            TYPE_4ITEM -> {
                val paperInfo4itemBinding = DataBindingUtil
                        .inflate<PaperInfo4itemBinding>(layoutInflater, R.layout.paper_info_4item, parent, false)
                PaperInfoVH(paperInfo4itemBinding)
            }
            else -> null
        }
    }

    override fun getItemCount(): Int {
        return paperDates.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (paperInfoList[position].size == 8)
            TYPE_8ITEM
        else
            TYPE_4ITEM
    }

    override fun onBindViewHolder(holder: PaperInfoVH?, position: Int) {
        val paperDate = paperDates[position]
        val paperInfo: List<Paper> = paperInfoList[position]
        if (holder != null) {
            holder.dataBinding.setVariable(BR.papers, paperInfo)
            holder.dataBinding.setVariable(BR.date, paperDate)
            holder.dataBinding.setVariable(BR.clickListener, onPaperClickListerner)
        }
    }

    inner class PaperInfoVH(var dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

    interface PaperClickListener {
        fun onPaperClick(view: View, paper: Paper)
    }

    companion object {
        private const val TYPE_8ITEM = 1
        private const val TYPE_4ITEM = 2
    }
}