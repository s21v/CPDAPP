package com.cpd.yuqing.retrofitInterface;

import com.cpd.yuqing.db.vo.video.Channel;
import java.util.ArrayList;

import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by s21v on 2018/3/5.
 */

public interface IVideoChannelApi {
    //根据参数不同选择获得全部视频栏目或者全部视频专题
    @GET("VideoChannelDBServlet")
    Observable<ArrayList<Channel>> getVideoChannelOrSubject(@Query("m") String method);

    @GET("VideoChannelDBServlet?m=getLastSubject")
    Observable<Channel> getLastSubject();
}
