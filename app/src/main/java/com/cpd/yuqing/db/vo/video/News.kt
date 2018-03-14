package com.cpd.yuqing.db.vo.video

/**
 * 视频新闻类
 * Created by s21v on 2018/3/5.
 */

data class News(var id: Int = 0, var title: String? = null, var newsUrl: String? = null, var source: String? = null,
                var author: String? = null, var publishTime: String? = null, var thumbIconUrl: String? = null,
                var videoUrl: String? = null, var content: String? = null, var channelId: Int = 0)