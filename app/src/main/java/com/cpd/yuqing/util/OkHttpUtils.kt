package com.cpd.yuqing.util

import android.content.Context
import android.os.Environment
import android.util.Log
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by s21v on 2017/7/10.
 */
class OkHttpUtils private constructor(context: Context) {
    val okHttpClientInstance: OkHttpClient

    init {
        //生成OkHttpUtil对象
        val builder = OkHttpClient.Builder()
        //设置读写、链接超时
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        //50M外部缓存
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState())
            builder.cache(Cache(context.externalCacheDir!!, (50 shl 20).toLong()))
        else
        //20M内存缓存
            builder.cache(Cache(context.cacheDir, (20 shl 20).toLong()))
        okHttpClientInstance = builder.build()
    }

    //异步网络连接
    fun httpConnection(request: Request, callback: Callback) {
        okHttpClientInstance.newCall(request).enqueue(callback)  //异步调用
    }

    // 单例
    companion object {
        private var okHttpUtilsInstance: OkHttpUtils? = null

        fun getOkHttpUtilInstance(context: Context): OkHttpUtils? {
            if (okHttpUtilsInstance == null) {
                synchronized(OkHttpUtils::class.java) {
                    if (okHttpUtilsInstance == null)
                        okHttpUtilsInstance = OkHttpUtils(context)
                }
            }
            return okHttpUtilsInstance
        }
    }

}
