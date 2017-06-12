package com.cpd.yuqing.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.activity_search.*

/**
 * Created by s21v on 2017/6/12.
 */
class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8,8,8,8)
        for (i in 0..10) {
            val textView = TextView(this)
            textView.setText("testtesttest"+i)
            textView.background = resources.getDrawable(R.drawable.sign_in_bg)
            textView.setPadding(8,8,8,8)
            textView.layoutParams = layoutParams
            searched.addView(textView)
        }
    }


}