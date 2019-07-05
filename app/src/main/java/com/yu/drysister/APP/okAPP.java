package com.yu.drysister.APP;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

public class okAPP extends Application {
    private static okAPP context;

    @Override
    public void onCreate() {
        super.onCreate();
        initOkGo();
        Utils.init(this);
        UMConfigure.init(this,"5d1d5f7c3fc19571ef001109"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"d1965fe55ba61afcc7bcdeda35e4799d");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0
        //微信
        PlatformConfig.setWeixin("Appkey","AppSecret");
        //新浪微博(第三个参数为回调地址)
    //    PlatformConfig.setSinaWeibo("Appkey", "AppSecret","http://sns.whalecloud.com/sina2/callback");
        //QQ
        PlatformConfig.setQQZone("AppId", "AppSecret");
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
