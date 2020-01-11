package com.yu.drysister.Presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.yu.drysister.APP.okAPP;
import com.yu.drysister.Activity.MainActivity;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.Interface.ISisterView;
import com.yu.drysister.Interface.IloadListener;
import com.yu.drysister.Model.Model;
import com.yu.drysister.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * CREATED BY DY ON 2019/9/11.
 * TIME BY 10:19.
 **/
public class Presenter implements IPresenter, IloadListener {

    private ISisterView iView;
    private Model model;
    public static Sister sister;
    private int length, page, number;

    public Presenter(ISisterView iView) {
        this.iView = iView;
        model = new Model();
    }

    @Override
    public void initModel() {
        model.InitBean(this);
        List<ResultsBean> resultsBeans = new ArrayList<>();
        sister = new Sister(false, resultsBeans);
    }

    @Override
    public void BeanLoad(int page, int number) {
        this.page = page;
        this.number = number;
        model.BeanLoad(page, number, this);
    }

    @Override
    public void Success(Sister sister, boolean flag) {
        length = 0;
        List<ResultsBean> resultsBeans = new ArrayList<>();
        Sister sisterRe = new Sister(false, resultsBeans);
        for (ResultsBean resultsBean : sister.getResults()) {
            Glide.with(okAPP.getContext())
                    .load(resultsBean.getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            end(sister,sisterRe, flag);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            sisterRe.getResults().add(resultsBean);
                            end(sister,sisterRe, flag);
                            return true;
                        }

                    }).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                }
            });
        }

    }

    private void end(Sister sister,Sister sisterRe, boolean flag) {
        length++;
        MainActivity.toolbar.setTitle(okAPP.getContext().getResources().getString(R.string.loading) + new DecimalFormat("0%").format((length * 1.0) / sister.getResults().size()));
        if (length == sister.getResults().size()) {
            if (sisterRe.getResults().size() == 0) {
                page++;
                BeanLoad(page, number);
            } else {
                Presenter.sister.addResults(sisterRe.getResults());
                if (flag) {
                    iView.BeanLoad();
                } else {
                    iView.BeanChange();
                }
            }
        }
    }

    @Override
    public void Error(String errMsg) {
        iView.errMsg(errMsg);
    }

}
