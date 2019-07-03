package com.yu.drysister.Activity;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;


import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import com.yu.drysister.Adapter.PageAdapter;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;
import com.yu.drysister.Utils.PermissionsUtil;



public class MainActivity extends BaseActivity implements PermissionsUtil.IPermissionsCallback {
    private RecyclerView recyclerView;
    private int page = 1;//当前页数
    private int number = 28; //当前请求数目
    private static final String TAG = "network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private PermissionsUtil permissionsUtil;//权限
    private Context mContext;
    private RefreshLayout refreshLayout;
    private Sister sister;
  //  public static ArrayList<Integer> posion;
    private PageAdapter  mypageAdapter;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
      //  posion = new ArrayList<Integer>();
        initJurisdiction();
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

    //获取UI
    private void initUI() {
       // mkLoader = findViewById(R.id.mkLoading);
        //        downloadBtn = findViewById(R.id.btn_download);//下载
        recyclerView = findViewById(R.id.recyclerview);
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
                page++;
                // number += 28;
                getJson();
            }
        });
    }

    //获取json 初始化数据
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
                          if (isFirst){
                              sister = new Gson().fromJson(json,Sister.class);
                              isFirst = false;
                              load();
                          }else {
                              Sister sisterBean = new Gson().fromJson(json, Sister.class);
//                              DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(sister, sisterBean), true);
                              sister.addResults(sisterBean.getResults());
                              mypageAdapter.notifyDataSetChanged();
//                              diffResult.dispatchUpdatesTo(mypageAdapter);
                              ToastUtils.showLong("加载成功！");
                               refreshLayout.finishLoadMore(true);//传入true表示加载成功
                          }
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

                });
    }

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
        mypageAdapter = new PageAdapter(mContext,sister);
        //设置adapter
        recyclerView.setAdapter(mypageAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mypageAdapter.setOnItemClickListener(new PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,boolean flag) {
                //点击
                //ToastUtils.showShort("" + position);
//                if (flag) {
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                intent.putExtra("url", sister.getResults().get(position).getUrl());
                intent.putExtra("position",position+"");
                intent.putExtra("page",page+"");
                    startActivity(intent);
                    Log.e(TAG,"short");
//                }else{
//                    ToastUtils.showShort("刷新中！");
//                    Log.e(TAG,"short:"+position);
//                }
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

    }

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        //权限获取成功
        getJson();
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
