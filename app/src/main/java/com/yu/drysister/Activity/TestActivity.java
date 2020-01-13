package com.yu.drysister.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;

import com.yu.drysister.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TestActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    LinearLayout refreshLayout;
    private Sister sister;
    private Context mContext;
    private Mydapter mydapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        sister = (Sister) getIntent().getSerializableExtra("sister");
        mContext = this;
        RecyclerView.LayoutManager gridManager = new GridLayoutManager(mContext, 2);
        ((GridLayoutManager) gridManager).setRecycleChildrenOnDetach(true);
        //设置布局管理器
        recyclerView.setLayoutManager(gridManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mydapter = new Mydapter(R.layout.recyclerview_item, sister.getResults());
        //添加头布局
        View headView = View.inflate(mContext, R.layout.header_layout_test, null);

        DrawerLayout.LayoutParams layoutParams = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        headView.setLayoutParams(layoutParams);

        //设置是否占满一行
        mydapter.setHeaderViewAsFlow(true);
        mydapter.addHeaderView(headView);
        drag();
//        Switch itemDrag = headView.findViewById(R.id.ItemDrag);
        Switch itemSwipe = headView.findViewById(R.id.ItemSwipe);
        Switch Splash = headView.findViewById(R.id.SplashRun);
        Splash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.getInstance().put("splash", isChecked);
            }
        });
//        itemDrag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (itemDrag.isChecked()) {
//                    itemSwipe.setChecked(false);
//                }
//            }
//        });

        itemSwipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    swip();
            }
        });

        //设置adapter
        recyclerView.setAdapter(mydapter);
        //设置Item增加、移除动画
        mydapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mydapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showShort("shout" + position);
            }
        });
        mydapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showShort("long:" + position);
                return true;
            }
        });

    }

    private void swip() {
        // 开启滑动删除
        mydapter.enableSwipeItem();
        mydapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });
    }

    private void drag() {
        // 设置是否可以滑动
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mydapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        // 开启拖拽
        mydapter.enableDragItem(itemTouchHelper, R.id.imageViewItem, true);
        mydapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            }
        });
    }


    class Mydapter extends BaseItemDraggableAdapter<ResultsBean, BaseViewHolder> {
        public Mydapter(int layoutResId, @Nullable List<ResultsBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ResultsBean item) {
            helper.setText(R.id.create_time, item.getDesc());
            Glide.with(TestActivity.this).load(item.getUrl()).into((ImageView) helper.getView(R.id.imageViewItem));
//            setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.header_layout, null, false));
        }
    }
}
