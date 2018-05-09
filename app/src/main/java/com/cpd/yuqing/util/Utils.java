package com.cpd.yuqing.util;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cpd.yuqing.R;
import com.cpd.yuqing.db.vo.szb.Paper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cpd.yuqing.util.NetUtils.PAPERURL;

/**
 * Created by Administrator on 2017/4/20.
 */

public class Utils {
    private static final String TAG = "Utils";

    //对输入进行SHA加密，用16进制的形式输出
    @NonNull
    public static String SHAEncrypt(String input) {
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(input.getBytes());
            for (byte b : messageDigest.digest()) {
                builder.append(String.format("%1$02x", b));  //%1$02x 第一个0是flag：表示用0填充；2表示宽度;
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "SHA加密失败" + e.getMessage());
        }
        return builder.toString();
    }

    //对图片进行高斯模糊处理，并返回处理后的图片
    public static void gaussianBlur(Context context, Bitmap bitmap) {
        //获得渲染脚本的实例
        RenderScript renderScript = RenderScript.create(context);
        //建立源、目的Allocation；Allocation提供了从RenderScript内核传递数据的方法
        Allocation sourceAllocation = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation outputAllocation = Allocation.createTyped(renderScript, sourceAllocation.getType());
        //获得高斯模糊的脚本
        ScriptIntrinsicBlur gaussianBlurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //设置系数
        gaussianBlurScript.setRadius(15);
        //设置输入
        gaussianBlurScript.setInput(sourceAllocation);
        //渲染
        gaussianBlurScript.forEach(outputAllocation);
        //输出到结果图片
        outputAllocation.copyTo(bitmap);
        //释放资源
        gaussianBlurScript.destroy();
        outputAllocation.destroy();
        sourceAllocation.destroy();
        renderScript.destroy();
    }

    /**
     * 图片转换为圆形，作为头像使用
     *
     * @param source  要做成头像的图片
     * @param context
     * @param dpVal   头像区域的大小,单位是dp
     * @return
     */
    public static Bitmap getRoundBitmap(Bitmap source, Context context, int dpVal) {
        //头像区域的大小，单位是px
        int destWidth = DensityUtils.dp2px(context, dpVal);
        int destHeight = destWidth;
        //头像区域的图片作为最后的返回的结果图片
        Bitmap result = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_4444);
        //设置头像区域的画布
        Canvas canvas = new Canvas(result);
        //设置画笔，抗锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //先绘制圆形区域(下层)
        Bitmap round = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
        Matrix roundScale = new Matrix();
        //设置Bitmap缩放的缩放比例，注意类型转换否则比例不对
        roundScale.postScale((float) destWidth / round.getWidth(), (float) destHeight / round.getHeight());
        canvas.drawBitmap(round, roundScale, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  //（取上下层交集，显示上层）
        //后绘制头像图片（上层）
        Matrix sourceScale = new Matrix();
        sourceScale.postScale((float) destWidth / source.getWidth(), (float) destHeight / source.getHeight());
        canvas.drawBitmap(source, sourceScale, paint);
        return result;
    }

    //得到时间差
    public static String getDateDifferenceValue(long publishTime) {
        long nowDateTime = new Date().getTime();
        long differenceValue = nowDateTime - publishTime;
        if (differenceValue / (24 * 60 * 60 * 1000) > 0) {
            return String.format("%d天前", differenceValue / (24 * 60 * 60 * 1000));
        } else {
            long differenceValue2 = differenceValue % (24 * 60 * 60 * 1000);
            if (differenceValue2 / (60 * 60 * 1000) > 0)
                return String.format("%d小时前", differenceValue2 / (60 * 60 * 1000));
            else {
                long differenceValue3 = differenceValue2 % (60 * 60 * 1000);
                if (differenceValue3 / (60 * 1000) > 0)
                    return String.format("%d分钟前", differenceValue3 / (60 * 1000));
                else {
                    long differenceValue4 = differenceValue3 % (60 * 1000);
                    if (differenceValue4 / 1000 > 0)
                        return String.format("%d秒钟前", differenceValue4 / 1000);
                    else
                        return "刚刚";
                }
            }
        }
    }

    // databinding 数据转换
    @BindingAdapter("loadImg")
    public static void loadPaperThumb(ImageView imageView, Paper paper) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String thumbUrl = PAPERURL+paper.getType()+"/"+simpleDateFormat.format(paper.getDate())+"/"+paper.getThumbPath();
        Glide.with(imageView.getContext().getApplicationContext())
                .load(thumbUrl)
                .into(imageView);
    }
}
