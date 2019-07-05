package com.yu.drysister.wxapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.yu.drysister.R;

public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
    }
}
