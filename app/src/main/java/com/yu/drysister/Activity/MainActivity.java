package com.yu.drysister.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;


import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tuyenmonkey.mkloader.MKLoader;

import com.yu.drysister.Adapter.PageAdapter;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;
import com.yu.drysister.Utils.PermissionsUtil;
import com.yu.drysister.Utils.SisterUtils;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PermissionsUtil.IPermissionsCallback {
    private Button downloadBtn, testBtn;
    private RecyclerView recyclerView;
    public int curPos = 0;//当前是哪一张
    public int page = 1;//当前页数
    private int number = 28; //当前请求数目
    private MKLoader mkLoader;
    private static final String TAG = "NetWork";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private PermissionsUtil permissionsUtil;//权限
    private static final String destFileDir = "/storage/emulated/0/Android/data/com.yu.drysister/SisterImage";//下载后文件夹名称
    private String destFileName;//文件名 按照 妹子加当前页加索引
    private Context mContext;
    private RefreshLayout refreshLayout;
    public static ArrayList<Integer> posion;
    private PageAdapter  mypageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        posion = new ArrayList<Integer>();
        initJurisdiction();
    }


    //下载图片
//    private void dwonloadImage() {
//        destFileName = "妹子" + page + curPos + ".jpg";
//        if (getFilesAllName(destFileName)) {
//            ToastUtils.showShort("文件已存在");
//        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    OkGo.<File>get(SisterUtils.getInstance().GetSisterBean().getResults().get(curPos).getUrl())
//
//                            .execute(new FileCallback(destFileDir, destFileName) {
//                                @Override
//                                public void onSuccess(Response<File> response) {
//                                    ToastUtils.showShort(response.body().getName() + "下载成功");
//                                }
//
//                                @Override
//                                public void downloadProgress(Progress progress) {
//                                    Log.e("EasyHttpActivity", "当前下载了:" + progress.currentSize + ",总共有:" + progress.totalSize + ",下载速度为:" + progress.speed);//这个totalSize一直是初始值-1，很尴尬
//                                    Log.e("xinxi", progress.toString());
//                                }
//
//                                @Override
//                                public void onError(Response<File> response) {
//                                    ToastUtils.showShort(response.body().getName() + "下载失败");
//                                }
//                            });
//                }
//            }).start();
//        }
//    }

    //获取文件夹下所有文件名称并判断是否重名
    public static boolean getFilesAllName(String FileName) {
        File filePath = new File(destFileDir);
        if (!filePath.exists())
            filePath.mkdirs();//不存在就新建一个目录
        File[] files = filePath.listFiles();
        for (int i = 0; i < files.length; i++) {
            Log.e(TAG, files[i].getName());
            if (FileName.equals(files[i].getName())) {
                return true;
            }
        }
        return false;
    }

    //获取权限
    private void initJurisdiction() {
        permissionsUtil = PermissionsUtil
                .with(this)
                .requestCode(1)
                .isDebug(true)
                .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE, PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    //初始化数据
    private void initData() {
        getJson();
    }

    //获取json
    public void getJson() {
        String HomeUrl = BASE_URL + number + "/" + page;
        OkGo.<String>get(HomeUrl)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)//先用缓存，再用网络
                .cacheKey("pic" + page)//缓存key 以当前页数为key
                .execute(new StringCallback() {
                    //网络请求成功调用
                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        if (json != null) {
                            SisterUtils.sister = new Gson().fromJson(json, Sister.class);
                            //设置全局唯一对象
//                            SisterUtils.getInstance().SetSisterBean(sister);
                            do {
                                load();
                            }while (false);

                            mypageAdapter.notifyDataSetChanged();
                            refreshLayout.finishLoadMore(true);//传入true表示加载成功
                            ToastUtils.showLong("加载成功！");
                        }
                    }

                    //缓存请求成功调用
                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        onSuccess(response);
                    }

                    //网络请求失败
                    @Override
                    public void onError(Response<String> response) {
                        refreshLayout.finishLoadMore(false);//下拉加载失败
                        //refreshLayout.finishLoadMore(2000/*,false*/);//2s后延迟执行
                        //当网络未连接且无缓存时出现的提示
                        ToastUtils.showShort("网络未连接！");
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getJson();
//                            }
//                        }, 10000);//延时10秒递归
                    }

                    @Override
                    public void onFinish() {
//                        ToastUtils.showLong("第" + page + "页");
                    }
                });

    }

    //获取UI
    private void initUI() {

        //        downloadBtn = findViewById(R.id.btn_download);//下载
        //   loading = findViewById(R.id.loading_bar);//进度条
//        mkLoader = findViewById(R.id.mkLoader);//加载进度
//        viewPager = findViewById(R.id.view_pager);//图片
        recyclerView = findViewById(R.id.recyclerview);

        //        downloadBtn.setOnClickListener(this);

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        //设置 Header 为 贝塞尔雷达 样式
//        refreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        //设置 Footer 为 球脉冲 样式
//        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setEnableRefresh(false);//取消下拉刷新
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getJson();
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                number += 28;
                getJson();
            }
        });

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_up://上一页
//                if (curPos > 0) {
//                    curPos--;//当curpos == 1 时经过这一行，下一行变为0，会记载为0的图像
//                    load();
//                } else if (curPos == 0) {
//                    if (page > 1) {
//                        page--;
//                        getJson();
//                        curPos = (sister.getResults().size() - 1);
//                    } else {
//                        ToastUtils.showShort(R.string.not_up);
//                    }
//                }
//                //             loading.setProgress(curPos);
//                break;
//            case R.id.btn_down://下一页
//                if (curPos < (sister.getResults().size() - 1)) {
//                    curPos++;
//                    load();
//                } else if (curPos == (sister.getResults().size() - 1)) {
//                    page++;
//                    curPos = 0;
//                    getJson();
//                }
//                //            loading.setProgress(curPos);
//                break;
//            case R.id.btn_download://下载
//                dwonloadImage();
//                break;
//            case R.id.btn_test://测试页面
//                Intent intent = new Intent(MainActivity.this, TestActivity.class);
//                startActivity(intent);
//                break;
//        }
//    }




    //加载图片
    private void load() {
        RecyclerView.LayoutManager gridManager = new GridLayoutManager(mContext,4);
        ((GridLayoutManager) gridManager).setRecycleChildrenOnDetach(true);
        //设置布局管理器
        recyclerView.setLayoutManager(gridManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
//       if (posion != null){
//             for (Integer s:posion) {
//                Log.e(TAG,s+"");
//            }
//        }
        mypageAdapter = new PageAdapter(mContext);
        mypageAdapter.setOnItemClickListener(new PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,boolean flag) {
                //点击
                //ToastUtils.showShort("" + position);
                if (flag) {
                    startActivity(new Intent(MainActivity.this, ShowActivity.class).putExtra("url", SisterUtils.sister.getResults().get(position).getUrl()));
                    Log.e(TAG,"short");
                }else{
                    ToastUtils.showShort("刷新中！");
                    Log.e(TAG,"short:"+position);
                }
            }

            @Override
            public void onItemLongClick(int position,boolean flag) {
                if (flag) {
                    // TODO: 2019/6/25 长按效果
                    //长按
//                    ToastUtils.showLong("changan" + position);
                    Log.e(TAG,"long");
                } else {

                }
            }

        });
        //设置adapter
        recyclerView.setAdapter(mypageAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        permissionsUtil.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
