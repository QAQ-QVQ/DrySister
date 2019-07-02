package com.yu.drysister.Activity;

import androidx.viewpager.widget.ViewPager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jaren.lib.view.LikeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yu.drysister.R;


public class TestActivity extends BaseActivity {

    ViewPager viewPager;
    RefreshLayout refreshLayout;
    LikeView likeView1;
    LikeView likeView2;
    LikeView likeView3;
    LikeView likeView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        likeView1 = findViewById(R.id.lv_icon1);
//        likeView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                likeView1.toggle();
//            }
//        });
//        likeView2 = findViewById(R.id.lv_icon2);
//        likeView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                likeView2.toggle();
//            }
//        });
//        likeView3 = findViewById(R.id.lv_icon3);
//        likeView3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                likeView3.toggle();
//            }
//        });
//        likeView4 = findViewById(R.id.lv_icon4);
//        likeView4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                likeView4.toggle();
//            }
//        });
        //改变状态
//        likeView.setChecked(true);
//        likeView.setCheckedWithoutAnimator(true);

        //将状态改为相反
//        likeView.toggle();
//        likeView.toggleWithoutAnimator();

//        viewPager = findViewById(R.id.view_pager);
        //   getJson();
//        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
//        //设置 Header 为 贝塞尔雷达 样式
////        refreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
//        //设置 Footer 为 球脉冲 样式
////        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
//        refreshLayout.setEnableRefresh(false);//取消下拉刷新
//        refreshLayout.setEnableLoadMore(true);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
//            }
//        });
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
//
//            }
//        });

    }

}
