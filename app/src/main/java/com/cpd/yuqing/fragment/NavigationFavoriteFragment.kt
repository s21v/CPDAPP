package com.cpd.yuqing.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.FavoriteNewsRecyclerViewAdapter
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.view.OnNewsClickListener

class NavigationFavoriteFragment : BaseFragment(), ActionMode.Callback{
    //实现ActionMode.Callback接口中的方法
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        Log.i(TAG, "onActionItemClicked")
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        Log.i(TAG, "onCreateActionMode")
        return if (actionMode == null) {
            actionMode = mode
            mode!!.menuInflater.inflate(R.menu.delete, menu)
            true
        } else
            false
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        Log.i(TAG, "onPrepareActionMode")
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        Log.i(TAG, "onDestroyActionMode")
        actionMode = null
    }

    private var recyclerView: RecyclerView? = null
    var data: ArrayList<News>? = null
    set(value) {
        field = value
        if (field != null && recyclerView != null) {
            recyclerView!!.adapter.notifyDataSetChanged()
        }
    }
    private var mNewsClickListener: OnNewsClickListener? = null
    private var mNewsLongClickListener: View.OnLongClickListener? = null
    private var actionMode: ActionMode? = null

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
        recyclerView!!.adapter = FavoriteNewsRecyclerViewAdapter(data!!, mNewsClickListener, mNewsLongClickListener, activity)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //设置toolbar
        initActionBar()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mNewsClickListener = OnNewsClickListener(context)
        mNewsLongClickListener = View.OnLongClickListener{
            if (actionMode == null) {
                (activity as AppCompatActivity).startSupportActionMode(this)
                return@OnLongClickListener true
            } else
                return@OnLongClickListener false
        }
    }

    override fun onDetach() {
        super.onDetach()
        mNewsClickListener = null
        mNewsLongClickListener = null
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
