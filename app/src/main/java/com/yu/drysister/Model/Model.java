package com.yu.drysister.Model;


import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.yu.drysister.APP.okAPP;
import com.yu.drysister.Activity.MainActivity;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.Config.SisterConfig;
import com.yu.drysister.Interface.IloadListener;
import com.yu.drysister.Interface.IwebCallback;
import com.yu.drysister.Interface.IwebManager;
import com.yu.drysister.R;
import com.yu.drysister.Utils.WebFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * CREATED BY DY ON 2019/9/11.
 * TIME BY 10:48.
 **/
public class Model implements IModel {
    private int page = 2;//当前页数
    private int number = 28; //当前请求数目

    @Override
    public void InitBean(IloadListener iloadListener) {
        LoadUrl(page, number,true, iloadListener);
    }

    @Override
    public void BeanLoad(int page, int number, IloadListener iloadListener) {
        LoadUrl(page, number,false, iloadListener);
    }

    private void LoadUrl(int page, int number,boolean flag, IloadListener iloadListener) {
        String HomeUrl = SisterConfig.SISTER_BASE_URL + number + "/" + page;
        IwebManager iwebManager = WebFactory.getWebManager();
        iwebManager.get(HomeUrl, "pic" + page, new IwebCallback() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    Sister sister = new Gson().fromJson(response, Sister.class);
                    if (iloadListener != null) {
                        iloadListener.Success(sister,flag);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (iloadListener != null) {
                    iloadListener.Error(throwable.toString());
                }
            }
        });
    }
}
