package com.cpd.yuqing.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by s21v on 2017/5/15.
 */

public class NetUtils {
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
