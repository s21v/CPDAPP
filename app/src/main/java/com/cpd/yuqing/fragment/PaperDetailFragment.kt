package com.cpd.yuqing.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cpd.yuqing.R
import com.cpd.yuqing.activity.PaperActivity
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.db.vo.szb.Paper
import com.cpd.yuqing.util.NetUtils
import kotlinx.android.synthetic.main.fragment_paper_detail_main.*
import java.text.SimpleDateFormat

/**
 * 报纸版面图
 * Created by s21v on 2018/5/23.
 */
class PaperDetailFragment : Fragment() {
    private lateinit var paper: Paper
    private lateinit var articleList: ArrayList<Article>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            paper = savedInstanceState.getParcelable("paper")
            articleList = savedInstanceState.getParcelableArrayList("articleList")
        } else {
            paper = arguments.getParcelable("paper")
            articleList = arguments.getParcelableArrayList("articleList")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_paper_detail_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(context).load(getImageUrl(paper.imgPath!!)).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
                shadeView.background = resource
                shadeView.setData(paper, articleList)
                shadeView.articleClickedListener = activity as PaperActivity
                shadeView.articleSelectedListener = activity as PaperActivity
            }
        })
    }

    private fun getImageUrl(imgPath: String): String {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        return (NetUtils.PAPERURL + paper.type + "/" + simpleDateFormat.format(paper.date)
                + "/" + imgPath)
    }

    fun openTouch() {
        shadeView.isClickable = true
        shadeView.isLongClickable = true
    }

    fun closeTouch() {
        shadeView.isClickable = false
        shadeView.isLongClickable = false
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("paper", paper)
        outState?.putParcelableArrayList("articleList", articleList)
    }

    companion object {
        fun getInstance(paper: Paper, articleList: ArrayList<Article>): PaperDetailFragment {
            val args = Bundle()
            args.putParcelable("paper", paper)
            args.putParcelableArrayList("articleList", articleList)
            val fragment = PaperDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}