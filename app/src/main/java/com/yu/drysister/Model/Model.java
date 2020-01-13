package com.yu.drysister.Model;


import com.google.gson.Gson;
import com.yu.drysister.Bean.Sister;
import com.yu.drysister.Config.ModelConfig;
import com.yu.drysister.Config.SisterConfig;
import com.yu.drysister.Interface.IloadListener;
import com.yu.drysister.Interface.IwebCallback;
import com.yu.drysister.Interface.IwebManager;
import com.yu.drysister.Utils.WebFactory;

/**
 * CREATED BY DY ON 2019/9/11.
 * TIME BY 10:48.
 **/
public class Model implements IModel {

    /**
     * @param iloadListener 加载回调
     */
    @Override
    public void BeanLoad(IloadListener iloadListener) {
        String HomeUrl = SisterConfig.SISTER_BASE_URL + SisterConfig.NUMBER + "/" + SisterConfig.PAGE;
        IwebManager iwebManager = WebFactory.getWebManager();
        iwebManager.get(HomeUrl, "pic" + SisterConfig.PAGE, new IwebCallback() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    Sister sister = new Gson().fromJson(response, Sister.class);
                    if (iloadListener != null) {
                        iloadListener.Success(sister);
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
