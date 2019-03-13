package com.yu.drysister.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tuyenmonkey.mkloader.MKLoader;
import com.yu.drysister.R;

public class TestActivity extends AppCompatActivity {
    MKLoader mkLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mkLoader = (MKLoader) findViewById(R.id.mkLoader);
        mkLoader.reDraw();
    }
}
