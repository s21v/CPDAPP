package com.cpd.yuqing.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by s21v on 2017/5/15.
 */
public class NetUtils {
    private static final String IP4Simulator = "10.0.3.2:8080";
    private static final String IP4Phone = "192.168.5.103:8080";    //远程主机的地址
    private static final String BASE_URL = "http://" + IP4Simulator + "/CpdNews";
    public static final String UserCommonURL = BASE_URL + "/UserDBServlet";
    public static final String ChannelCommonUrl = BASE_URL + "/ChannelDBServlet";
    public static final String NewsCommonUrl = BASE_URL + "/NewsDBServlet";

    //判读网络是否连接
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.isConnected())
                return true;
        }
        return false;
    }
}
