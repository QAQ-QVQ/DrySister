package com.yu.drysister.Activity;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import com.yu.drysister.Adapter.PageAdapter;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.Dialog.AboutDialog;

import com.yu.drysister.Interface.IwebCallback;
import com.yu.drysister.Interface.IwebManager;
import com.yu.drysister.R;

import com.yu.drysister.Utils.DbLikeDefine;
import com.yu.drysister.Utils.PermissionsUtil;
import com.yu.drysister.Utils.WebFactory;

import java.util.List;
import java.util.zip.Inflater;


/**
 * 主页面
 */
public class MainActivity extends BaseActivity implements PermissionsUtil.IPermissionsCallback {
    private RecyclerView recyclerView;
    private int page = 1;//当前页数
    private int number = 28; //当前请求数目
    private static final String TAG = "network";
    private static String BASE_URL;
    private PermissionsUtil permissionsUtil;//权限
    private Context mContext;
    private RefreshLayout refreshLayout;
    private Toolbar toolbar;
    private Sister sister;
    //  public static ArrayList<Integer> posion;
    private Mydapter mypageAdapter;
    private boolean isFirst = true;
    private boolean splash = false;
    private TextView tv_spleash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        BASE_URL = getResources().getString(R.string.sister_url);
        //  posion = new ArrayList<Integer>();
        initJurisdiction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (SPUtils.getInstance().getBoolean("splash")){
            menu.getItem(1).setIcon(R.drawable.checked);
        }else {
            menu.getItem(1).setIcon(R.drawable.check);
        }
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
        recyclerView = findViewById(R.id.recyclerview);
        refreshLayout = findViewById(R.id.refreshLayout);
        tv_spleash = findViewById(R.id.tv_spleash);
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

        refreshLayout.setEnableRefresh(false);//取消下拉刷新
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
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
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(4));
        tv_spleash.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(4));
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(4));
        tv_spleash.animate().translationY(0).setInterpolator(new DecelerateInterpolator(4));
    }

    class Mydapter extends BaseQuickAdapter<ResultsBean, BaseViewHolder> {
        public Mydapter(int layoutResId, @Nullable List<ResultsBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ResultsBean item) {
            helper.setText(R.id.create_time, item.getDesc());
            Glide.with(MainActivity.this).load(item.getUrl()).placeholder(R.drawable.icon).into((ImageView) helper.getView(R.id.imageViewItem));
        }
    }

    //获取json 初始化数据
    public void getJson() {
        String HomeUrl = BASE_URL + number + "/" + page;
        IwebManager iwebManager = WebFactory.getWebManager();
        iwebManager.get(HomeUrl, "pic" + page, new IwebCallback() {
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
                        ToastUtils.showLong(getResources().getString(R.string.loading_true));
                        refreshLayout.finishLoadMore(true);//传入true表示加载成功
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                refreshLayout.finishLoadMore(false);//上拉加载失败
                //refreshLayout.finishLoadMore(2000/*,false*/);//2s后延迟执行
                //当网络未连接且无缓存时出现的提示
                ToastUtils.showShort(getResources().getString(R.string.net_false));
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
        mypageAdapter = new Mydapter(R.layout.recyclerview_item, sister.getResults());
        View headView = View.inflate(mContext, R.layout.header_layout, null);
        headView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
        toolbarInit();
        mypageAdapter.addHeaderView(headView);
        //设置adapter
        recyclerView.setAdapter(mypageAdapter);
        //设置Item增加、移除动画
        mypageAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mypageAdapter.isFirstOnly(false);

        mypageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, ShowActivity.class);
                intent.putExtra("position", position + "");
                intent.putExtra("page", page + "");
                intent.putExtra("ResultsBean", sister.getResults().get(position));
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                Log.e(TAG, "short" + position);
            }
        });
        mypageAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e(TAG, "long" + position);
                return true;
            }
        });
    }

    private void toolbarInit() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tv_test:
                        Intent intentL = new Intent(MainActivity.this, TestActivity.class);
                        intentL.putExtra("sister", sister);
                        startActivity(intentL);
                        break;
                    case R.id.tv_about:
                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
                        break;
                    case R.id.tv_spleash:
                        splash = !splash;
                        SPUtils.getInstance().put("splash",splash);
                        if (splash){
                            item.setIcon(R.drawable.checked);
                        }else {
                            item.setIcon(R.drawable.check);
                        }
                        break;
                }
                return false;
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
