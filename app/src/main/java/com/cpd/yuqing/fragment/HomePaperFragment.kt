package com.cpd.yuqing.fragment

import android.app.Activity
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

    override fun onResume() {
        super.onResume()
        // 除去多余的组件
        targetFragment.onActivityResult(NavigationHomeFragment.PAPER_RESUME, Activity.RESULT_OK, null)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            // 除去多余的组件
            targetFragment.onActivityResult(NavigationHomeFragment.PAPER_RESUME, Activity.RESULT_OK, null)
    }

    companion object {
        private const val TAG = "HomePaperFragment"
    }
}