package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cpd.yuqing.R

/**
 * Created by s21v on 2017/6/12.
 */
class NewsFragment : Fragment{
    constructor() : super()

    companion object {
        fun getInstance(args: Bundle): NewsFragment {
            var fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater?.inflate(R.layout.news_channel_layout, container, false)
        var textView = view?.findViewById(R.id.text) as TextView
        textView.text = arguments.getString("channelName")
        return view
    }
}