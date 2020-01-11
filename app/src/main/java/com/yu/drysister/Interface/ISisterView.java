package com.yu.drysister.Interface;

import com.yu.drysister.Bean.Sister;

/**
 * CREATED BY DY ON 2020/1/11.
 * TIME BY 15:37.
 **/
public interface ISisterView {
    void BeanInit();//初始化
    void BeanChange();//刷新加载
    void errMsg(String errMsg);//错误信息
    void BeanLoad();//第一次加载
}
