package com.yu.drysister.Presenter;

import android.content.Context;

import com.yu.drysister.Bean.Sister;

/**
 * CREATED BY DY ON 2019/9/11.
 * TIME BY 10:17.
 **/
public interface IPresenter {
    void initModel();//将view层传递到model层
    void BeanLoad(int page, int number);
}
