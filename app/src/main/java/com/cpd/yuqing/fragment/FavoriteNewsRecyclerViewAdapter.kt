package com.cpd.yuqing.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.fragment.NewsFavoriteListFragment.OnListFragmentInteractionListener

class FavoriteNewsRecyclerViewAdapter(private val mValues: ArrayList<News>,
                                      private val mListener: OnListFragmentInteractionListener?) :
        RecyclerView.Adapter<FavoriteNewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_news_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mContentView.text = mValues[position].homePageTitle

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.findViewById<View>(R.id.content) as TextView
        var mItem: News? = null
    }
}
