package com.cpd.yuqing.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cpd.yuqing.R
import com.cpd.yuqing.db.vo.szb.Article
import com.cpd.yuqing.fragment.PaperContentFragment

/**
 * Created by s21v on 2018/5/22.
 */
class PaperContentActivity : AppCompatActivity() {
    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        article = if (savedInstanceState != null)
            savedInstanceState.getParcelable("article")
        else
            intent.getParcelableExtra("article")
        setContentView(R.layout.activity_paper_content)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, PaperContentFragment.getInstance(article))
                .commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("article", article)
    }
}