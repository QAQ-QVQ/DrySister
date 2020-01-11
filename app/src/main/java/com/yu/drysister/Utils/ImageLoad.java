package com.yu.drysister.Utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.yu.drysister.APP.okAPP;
import com.yu.drysister.Activity.MainActivity;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ImageLoad {
    private Iload iload;
    private static int length;

    public ImageLoad(String url, final ImageView image, final Context context) {
        OkGo.<Bitmap>post(url).execute(new BitmapCallback() {
            @Override
            public void onSuccess(Response<Bitmap> response) {
                image.setImageBitmap(response.body());
            }

            @Override
            public void onError(Response<Bitmap> response) {
                image.setImageDrawable(context.getDrawable(R.drawable.icon));
            }
        });
    }

    public static void load(Context context, Sister sister, Iload iload) {
        List<ResultsBean> resultsBeans = new ArrayList<>();
        Sister sisterRe = new Sister(false, resultsBeans);
        length = 0;
        for (ResultsBean resultsBean : sister.getResults()) {
            Glide.with(context)
                    .load(resultsBean.getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            length++;
                            MainActivity.toolbar.setTitle(context.getResources().getString(R.string.loading)+new DecimalFormat("0%").format((length*1.0)/sister.getResults().size()));
                            if (length == sister.getResults().size()) {
                                if (iload != null) {
                                    iload.loadTrue(sisterRe);
                                }
                            }
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            sisterRe.getResults().add(resultsBean);
                            length++;
                           MainActivity.toolbar.setTitle(context.getResources().getString(R.string.loading)+new DecimalFormat("0%").format((length*1.0)/sister.getResults().size()));
                            if (length == sister.getResults().size()) {
                                if (iload != null) {
                                    iload.loadTrue(sisterRe);
                                }
                            }
                            return true;
                        }

                    }).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                }
            });
        }
    }

    public interface Iload {
        void loadTrue(Sister sister);
    }
}
