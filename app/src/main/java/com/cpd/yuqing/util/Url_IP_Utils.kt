package com.cpd.yuqing.util

/**
 * Created by s21v on 2017/5/5.
 */

object Url_IP_Utils {
    private val IP4Simulator = "10.0.3.2:8080"
    private val IP4Phone = "192.168.5.101:8080"    //远程主机的地址
    private val BASE_URL = "http://$IP4Simulator/CpdNews"
    val UserCommonURL = BASE_URL + "/UserDBServlet"
    val ChannelCommonUrl = BASE_URL + "/ChannelDBServlet"
    val NewsCommonUrl = BASE_URL + "/NewsDBServlet"
}
