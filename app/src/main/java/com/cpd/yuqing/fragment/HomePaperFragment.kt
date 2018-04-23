package com.cpd.yuqing.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * 数字报页面
 * Created by s21v on 2018/4/23.
 */
class HomePaperFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_paper, container, false)
    }

    override fun onStart() {
        super.onStart()
        targetFragment.onActivityResult(NavigationHomeFragment.PAPER_RESUME, 200, null)
    }

    companion object {
        private const val TAG = "HomePaperFragment"
    }
}