package com.yu.drysister.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yu.drysister.Adapter.PageAdapter;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;
import com.yu.drysister.Utils.SisterDBHelper;

import java.util.List;

/**
 * 收藏页面
 * 废弃
 */
public class CollectionActivity extends BaseActivity {
    private  String flag;
    private List<ResultsBean> resultsBeanList;
    private Sister resultsBean;
    private RecyclerView recyclerView;
    private Sister sister;
    private Context mContext;
    private PageAdapter mypageAdapter;
    private RefreshLayout refreshLayout;
    private int page = 0;
    private int number = 28;
    private static final String TAG = "network";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initView();
        initDate();
    }

    private void initView() {
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
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
          //      page++;
                List<ResultsBean> resultsBeans = SisterDBHelper.getInstance().getSisterFlag(flag);
                if (resultsBeans.size() <= resultsBeanList.size()){
                    Toast.makeText(mContext, "到底啦！", Toast.LENGTH_SHORT).show();
                    refreshlayout.finishLoadMore(true/*,false*/);
                }else {
                    number += 28;
                    getDate();
                }
            }
        });
    }

    private void initDate() {
        mContext = this;
        flag = getIntent().getStringExtra("flag");
        getDate();
        load();
    }

    private void getDate() {
        // TODO: 2019/7/10 没有数据显示无数据页面
        resultsBeanList = SisterDBHelper.getInstance().getSistersLimit(page,number,flag);
        sister = new Sister(false,resultsBeanList);
    }

    //加载图片
    private void load() {
        RecyclerView.LayoutManager  gridManager = new GridLayoutManager(mContext, 2);
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
}
