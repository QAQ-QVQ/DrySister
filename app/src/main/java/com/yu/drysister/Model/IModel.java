package com.yu.drysister.Model;

import android.content.Context;

import com.yu.drysister.Bean.Sister;
import com.yu.drysister.Interface.IloadListener;


/**
 * CREATED BY DY ON 2019/9/11.
 * TIME BY 10:32.
 **/
public interface IModel {
    /**
     *
     * @param iloadListener 加载回调
     */
    void BeanLoad(IloadListener iloadListener);
}
