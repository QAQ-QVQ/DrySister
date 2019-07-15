package com.yu.drysister.Utils;

import com.yu.drysister.Interface.IwebManager;

/**
 * CREATED BY DY ON 2019/7/15.
 * TIME BY 10:25.
 **/
public class WebFactory {
    public static IwebManager getWebManager(){
        return OkgoManager.getInstance();
    }
}
