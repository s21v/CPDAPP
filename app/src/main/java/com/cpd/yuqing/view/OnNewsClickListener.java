package com.cpd.yuqing.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cpd.yuqing.activity.NewsContentActivity;
import com.cpd.yuqing.db.vo.News;

/**
 * Created by s21v on 2017/12/15.
 */

public class OnNewsClickListener implements View.OnClickListener {
    private News mNews;
    private Context mContext;

    public OnNewsClickListener(Context context, News news){
        mNews = news;
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, NewsContentActivity.class);
        intent.putExtra("news", mNews);
        mContext.startActivity(intent);
    }
}
