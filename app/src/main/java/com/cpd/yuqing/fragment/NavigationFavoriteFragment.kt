package com.cpd.yuqing.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.FavoriteNewsRecyclerViewAdapter
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.view.OnNewsClickListener

class NavigationFavoriteFragment : BaseFragment() {
    private var recyclerView: RecyclerView? = null
    var data: ArrayList<News>? = null
    set(value) {
        field = value
        if (field != null && recyclerView != null) {
            recyclerView!!.adapter.notifyDataSetChanged()
        }
    }
    private var mListener: OnNewsClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments.getParcelableArrayList<News>(DATA)
            mActionBarTitle = arguments.getCharSequence(ACTIONBAR_TITLE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_news_favorite_list, container, false)
        recyclerView = rootView.findViewById(R.id.favoriteList)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = FavoriteNewsRecyclerViewAdapter(data!!, mListener)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //设置toolbar
        initActionBar()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = OnNewsClickListener(context)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {
        private const val DATA = "data"
        private const val ACTIONBAR_TITLE = "actionBarTitle"
        private val TAG = NavigationFavoriteFragment::class.java.simpleName

        fun newInstance(data: ArrayList<News>, mActionBarTitle: CharSequence): NavigationFavoriteFragment {
            val fragment = NavigationFavoriteFragment()
            val args = Bundle()
            args.putParcelableArrayList(DATA, data)
            args.putCharSequence(ACTIONBAR_TITLE, mActionBarTitle)
            fragment.arguments = args
            return fragment
        }
    }
}
