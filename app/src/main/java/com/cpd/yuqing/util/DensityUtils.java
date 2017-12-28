package com.cpd.yuqing.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by s21v on 2017/5/3.
 */

public class DensityUtils {
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxVal/scale;
    }

    public static float px2sp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxVal/scale;
    }

}
