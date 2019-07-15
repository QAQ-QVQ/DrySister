package com.yu.drysister.Interface;

/**
 * CREATED BY DY ON 2019/7/15.
 * TIME BY 10:16.
 **/
public interface IwebCallback  {
    void onSuccess(String response);
    void onFailure(Throwable throwable);
}
