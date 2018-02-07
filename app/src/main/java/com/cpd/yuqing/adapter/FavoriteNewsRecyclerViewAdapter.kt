package com.cpd.yuqing.adapter

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.FragmentNewsFavoriteBinding
import com.cpd.yuqing.db.vo.Channel
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.view.OnNewsClickListener
import kotlinx.android.synthetic.main.fragment_news_favorite.view.*

class FavoriteNewsRecyclerViewAdapter(private val channelList: ArrayList<Channel>,
                                      private val mClickListener: OnNewsClickListener?,
                                      private val mLongClickListener: View.OnLongClickListener?,
                                      private val context: Context) :
        RecyclerView.Adapter<FavoriteNewsRecyclerViewAdapter.ViewHolder>() {

    var isActionMode = false
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    var mValues: ArrayList<News> = arrayListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    //待删除的项
    private val selectedSet: HashSet<News> = hashSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = (context as Activity).layoutInflater
        val dataBind = DataBindingUtil.inflate<FragmentNewsFavoriteBinding>(layoutInflater, R.layout.fragment_news_favorite, parent, false)
        return ViewHolder(dataBind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBind.setVariable(BR.news, mValues[position])
        val root = holder.dataBind.root
        if (isActionMode) {
            root.checkbox.visibility = View.VISIBLE
            root.checkbox.isChecked = selectedSet.contains(mValues[position])
            root.setOnClickListener {
                root.checkbox.isChecked = !root.checkbox.isChecked
                if (root.checkbox.isChecked)
                    selectedSet.add(mValues[position])
                else
                    selectedSet.remove(mValues[position])
            }
        }
        else {
            root.checkbox.visibility = View.GONE
            //设置监听器
            root.setOnClickListener {
                mClickListener?.onClick(mValues[position])
            }
        }
        root.setOnLongClickListener(mLongClickListener)
        val channelName = getChannelName(mValues[position].channel_id)
        if (channelName != null)
            root.channelName.setText("栏目:$channelName")
        else
            root.channelName.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    private fun getChannelName(channelId: String): String? {
        return (0 .. channelList.size)
                .firstOrNull { channelList[it].id == channelId }
                ?.let { channelList[it].name }
    }

    fun getSelectedSet(): HashSet<News> {
        return selectedSet
    }

    fun removeSelectedItem(item: News) {
        selectedSet.remove(item)
        val position = mValues.indexOf(item)
        mValues.remove(item)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(val dataBind: FragmentNewsFavoriteBinding) : RecyclerView.ViewHolder(dataBind.root)

    companion object {
        private val TAG = FavoriteNewsRecyclerViewAdapter::class.java.simpleName
    }

    interface NotifySelectedNum {
        fun selectedNumChanged(size: Int)
    }
}
