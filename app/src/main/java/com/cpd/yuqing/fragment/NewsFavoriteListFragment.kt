package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.FavoriteNewsRecyclerViewAdapter
import com.cpd.yuqing.db.vo.News

class NewsFavoriteListFragment : Fragment() {
    var data: ArrayList<News>? = null
    set(value) {
        Log.i(TAG, "setData !!!")
        field = value
        if (field != null) {
            if (view is RecyclerView) {
                (view as RecyclerView).adapter.notifyDataSetChanged()
            }
        }
    }
//    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate() !!!")
        if (arguments != null) {
            data = arguments.getParcelableArrayList<News>(DATA)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView() !!!")
        val view = inflater!!.inflate(R.layout.fragment_news_favorite_list, container, false)
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = FavoriteNewsRecyclerViewAdapter(data!!, object : OnListFragmentInteractionListener {
                override fun onListFragmentInteraction(item: News) {
                    //跳转到内容页面
                }
            })
        }
        return view
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        mListener = null
//    }

    /**
     * recyclerView item点击事件监听器
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: News)
    }

    companion object {
        private val DATA = "data"
        private val TAG = NewsFavoriteListFragment::class.java.simpleName

        fun newInstance(data: ArrayList<News>): NewsFavoriteListFragment {
            val fragment = NewsFavoriteListFragment()
            val args = Bundle()
            args.putParcelableArrayList(DATA, data)
            fragment.arguments = args
            return fragment
        }
    }
}
