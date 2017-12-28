package com.cpd.yuqing.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.cpd.yuqing.R
import java.io.InputStream

/**
 * Glide Module类 会被Glide-annotations自动使用
 * Created by s21v on 2017/7/10.
 */
@GlideModule
class GlideUtils : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context?, builder: GlideBuilder?) {
        builder!!.setDefaultRequestOptions(RequestOptions()
                .placeholder(R.mipmap.pic_downloading)
                .error(R.mipmap.pic_download_fail)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_ARGB_8888))
        //如果外部存储可用使用外部磁盘缓存，否则使用内部磁盘缓存
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState())
            builder.setDiskCache(ExternalCacheDiskCacheFactory(context, "glideDiskCache", 50 shl 20))
        else
            builder.setDiskCache(InternalCacheDiskCacheFactory(context, "glideDiskCache", 20 shl 20))
        //设置内存缓存大小
        val calculator = MemorySizeCalculator.Builder(context!!)
                .setMemoryCacheScreens(3f)
                .build()
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize))
    }

    override fun registerComponents(context: Context?, glide: Glide?, registry: Registry?) {
        //关联OkHttp3
        registry!!.append(GlideUrl::class.java, InputStream::class.java,
                OkHttpUrlLoader.Factory(
                        OkHttpUtils.getOkHttpUtilInstance(context!!)!!.okHttpClientInstance)
        )
    }
}
