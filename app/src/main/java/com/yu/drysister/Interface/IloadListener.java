package com.yu.drysister.Interface;

import com.yu.drysister.Bean.Sister;

/**
 * CREATED BY DY ON 2020/1/11.
 * TIME BY 15:52.
 **/
public interface IloadListener {
    /**
     * @param sister 成功回调
     */
    void Success(Sister sister);

    /**
     * @param errMsg 错误信息
     */
    void Error(String errMsg);
}
