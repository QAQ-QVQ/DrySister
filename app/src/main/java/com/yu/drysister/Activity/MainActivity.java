package com.yu.drysister.Activity;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.appcompat.app.ActionBar;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;


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
import com.yu.drysister.Dialog.AboutDialog;

import com.yu.drysister.Interface.IwebCallback;
import com.yu.drysister.Interface.IwebManager;
import com.yu.drysister.R;

import com.yu.drysister.Utils.PermissionsUtil;
import com.yu.drysister.Utils.WebFactory;


/**
 * 主页面
 */
public class MainActivity extends BaseActivity implements PermissionsUtil.IPermissionsCallback {
    private RecyclerView recyclerView;
    private int page = 1;//当前页数
    private int number = 28; //当前请求数目
    private static final String TAG = "network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private PermissionsUtil permissionsUtil;//权限
    private Context mContext;
    private RefreshLayout refreshLayout;
    private Toolbar toolbar;
    private Sister sister;
    //  public static ArrayList<Integer> posion;
    private PageAdapter mypageAdapter;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        //  posion = new ArrayList<Integer>();
        initJurisdiction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tv_test1:
                        Toast.makeText(mContext, "tv1", Toast.LENGTH_SHORT).show();
                        break;
//                    case R.id.tv_like:
//                        Toast.makeText(mContext, "收藏", Toast.LENGTH_SHORT).show();
//                     //   Intent intentL = new Intent(MainActivity.this,CollectionActivity.class);
//                     //   intentL.putExtra("flag", DbLikeDefine.DB_LIKE);
//                     //   startActivity(intentL);
//                        break;
//                    case R.id.tv_give:
//                        Toast.makeText(mContext, "点赞", Toast.LENGTH_SHORT).show();
//                     //   Intent intentC = new Intent(MainActivity.this,CollectionActivity.class);
//                     //   intentC.putExtra("flag", DbLikeDefine.DB_COLLECTION);
//                     //   startActivity(intentC);
//                        break;
                    case R.id.tv_about:
                        new AboutDialog(mContext, true, new AboutDialog.onItemClicklisner() {
                            @Override
                            public void onclicklistner() {
                                Toast.makeText(mContext, "关于我", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                        break;
                }
                return false;
            }
        });

        refreshLayout =  findViewById(R.id.refreshLayout);
        /**
         * 实现RecyclerView上下滑动的显示和隐藏****
         *
         * */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private static final int HIDE_THRESHOLD = 30;
            private int scrolledDistance = 0;
            private boolean controlsVisible = true;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.e(TAG, "onScrolled dy: " + dy);
//                Log.e(TAG, "onScrolled dx: " + dx);
//                Log.e(TAG, "-------------------- onScrolled: --------------------");
                int firstVisibleItem = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisibleItem >= 1) {
                    if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                        // 隐藏toolbar
                        hideViews();
                        controlsVisible = false;
                        scrolledDistance = 0;
                    } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                        // 显示toolbar
                        showViews();
                        controlsVisible = true;
                        scrolledDistance = 0;
                    }
                    if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                        scrolledDistance += dy;
                    }
                }
            }
        });

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
                getJson();
            }
        });
    }

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
       // toolbar.setVisibility(View.GONE);
    }

    private void showViews() {
       // toolbar.setVisibility(View.VISIBLE);
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    //获取json 初始化数据
    public void getJson() {
        String HomeUrl = BASE_URL + number + "/" + page;
        IwebManager iwebManager = WebFactory.getWebManager();
        iwebManager.get(HomeUrl,"pic" + page, new IwebCallback() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    if (isFirst) {
                        sister = new Gson().fromJson(response, Sister.class);
                        isFirst = false;
                        load();
                    } else {
                        Sister sisterBean = new Gson().fromJson(response, Sister.class);
                        sister.addResults(sisterBean.getResults());
                        mypageAdapter.notifyDataSetChanged();
                        ToastUtils.showLong("加载成功！");
                        refreshLayout.finishLoadMore(true);//传入true表示加载成功
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                refreshLayout.finishLoadMore(false);//下拉加载失败
                //refreshLayout.finishLoadMore(2000/*,false*/);//2s后延迟执行
                //当网络未连接且无缓存时出现的提示
                ToastUtils.showShort("网络未连接！");
            }
        });
    }

    //加载图片
    private void load() {
        RecyclerView.LayoutManager gridManager = new GridLayoutManager(mContext, 2);
        ((GridLayoutManager) gridManager).setRecycleChildrenOnDetach(true);
        //设置布局管理器
        recyclerView.setLayoutManager(gridManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mypageAdapter = new PageAdapter(mContext, sister);

        //设置adapter
        recyclerView.setAdapter(mypageAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mypageAdapter.setOnItemClickListener(new PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean flag) {
                //点击
                //ToastUtils.showShort("" + position);
//                if (flag) {
                Intent intent = new Intent(mContext, ShowActivity.class);
        //        intent.putExtra("url", sister.getResults().get(position).getUrl());
                intent.putExtra("position", position + "");
                intent.putExtra("page", page + "");
                intent.putExtra("sister",sister);
                startActivity(intent);
                Log.e(TAG, "short");
//                }else{
//                    ToastUtils.showShort("刷新中！");
//                    Log.e(TAG,"short:"+position);
//                }
            }

            @Override
            public void onItemLongClick(int position, boolean flag) {
                if (flag) {
                    // TODO: 2019/6/25 长按效果
                    //长按
//                    ToastUtils.showLong("changan" + position);
                    Log.e(TAG, "long");
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
