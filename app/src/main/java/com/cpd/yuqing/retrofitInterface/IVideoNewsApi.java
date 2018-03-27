package com.cpd.yuqing.retrofitInterface;

import com.cpd.yuqing.db.vo.video.News;

import java.util.ArrayList;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * retrofit接口:视频新闻
 * Created by s21v on 2018/3/5.
 */

public interface IVideoNewsApi {
    //获得某个视频栏目最近的几条新闻
    @GET("VideoNewsDBServlet?m=last")
    Observable<ArrayList<News>> getLastVideoNews(@Query("cid") int channelId, @Query("count") int count);

    //获得播报类警务新闻
    @GET("VideoNewsDBServlet?m=getCpdNews")
    Observable<ArrayList<News>> getLastCpdNews(@Query("count") int count);

    //获得非播报类警务新闻
    @GET("VideoNewsDBServlet?m=getNotCpdNews")
    Observable<ArrayList<News>> getLastNotCpdNews(@Query("count") int count);

    //分页获取新闻
    @GET("VideoNewsDBServlet?m=getNewsByPage")
    Observable<ArrayList<News>> getNewsByPage(@Query("cid") int channelId, @Query("curPage") int curPage, @Query("count") int count);
}
