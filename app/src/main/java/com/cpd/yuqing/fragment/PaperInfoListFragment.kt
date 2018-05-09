package com.cpd.yuqing.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.PaperInfoAdapter
import com.cpd.yuqing.db.vo.szb.Paper
import com.cpd.yuqing.retrofitInterface.IPaperInfoApi
import com.cpd.yuqing.util.RetrofitUtils
import kotlinx.android.synthetic.main.fragment_paper_info_list.*
import retrofit2.Retrofit
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by s21v on 2018/5/7.
 */
class PaperInfoListFragment : Fragment() {
    private lateinit var retrofit: Retrofit
    private lateinit var type: String
    private lateinit var curDate: Date
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val calendar = GregorianCalendar()
    private lateinit var papers: ArrayList<Paper>

    override fun onCreate(savedInstanceState: Bundle?) {
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
        val paperInfoApi = retrofit.create(IPaperInfoApi::class.java)
        paperInfoRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (papers.size != 0) {
            paperInfoRecycler.adapter = PaperInfoAdapter(context, papers, object : PaperInfoAdapter.PaperClickListener {
                override fun onPaperClick(view: View, paper: Paper) {
                    gotoPaperDetailActivity(paper)
                }
            })
        } else {
            paperInfoApi.getPaperByRange(type, simpleDateFormat.format(curDate), DURATION)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<ArrayList<Paper>>() {
                        var tmpData: ArrayList<Paper>? = null
                        override fun onNext(t: ArrayList<Paper>?) {
                            tmpData = t!!
                        }

                        override fun onCompleted() {
                            papers.addAll(tmpData!!)
                            paperInfoRecycler.adapter = PaperInfoAdapter(context, papers, object : PaperInfoAdapter.PaperClickListener {
                                override fun onPaperClick(view: View, paper: Paper) {
                                    gotoPaperDetailActivity(paper)
                                }
                            })
                        }

                        override fun onError(e: Throwable?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    })
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("type", type)
        outState?.putSerializable("curDate", curDate)
        outState?.putParcelableArrayList("papers", papers)
    }

    private fun gotoPaperDetailActivity(paper: Paper) {
//        val intent = Intent(context, PaperDetailActivity::class.java)
//        intent.putExtra("paper", paper)
//        startActivity(intent)
        Log.i(TAG, "onclick: $paper")
    }

    companion object {
        private val TAG = PaperInfoListFragment::class.java.simpleName!!
        private const val DURATION = 7
        fun getInstance(type: String): PaperInfoListFragment {
            val args = Bundle()
            args.putString("type", type)
            val fragment = PaperInfoListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}