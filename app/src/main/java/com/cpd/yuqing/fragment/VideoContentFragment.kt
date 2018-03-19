package com.cpd.yuqing.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.FragmentVedioContentBinding
import com.cpd.yuqing.db.vo.video.News

/**
 * Created by s21v on 2018/3/19.
 */
class VideoContentFragment : Fragment() {
    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        news = if (savedInstanceState != null)
            savedInstanceState.getParcelable("news")
        else
            arguments.getParcelable("news")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentVedioContentBinding>(inflater, R.layout.fragment_vedio_content, container, false)
        dataBinding.setVariable(BR.news, news)
        dataBinding.executePendingBindings()
        return dataBinding.root
    }

    companion object {
        fun getInstance(news: News): VideoContentFragment{
            val bundle = Bundle()
            bundle.putParcelable("news", news)
            val fragment = VideoContentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}