package com.cpd.yuqing.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.cpd.yuqing.CpdnewsApplication
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.FavoriteNewsRecyclerViewAdapter
import com.cpd.yuqing.db.dao.ChannelDao
import com.cpd.yuqing.db.dao.NewsDao
import com.cpd.yuqing.db.vo.News
import com.cpd.yuqing.view.OnNewsClickListener
import com.cpd.yuqing.view.SampleLineItemDecoration

class NavigationFavoriteFragment : BaseFragment(), ActionMode.Callback, FavoriteNewsRecyclerViewAdapter.NotifySelectedNum {
    private var recyclerView: RecyclerView? = null
    private var mNewsClickListener: OnNewsClickListener? = null
    private var mNewsLongClickListener: View.OnLongClickListener? = null
    private var actionMode: ActionMode? = null
    private var dao: NewsDao? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mNewsClickListener = OnNewsClickListener(context)
        mNewsLongClickListener = View.OnLongClickListener{
            if (actionMode == null) {
                (activity as AppCompatActivity).startSupportActionMode(this)
                (recyclerView!!.adapter as FavoriteNewsRecyclerViewAdapter).isActionMode = true
                return@OnLongClickListener true
            } else
                return@OnLongClickListener false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mActionBarTitle = arguments.getCharSequence(ACTIONBAR_TITLE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_news_favorite_list, container, false)
        val channelDao = ChannelDao.getInstance(context)
        val channelList = channelDao.queryAll()
        channelDao.closeDB()
        recyclerView = rootView.findViewById(R.id.favoriteList)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = FavoriteNewsRecyclerViewAdapter(channelList!!, mNewsClickListener, mNewsLongClickListener, activity)
        recyclerView!!.addItemDecoration(SampleLineItemDecoration(context, android.R.color.darker_gray, SampleLineItemDecoration.VERTICAL_LIST, 1, false))
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //设置toolbar
        initActionBar()
    }

    override fun onResume() {
        super.onResume()
        //获得数据库中的数据
        dao = NewsDao(context)
        (recyclerView!!.adapter as FavoriteNewsRecyclerViewAdapter).mValues =
                dao!!.selectAll(CpdnewsApplication.getCurrentUser().id, NewsDao.TYPE_FAVORITE)
    }

    override fun onStop() {
        super.onStop()
        dao!!.closeDB()
        dao = null
    }

    override fun onDetach() {
        super.onDetach()
        mNewsClickListener = null
        mNewsLongClickListener = null
    }

    //adapter中借口定义的方法
    override fun selectedNumChanged(size: Int) {
        if (size == 0)
            actionMode!!.title = ""
        else
            actionMode!!.title = "已选择${size}项"
    }

    //实现ActionMode.Callback接口中的方法
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.delete) {
            if (dao != null) {
                val copyData = (recyclerView!!.adapter as FavoriteNewsRecyclerViewAdapter).getSelectedSet().clone()
                @Suppress("UNCHECKED_CAST")
                (copyData as HashSet<News>).forEach{
                    if (dao!!.cancel(CpdnewsApplication.getCurrentUser().id, it.news_id, NewsDao.TYPE_FAVORITE) == 1)
                        (recyclerView!!.adapter as FavoriteNewsRecyclerViewAdapter).removeSelectedItem(it)
                }
                copyData.clear()
                mode!!.finish()
            }
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return if (actionMode == null) {
            actionMode = mode
            mode!!.menuInflater.inflate(R.menu.delete, menu)
            true
        } else
            false
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        (recyclerView!!.adapter as FavoriteNewsRecyclerViewAdapter).isActionMode = false
    }

    companion object {
        private const val ACTIONBAR_TITLE = "actionBarTitle"
//        private val TAG = NavigationFavoriteFragment::class.java.simpleName

        fun newInstance(mActionBarTitle: CharSequence): NavigationFavoriteFragment {
            val fragment = NavigationFavoriteFragment()
            val args = Bundle()
            args.putCharSequence(ACTIONBAR_TITLE, mActionBarTitle)
            fragment.arguments = args
            return fragment
        }
    }
}
