package com.yu.drysister.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yu.drysister.Adapter.PageAdapter;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TestActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    LinearLayout refreshLayout;
    Sister sister;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        sister = (Sister) getIntent().getSerializableExtra("sister");

        RecyclerView.LayoutManager gridManager = new GridLayoutManager(mContext, 2);
        ((GridLayoutManager) gridManager).setRecycleChildrenOnDetach(true);
        //设置布局管理器
        recyclerView.setLayoutManager(gridManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        Mydapter mydapter = new Mydapter(R.layout.recyclerview_item,sister.getResults());
        //设置adapter
        recyclerView.setAdapter(mydapter);
        //设置Item增加、移除动画
        mydapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        mydapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showShort("shout"+position);
            }
        });
        mydapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showShort("long:"+position);
                return true;
            }
        });

    }
    class Mydapter extends BaseQuickAdapter<ResultsBean,BaseViewHolder> {
        public Mydapter(int layoutResId, @Nullable List<ResultsBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ResultsBean item) {
            helper.setText(R.id.create_time,item.getDesc());
            Glide.with(TestActivity.this).load(item.getUrl()).into((ImageView) helper.getView(R.id.imageViewItem));
        }
    }
}
