package com.yu.drysister.Interface;

import com.yu.drysister.Bean.Sister;

/**
 * CREATED BY DY ON 2020/1/11.
 * TIME BY 15:52.
 **/
public interface IloadListener {
    void Success(Sister sister,boolean flag);
    void Error(String errMsg);
}
