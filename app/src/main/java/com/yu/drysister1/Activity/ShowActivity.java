package com.yu.drysister1.Activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yu.drysister1.R;

public class ShowActivity extends AppCompatActivity {
    private ImageView showImage;
    private RelativeLayout loadingErr;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        showImage = findViewById(R.id.show_image);
        loadingErr = findViewById(R.id.loading_err);
        url = getIntent().getStringExtra("url");
        load();
    }

    private void load() {
        Glide.with(this).load(url)
                .placeholder(R.drawable.icon)
                .listener(new RequestListener<Drawable>() {
                    //加载图片失败
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingErr.setVisibility(View.VISIBLE);
                        showImage.setVisibility(View.GONE);
                        loadingErr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShort("刷新中！");
                                    load();
                            }
                        });
                        return true;
                    }
                    //加载图片成功
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingErr.setVisibility(View.GONE);
                        showImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(showImage);
    }
}
