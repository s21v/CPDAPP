package com.cpd.yuqing.activity

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import com.cpd.yuqing.R
import com.cpd.yuqing.view.FontSizeView
import kotlinx.android.synthetic.main.news_content_popupmenu.*
import kotlin.properties.Delegates

/**
 * Created by s21v on 2017/12/29.
 */
class TestViewActivity : AppCompatActivity(), FontSizeView.SliderPositionChangeListener {
    override fun onSliderPositionChange(currentSliderPosition: Int) {
        Toast.makeText(this, "当前位置：$currentSliderPosition", Toast.LENGTH_SHORT).show()
    }

    var popup: PopupWindow by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val button = Button(this)
        button.setText("Click!!!")
        button.setOnClickListener {
            val rootView = this@TestViewActivity.layoutInflater.inflate(R.layout.news_content_popupmenu, null)
            rootView.findViewById<FontSizeView>(R.id.fontSizeView).callBack = this@TestViewActivity
            popup = PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            //设置背景图后setOutsideTouchable()才有效
            popup.setBackgroundDrawable(BitmapDrawable())
            //点击popupwindow以外的区域是否可以点击
            popup.isOutsideTouchable = true
            //show
            popup.showAtLocation(button, Gravity.BOTTOM, 0, 0)
        }
        setContentView(button)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (popup.isShowing)
            popup.dismiss()
    }
}