package com.yu.drysister.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.NetworkUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tuyenmonkey.mkloader.MKLoader;

import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;
import com.yu.drysister.Utils.ACache;
import com.yu.drysister.Utils.PermissionsUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener ,PermissionsUtil.IPermissionsCallback {
    private Button downBtn,upBtn,permissionBtn,testBtn;
    private ImageView showImg;
    private int curPos = 0;//当前是哪一张
    private int page = 1;//当前页数
    private int number = 10; //当前请求数目
    private Sister sister;
    private MKLoader mkLoader;
    private static final String TAG = "NetWork";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private ACache aCache;//缓存
    private PermissionsUtil permissionsUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initJurisdiction();
    }
    //获取权限
    private void initJurisdiction() {
        permissionsUtil = PermissionsUtil
                .with(this)
                .requestCode(1)
                .isDebug(true)
                .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                .request();
            }
    private void initData() {
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
        upBtn =  findViewById(R.id.btn_up);//上一张
        downBtn =  findViewById(R.id.btn_down);//下一张
        testBtn =  findViewById(R.id.btn_test);//测试
        permissionBtn =  findViewById(R.id.btn_permission);//权限
        showImg =  findViewById(R.id.img_show);
        mkLoader = findViewById(R.id.mkLoader);
        upBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        testBtn.setOnClickListener(this);
        permissionBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_up://上一页
                if (curPos > 0){
                    curPos--;//当curpos == 1 时经过这一行，下一行变为0，会记载为0的图像
                    load();
                }else if (curPos == 0){
                    if (page > 1){
                        page--;
                        getJson();
                        curPos = (sister.getResults().size() - 1);
                    }else {
                        upBtn.setVisibility(View.INVISIBLE);
                        ToastUtils.showShort(R.string.not_up);
                    }
                }
                break;
            case R.id.btn_down://下一页
                upBtn.setVisibility(View.VISIBLE);
                if (curPos < (sister.getResults().size() - 1)){
                    curPos++;
                    load();
                }else if (curPos == (sister.getResults().size() - 1)){
                    page++;
                    curPos = 0;
                    getJson();
                }
                break;
            case R.id.btn_permission://权限
                initJurisdiction();
                break;
            case R.id.btn_test://测试页面
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
        }
    }
//加载图片
    private void load() {
        if (NetworkUtils.isConnected()){
        //    Log.e(TAG,"当前网络可用");
            OkGo.<Bitmap>get(sister.getResults().get(curPos).getUrl())
                    .execute(new BitmapCallback() {
                        @Override //网络加载成功回调
                        public void onSuccess(Response<Bitmap> response) {
                            mkLoader.setVisibility(View.INVISIBLE);
                            showImg.setImageBitmap(ImageUtils.compressByQuality(response.body(),50));
                            downBtn.setVisibility(View.VISIBLE);
                            //以md5的方式加密url作为key，缓存当前的图像 保存时间默认为一小时
                            aCache.put(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl()), ImageUtils.compressByQuality(response.body(),50),36*100);
                        }
                    });
        }else {
         //   Log.e(TAG,"当前网络不可用");
            //网络不可用 且缓存存在 时加载缓存
            if (aCache.getAsBitmap(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl()))!= null) {
                showImg.setImageBitmap(aCache.getAsBitmap(EncryptUtils.encryptMD5ToString(sister.getResults().get(curPos).getUrl())));
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
    //权限获取成功
        initData();
        initUI();
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
    //权限获取失败
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsUtil.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        permissionsUtil.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
