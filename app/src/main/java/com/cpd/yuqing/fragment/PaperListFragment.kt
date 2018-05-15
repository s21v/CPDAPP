package com.cpd.yuqing.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.PaperActivity
import com.cpd.yuqing.adapter.PaperInfoAdapter
import com.cpd.yuqing.db.vo.szb.Paper
import com.cpd.yuqing.retrofitInterface.IPaperInfoApi
import com.cpd.yuqing.util.RetrofitUtils
import kotlinx.android.synthetic.main.fragment_paper_info_list.*
import kotlinx.android.synthetic.main.wait_page_layout.*
import retrofit2.Retrofit
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 数字报版面列表页
 * Created by s21v on 2018/5/7.
 */
class PaperListFragment : Fragment() {
    private lateinit var retrofit: Retrofit
    private lateinit var type: String
    private lateinit var curDate: Date
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val calendar = GregorianCalendar()
    private lateinit var papers: ArrayList<Paper>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG,"onCreate")
        super.onCreate(savedInstanceState)
        retrofit = RetrofitUtils.getInstance(context)!!.retrofitInstance
        if (savedInstanceState != null) {
            type = savedInstanceState.getString("type")
            curDate = savedInstanceState.getSerializable("curDate") as Date
            papers = savedInstanceState.getParcelableArrayList("papers")
        } else {
            type = arguments.getString("type")
            curDate = Date()
            papers = arrayListOf()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_paper_info_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG,"onViewCreated, papers size：${papers.size}")
        paperInfoRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        paperInfoRecycler.setHasFixedSize(false)
        if (papers.size > 0) {
            // 隐藏loading页面
            waitingPage.visibility = View.GONE
            paperInfoRecycler.adapter = PaperInfoAdapter(context, papers, object : PaperInfoAdapter.PaperClickListener {
                override fun onPaperClick(view: View, paper: Paper) {
                    gotoPaperDetailActivity(paper)
                }
            })
        } else {
            // 显示loading页面
            waitingPage.visibility = View.VISIBLE
            firstDownload()
        }
        reloadPage.setOnClickListener {
            reloadPage.visibility = View.GONE
            firstDownload()
        }
    }

    // 首次下载数据
    private fun firstDownload() {
        val paperInfoApi = retrofit.create(IPaperInfoApi::class.java)
        paperInfoApi.getPaperByRange(type, simpleDateFormat.format(curDate), DURATION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<Paper>>() {
                    var tmpData: ArrayList<Paper>? = null
                    override fun onNext(t: ArrayList<Paper>?) {
                        tmpData = t
                    }

                    override fun onCompleted() {
                        if (tmpData != null && tmpData?.size!! > 0) {    // 确保获得数据
                            // 隐藏loading页面
                            emptyPage.visibility = View.GONE
                            papers.addAll(tmpData!!)
                            Log.i(TAG, "$papers")
                            paperInfoRecycler.adapter = PaperInfoAdapter(context, papers, object : PaperInfoAdapter.PaperClickListener {
                                override fun onPaperClick(view: View, paper: Paper) {
                                    gotoPaperDetailActivity(paper)
                                }
                            })
                        } else {
                            // 显示重新加载按钮
                            reloadPage.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(e: Throwable?) {
                        if (emptyPage.visibility == View.GONE)
                            emptyPage.visibility = View.VISIBLE
                        // 显示重新加载按钮
                        reloadPage.visibility = View.VISIBLE
                    }
                })
    }

    override fun onStart() {
        super.onStart()
        // 设置recycler view的滑动监听器， 用以改变footerView 的显示状态
        paperInfoRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isLoading = false   // 防止重复下载
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                val llm = paperInfoRecycler.layoutManager as LinearLayoutManager
                val lastCompletelyVisibleItemPosition = llm.findLastCompletelyVisibleItemPosition()
                val lastVisibleItemPosition = llm.findLastVisibleItemPosition()
                val adapter = paperInfoRecycler.adapter as PaperInfoAdapter
                val footViewPosition = adapter.getFootPosition()
                when (newState) {
                // 在滑动时检查是否未完全滚动到了footer
                    RecyclerView.SCROLL_STATE_DRAGGING, RecyclerView.SCROLL_STATE_SETTLING -> {
                        if (lastVisibleItemPosition == footViewPosition) {
                            if (lastCompletelyVisibleItemPosition != footViewPosition) {
                                adapter.notifyItemChanged(lastVisibleItemPosition, false)
                            }
                        }
                    }
                // 滑动停止时检查并更新footer
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (lastVisibleItemPosition == footViewPosition) {
                            if (lastCompletelyVisibleItemPosition == footViewPosition) {    //底部视图完全显示
                                if (!isLoading) {   // 防止多次重复下载
                                    isLoading = true
                                    adapter.notifyItemChanged(lastVisibleItemPosition, true)
                                    // 更新当前要下载的时间
                                    nextDate()
                                    // 下载数据
                                    val paperInfoApi = retrofit.create(IPaperInfoApi::class.java)
                                    paperInfoApi.getPaperByRange(type, simpleDateFormat.format(curDate), DURATION)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object : Subscriber<ArrayList<Paper>>() {
                                                var tmpData: ArrayList<Paper>? = null
                                                override fun onNext(t: ArrayList<Paper>?) {
                                                    tmpData = t
                                                }

                                                override fun onCompleted() {
                                                    // 添加数据
                                                    if (tmpData != null) {
                                                        Log.i(TAG, "list size:${tmpData!!.size}")
                                                        papers.addAll(tmpData!!)
                                                        adapter.addMoreData(tmpData!!)
                                                    }
                                                    isLoading = false
                                                }

                                                override fun onError(e: Throwable?) {
                                                    Toast.makeText(context, e?.message
                                                            ?: "不能获得下载数据", Toast.LENGTH_SHORT).show()
                                                    isLoading = false
                                                }
                                            })
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        // 清理滑动监听器
        paperInfoRecycler.clearOnScrollListeners()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("type", type)
        outState?.putSerializable("curDate", curDate)
        outState?.putParcelableArrayList("papers", papers)
    }

    private fun gotoPaperDetailActivity(paper: Paper) {
        val intent = Intent(context, PaperActivity::class.java)
        intent.putExtra("paper", paper)
        startActivity(intent)
    }

    // 获得下一个查询日期
    private fun nextDate() {
        calendar.time = papers.last().date
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1)
        curDate = calendar.time
        Log.i(TAG, "nextDate:${simpleDateFormat.format(curDate)}")
    }

    companion object {
        private val TAG = PaperListFragment::class.java.simpleName!!
        private const val DURATION = 7
        fun getInstance(type: String): PaperListFragment {
            val args = Bundle()
            args.putString("type", type)
            val fragment = PaperListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}