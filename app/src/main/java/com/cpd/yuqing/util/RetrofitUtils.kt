package com.cpd.yuqing.util

import android.content.Context

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 单例retrofit
 * Created by s21v on 2018/3/6.
 */

class RetrofitUtils private constructor(context: Context) {
    val retrofitInstance: Retrofit

    init {
        val okHttpClient = OkHttpUtils
                .getOkHttpUtilInstance(context)!!
                .okHttpClientInstance
        retrofitInstance = Retrofit.Builder()
                .baseUrl(NetUtils.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
    }

    companion object {
        @Volatile
        private var retrofitUtils: RetrofitUtils? = null

        fun getInstance(context: Context): RetrofitUtils? {
            if (retrofitUtils == null)
                synchronized(RetrofitUtils::class.java) {
                    if (retrofitUtils == null)
                        retrofitUtils = RetrofitUtils(context)
                }
            return retrofitUtils
        }
    }
}
