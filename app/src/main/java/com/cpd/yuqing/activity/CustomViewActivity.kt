package com.cpd.yuqing.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cpd.yuqing.R
import kotlinx.android.synthetic.main.activity_customview.*

/**
 * Created by s21v on 2017/8/24.
 */
class CustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customview)
        moveButton.setOnClickListener {
            (customView.parent as View).scrollBy(-20,-20)
        }

        val list = arrayListOf(10, 100, 1000)
        for((index, element) in list.withIndex()) {
            print("$index : $element")
        }
    }
}