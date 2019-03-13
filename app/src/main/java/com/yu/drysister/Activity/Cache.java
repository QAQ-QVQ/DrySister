package com.yu.drysister.Activity;

import android.graphics.Bitmap;

import android.util.Log;
import android.util.LruCache;


import java.lang.ref.SoftReference;

import java.util.Map;



public class Cache {

    public Cache(){
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    private static final int MAX_CACHE_COUNT = 30;  //设置最大缓存数
    private LruCache<String, Bitmap> mMemoryCache;


    // 把Bitmap对象加入到缓存中
    public void setBitmapToMemory(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    // 从缓存中得到Bitmap对象
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    // 从缓存中删除指定的Bitmap
    public void removeBitmapFromMemory(String key) {
        mMemoryCache.remove(key);
    }


}
