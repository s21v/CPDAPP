package com.cpd.yuqing.retrofitInterface;

import com.cpd.yuqing.db.vo.szb.Paper;

import java.util.ArrayList;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 数字报的retrofit接口
 * Created by s21v on 2018/5/4.
 */
public interface IPaperInfoApi {
    // 获得一段时间内的版面信息
    @GET("PaperDBServlet?m=getPaperByRange")
    Observable<ArrayList<Paper>> getPaperByRange(@Query("type") String type, @Query("date") String date,
                                                 @Query("duration") int duration);
}
