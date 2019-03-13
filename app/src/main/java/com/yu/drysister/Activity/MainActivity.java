package com.yu.drysister.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.db.CacheManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.yu.drysister.R;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.Utils.ACache;
import com.yu.drysister.Utils.SisterDBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button downBtn;
    private Button upBtn;
    private Button otherBtn;
    private Button testBtn;
    private ImageView showImg;
    private int curPos = 0;//当前是哪一张
    private int page = 1;//当前页数
    private int number = 10; //当前请求数目
    private Sister sister;
    private SisterDBHelper sisterDBHelper;//数据库
    private static final String TAG = "NetWork";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private ACache aCache;//缓存
    private boolean acacheFlag;//是否使用缓存
 //   private boolean unDown ;//上一张下一张
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initUI();
    }
    private void initData() {
        aCache = ACache.get(this);//初始化缓存
        String HomeUrl = BASE_URL + number + "/" + page;
        OkGo.<String>get(HomeUrl)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)//先用缓存，再用网络用网络
                .cacheKey("picjson")//缓存key
                .execute(new StringCallback() {
                    //网络请求成功调用
                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        if (json != null){
                            sister = new Gson().fromJson(json, Sister.class);
                     //       Log.e(TAG,"请求成功");
                        }

                    }
                    //缓存请求成功调用
                    @Override
                    public void onCacheSuccess(Response<String> response) {
                       // super.onCacheSuccess(response);
                        if (!acacheFlag){
                    //        Log.e(TAG,"缓存调用成功");
                            onSuccess(response);
                            acacheFlag = true;
                        }
                    }
                });
        Toast.makeText(this, "" + HomeUrl, Toast.LENGTH_SHORT).show();

    }

    private void initUI() {
        upBtn = (Button) findViewById(R.id.btn_up);//上一张
        downBtn = (Button) findViewById(R.id.btn_down);//下一张
        testBtn = (Button) findViewById(R.id.btn_test);//测试
        otherBtn = (Button) findViewById(R.id.btn_other);//换一批
        showImg = (ImageView) findViewById(R.id.img_show);
        upBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        testBtn.setOnClickListener(this);
        otherBtn.setOnClickListener(this);
    }
    private void DownodloadFile(View view) {
        //代码进度条的文件下载
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkGo.<File>get("sss").headers("user", "18435997685_02-18-22:55:31_03-03-16:09:57.pdf")
                        .execute(new FileCallback("kejian.pdf") {//在下载中，可以设置下载下来的文件的名字
                            @Override
                            public void downloadProgress(Progress progress) {
                                super.downloadProgress(progress);
                                Log.d("EasyHttpActivity", "当前下载了:" + progress.currentSize + ",总共有:" + progress.totalSize + ",下载速度为:" + progress.speed);//这个totalSize一直是初始值-1，很尴尬
                                Log.d("xinxi", progress.toString());
                            }

                            @Override
                            public void onSuccess(Response<File> response) {
                                Log.d("EasyHttpActivity", "下载完成");
                            }
                        });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_up:
                if (curPos != 0){
                    curPos--;
                }else if (curPos == 0){
                    curPos = 9;
                }
                load();//true 上一张
                Log.e(TAG,curPos+"    up");
                break;
            case R.id.btn_down:
                if (curPos >= 9) {
                    curPos = 0;
                }else {
                    curPos++;
                }
                load();//false 下一张
                Log.e(TAG,curPos+"    down");
                break;
            case R.id.btn_other:
                page++;
                initData();
                break;
            case R.id.btn_test:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
        }
    }

    private void load() {
        if (NetworkUtils.isConnected() && curPos <= 9){
        //    Log.e(TAG,"当前网络可用");
            OkGo.<Bitmap>get(sister.getResults().get(curPos).getUrl())
                    .execute(new BitmapCallback() {
                        @Override //网络加载成功回调
                        public void onSuccess(Response<Bitmap> response) {
                            showImg.setImageBitmap(ImageUtils.compressByQuality(response.body(),50));
                            //以md5的方式加密url作为key
                            aCache.put(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl()), ImageUtils.compressByQuality(response.body(),50));

                        }
                    });
        }else if(curPos <= 9) {
         //   Log.e(TAG,"当前网络不可用");
            showImg.setImageBitmap(aCache.getAsBitmap(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl())));

        }
    }
}
