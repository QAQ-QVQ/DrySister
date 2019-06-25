package com.yu.drysister1.APP;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

public class okAPP extends Application {
    private static okAPP context;
    @Override
    public void onCreate() {
        super.onCreate();
        initOkGo();
        Utils.init(this);
    }
    private void initOkGo() {
        OkGo.getInstance().init(this)                       //必须调用初始化
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                            //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }
    public static okAPP getContext() {
        return context;
    }
}
