package com.yu.drysister.Utils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yu.drysister.Interface.IwebCallback;
import com.yu.drysister.Interface.IwebManager;

/**
 * CREATED BY DY ON 2019/7/15.
 * TIME BY 10:21.
 **/
public class OkgoManager implements IwebManager {
    public static OkgoManager Instence;

    /**
     * 单例
     */
    public static OkgoManager getInstance() {
        if (Instence == null) {
            synchronized (SisterDBHelper.class) {
                if (Instence == null) {
                    Instence = new OkgoManager();
                }
            }
        }
        return Instence;
    }

    @Override
    public void get(String url, final IwebCallback iwebCallback) {
        OkGo.<String>get(url).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                iwebCallback.onSuccess(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                iwebCallback.onFailure(new Throwable(response.body()));
            }
        });
    }

    @Override
    public void get(String url, String cacheKey, final IwebCallback iwebCallback) {
        OkGo.<String>get(url)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)//先用缓存，再用网络
                .cacheKey(cacheKey)//缓存key 以当前页数为key
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        iwebCallback.onSuccess(response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        iwebCallback.onFailure(new Throwable(response.body()));
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        onSuccess(response);
                    }
                });
    }

    @Override
    public void post(String url, final IwebCallback iwebCallback) {
        OkGo.<String>post(url).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                iwebCallback.onSuccess(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                iwebCallback.onFailure(new Throwable(response.body()));
            }
        });
    }

    @Override
    public void put(String url, String requestBody, IwebCallback iwebCallback) {

    }

    @Override
    public void delete(String url, String requestBody, IwebCallback iwebCallback) {

    }
}
