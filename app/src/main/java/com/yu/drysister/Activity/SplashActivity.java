package com.yu.drysister.Activity;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.yu.drysister.R;
import com.yu.drysister.Utils.ImageLoad;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {
    private String url; //;https://acg.toubiec.cn/random?return=json// 随机二次元图片接口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        url = getResources().getString(R.string.sister_random);
        ImageView splash = findViewById(R.id.splash_image);
        //取消本地缓存
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.icon)
                .error(R.drawable.icon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(this).load(url).apply(options).placeholder(R.drawable.icon).into(splash);
//        new ImageLoad(url,splash,this);
         RelativeLayout layoutSplash=(RelativeLayout) findViewById(R.id.activity_splash);
        AlphaAnimation alphaAnimation=new AlphaAnimation(0.1f,1.0f);
        alphaAnimation.setDuration(3000);//设置动画播放时长1000毫秒（1秒）
        layoutSplash.startAnimation(alphaAnimation);
        //设置动画监听
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ToastUtils.showShort(getResources().getString(R.string.login));
            }
            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                //页面的跳转
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SPUtils.getInstance().getBoolean("splash")){
            Intent intent=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
