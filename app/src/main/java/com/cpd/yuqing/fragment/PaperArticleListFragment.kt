package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import com.cpd.yuqing.adapter.ArticleListRecyclerAdapter
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.db.vo.szb.Paper
import kotlinx.android.synthetic.main.fragment_paper_info.*
import java.util.ArrayList

/**
 * 用作侧边栏来显示报纸的文章列表
 * Created by s21v on 2018/5/17.
 */
class PaperArticleListFragment : Fragment() {
    private lateinit var paper: Paper
    private lateinit var articleList: ArrayList<Article>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            paper = savedInstanceState.getParcelable("paper")
            articleList = savedInstanceState.getParcelableArrayList("articleList")
        }
        else {
            paper = arguments.getParcelable("paper")
            articleList = arguments.getParcelableArrayList("articleList")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_paper_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when {
            paper.type == "szb" -> paperTypeImg.setImageResource(R.drawable.szb_header)
            paper.type == "jtzk" -> paperTypeImg.setImageResource(R.drawable.jtzk_header)
            else -> paperTypeImg.setImageResource(R.drawable.xtzk_header)
        }
        articleListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        articleListRv.setHasFixedSize(true)
        articleListRv.adapter = ArticleListRecyclerAdapter(articleList, activity)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArrayList("articleList", articleList)
        outState?.putParcelable("paper", paper)
    }

    companion object {
        fun getInstance(paper: Paper, articleList: ArrayList<Article>): PaperArticleListFragment {
            val args = Bundle()
            args.putParcelable("paper", paper)
            args.putParcelableArrayList("articleList", articleList)
            val fragment = PaperArticleListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}