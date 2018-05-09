package com.cpd.yuqing.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络相关
 * Created by s21v on 2017/5/15.
 */
public class NetUtils {
    private static final String IP4Simulator = "10.0.2.2:8080"; //genymotion模拟机IP:10.0.3.2:8080
    private static final String IP4Phone = "192.168.5.102:8080";    //远程主机的地址
    public static final String BASE_URL = "http://" + IP4Simulator + "/CpdNews/";
    public static final String UserCommonURL = BASE_URL + "UserDBServlet";
    public static final String ChannelCommonUrl = BASE_URL + "ChannelDBServlet";
    public static final String NewsCommonUrl = BASE_URL + "NewsDBServlet";
    public static final String PAPERURL = "http://epaper.cpd.com.cn/";  // 数字报的地址

    //判读网络是否连接
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return null != networkInfo && networkInfo.isAvailable();
        }
        return false;
    }

    //判读是否式无线网络
    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return networkInfo.isAvailable();
        }
        return false;
    }
}
