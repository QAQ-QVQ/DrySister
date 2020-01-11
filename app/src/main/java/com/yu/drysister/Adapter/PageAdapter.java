package com.yu.drysister.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.myViewHolder> {
    private Context mContext;
    private Sister sister;
    private boolean flag;

    // 事件回调监听
    private PageAdapter.OnItemClickListener onItemClickListener;

    public PageAdapter(Context Context, Sister sister) {
        this.mContext = Context;
        this.sister = sister;
    }

    //② 创建ViewHolder
    public static class myViewHolder extends RecyclerView.ViewHolder {
        public final ImageView context;
        public RelativeLayout loadingErr;
        public TextView title;

        public myViewHolder(View v) {
            super(v);
            context = v.findViewById(R.id.imageViewItem);
            loadingErr = v.findViewById(R.id.loading_err);
            title = v.findViewById(R.id.create_time);
        }
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder myViewHolder, final int position) {

        myViewHolder.title.setText(sister.getResults().get(position).getDesc());
        Glide.with(mContext).load(sister.getResults().get(position).getUrl())
                .placeholder(R.drawable.icon)
                .listener(new RequestListener<Drawable>() {
                    //加载图片失败
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        posion.add(position);
//                        SisterUtils.sister.getResults().remove(position);
                        flag = false;
                        return true;
                    }

                    //加载图片成功
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        flag = true;
                        return false;
                    }
                })
                .into(myViewHolder.context);

        //加载成功的点击
        myViewHolder.context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, flag);
                }
            }
        });
        myViewHolder.context.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(position, flag);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return sister.getResults().size();
    }

    // ① 定义点击回调接口
    public interface OnItemClickListener {
        void onItemClick(int position, boolean flag);

        void onItemLongClick(int position, boolean flag);
    }

    public interface getPosion {
        void posion(int posion);
    }

    // ② 定义一个设置点击监听器的方法
    public void setOnItemClickListener(PageAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void addSisterSize(Sister sister) {
        this.sister = sister;
        notifyDataSetChanged();
        // notifyItemInserted(this.sister.getResults().size());
    }
}
