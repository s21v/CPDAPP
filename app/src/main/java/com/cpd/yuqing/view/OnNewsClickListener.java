package com.cpd.yuqing.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cpd.yuqing.activity.NewsContentActivity;
import com.cpd.yuqing.db.vo.News;

/**
 * Created by s21v on 2017/12/15.
 */

public class OnNewsClickListener {
    private Context mContext;

    public OnNewsClickListener(Context context){
        mContext = context;
    }

    public void onClick(News news) {
        Intent intent = new Intent(mContext, NewsContentActivity.class);
        intent.putExtra("news", news);
        mContext.startActivity(intent);
    }
}
