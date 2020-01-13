package com.yu.drysister.Interface;

import com.yu.drysister.Config.ModelConfig;

/**
 * CREATED BY DY ON 2020/1/11.
 * TIME BY 15:37.
 **/
public interface ISisterView {

    /**
     * @param errMsg 错误信息
     */
    void errMsg(int flag,String errMsg);

    /**
     * 初始化成功
     */
    void ToInitSuccess();

    /**
     * 加载成功
     */
    void ToLoadSuccess();


}
