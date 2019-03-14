package com.yu.drysister.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.NetworkUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.db.CacheManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.tuyenmonkey.mkloader.MKLoader;
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
    private RelativeLayout group_btn;
    private int curPos = 0;//当前是哪一张
    private int page = 1;//当前页数
    private int number = 10; //当前请求数目
    private Sister sister;
    private MKLoader mkLoader;
    private static final String TAG = "NetWork";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private ACache aCache;//缓存
    private boolean firstFlag = true;//是否第一次启动
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initUI();
    }
    private void initData() {
     //   SisterDBHelper.getInstance();//初始化数据库
        aCache = ACache.get(this);//初始化缓存
        getJson();
    }

public void getJson(){
    String HomeUrl = BASE_URL + number + "/" + page;
    OkGo.<String>get(HomeUrl)
            .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)//先用缓存，再用网络
            .cacheKey("picjson")//缓存key
            .execute(new StringCallback() {
                //网络请求成功调用
                @Override
                public void onSuccess(Response<String> response) {
                    String json = response.body();
                    if (json != null){
                        sister = new Gson().fromJson(json, Sister.class);
                        load();
                    }
                }
                //缓存请求成功调用
                @Override
                public void onCacheSuccess(Response<String> response) {
                    onSuccess(response);
                }
            });
    Toast.makeText(this, "第" + page+"页", Toast.LENGTH_SHORT).show();
}
    private void initUI() {
        upBtn = (Button) findViewById(R.id.btn_up);//上一张
        downBtn = (Button) findViewById(R.id.btn_down);//下一张
        testBtn = (Button) findViewById(R.id.btn_test);//测试
        otherBtn = (Button) findViewById(R.id.btn_other);//换一批
        showImg = (ImageView) findViewById(R.id.img_show);
        group_btn = findViewById(R.id.group_btn);
        mkLoader = findViewById(R.id.mkLoader);
        upBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        testBtn.setOnClickListener(this);
        otherBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_up:
                if (curPos > 0){
                    load();
                    curPos--;
                }else if (curPos == 0){
                    if (page > 1){
                        page--;
                        getJson();
                        curPos = 9;
                    }else {
                        upBtn.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.btn_down:
                upBtn.setVisibility(View.VISIBLE);
                if (curPos < 9){
                    curPos++;
                    load();
                }else if (curPos == 9){
                    page++;
                    curPos = 0;
                    getJson();
                }
                break;
            case R.id.btn_other:


                break;
            case R.id.btn_test:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
        }
    }
//加载图片
    private void load() {
        List<Sister.ResultsBean> results = new ArrayList<>();
        if (NetworkUtils.isConnected()){
        //    Log.e(TAG,"当前网络可用");
            OkGo.<Bitmap>get(sister.getResults().get(curPos).getUrl())
                    .execute(new BitmapCallback() {
                        @Override //网络加载成功回调
                        public void onSuccess(Response<Bitmap> response) {

                            mkLoader.setVisibility(View.INVISIBLE);
                            showImg.setImageBitmap(ImageUtils.compressByQuality(response.body(),50));
                            group_btn.setVisibility(View.VISIBLE);
                            //以md5的方式加密url作为key
                            aCache.put(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl()), ImageUtils.compressByQuality(response.body(),50));
                        }
                    });
        }else {
         //   Log.e(TAG,"当前网络不可用");
            showImg.setImageBitmap(aCache.getAsBitmap(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl())));
        }
    }
}
