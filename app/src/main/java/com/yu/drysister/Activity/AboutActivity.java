package com.yu.drysister.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yu.drysister.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_more)
    ImageView btnMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_back, R.id.btn_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_more:
                // TODO: 2020/1/11 关于页面的更多选项
                Toast.makeText(this, "点不动吧！点不动就对了使劲点！！", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
