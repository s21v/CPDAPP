package com.cpd.yuqing.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cpd.yuqing.R;
import com.cpd.yuqing.db.vo.News;
import com.cpd.yuqing.fragment.NewsContentFragment;

/**
 * Created by s21v on 2017/12/15.
 */

public class NewsContentActivity extends AppCompatActivity{
    private News mNews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mNews = savedInstanceState.getParcelable("news");
        else
            mNews = getIntent().getParcelableExtra("news");
        setContentView(R.layout.activity_news_content);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentNewsContentContainer, NewsContentFragment.getInstance(mNews))
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("news", mNews);
    }
}
