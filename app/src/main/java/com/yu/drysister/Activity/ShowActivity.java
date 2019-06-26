package com.yu.drysister.Activity;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.yu.drysister.R;

import java.io.File;

public class ShowActivity extends BaseActivity {
    private ImageView showImage;
    private RelativeLayout loadingErr;
    private TextView loadingErrText;
    private String url;
    private String page;
    private String position;
    private String destFileName;//文件名 按照 妹子加当前页加索引
    private static final String destFileDir = "/storage/emulated/0/Android/data/com.yu.drysister/SisterImage";//下载后文件夹名称
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        showImage = findViewById(R.id.show_image);
        loadingErr = findViewById(R.id.loading_err);
        loadingErrText = findViewById(R.id.loading_err_text);
        url = getIntent().getStringExtra("url");
        page = getIntent().getStringExtra("page");
        position = getIntent().getStringExtra("position");
        load();
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dwonloadImage();
            }
        });
    }
    //下载图片
    private void dwonloadImage() {
        destFileName = "妹子" + page + position + ".jpg";
        if (getFilesAllName(destFileName)) {
            ToastUtils.showShort("文件已存在");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkGo.<File>get(url)
                            .execute(new FileCallback(destFileDir, destFileName) {
                                @Override
                                public void onSuccess(Response<File> response) {
                                    ToastUtils.showShort(response.body().getName() + "下载成功");
                                }

                                @Override
                                public void downloadProgress(Progress progress) {
                                    Log.e("EasyHttpActivity", "当前下载了:" + progress.currentSize + ",总共有:" + progress.totalSize + ",下载速度为:" + progress.speed);//这个totalSize一直是初始值-1，很尴尬
                                    Log.e("xinxi", progress.toString());
                                }

                                @Override
                                public void onError(Response<File> response) {
                                    ToastUtils.showShort(response.body().getName() + "下载失败");
                                }
                            });
                }
            }).start();
        }
    }
    //获取文件夹下所有文件名称并判断是否重名
    public static boolean getFilesAllName(String FileName) {
        File filePath = new File(destFileDir);
        if (!filePath.exists())
            filePath.mkdirs();//不存在就新建一个目录
        File[] files = filePath.listFiles();
        for (int i = 0; i < files.length; i++) {
//            Log.e(TAG, files[i].getName());
            if (FileName.equals(files[i].getName())) {
                return true;
            }
        }
        return false;
    }
    private void load() {
        Glide.with(this).load(url)
                //.placeholder(R.drawable.icon)
                .listener(new RequestListener<Drawable>() {
                    //加载图片失败
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingErr.setVisibility(View.VISIBLE);
                        showImage.setVisibility(View.GONE);
                        loadingErrText.setText("加载失败！点击刷新！");
                        loadingErr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadingErrText.setText("刷新中...");
                                load();
                            }
                        });
                        return true;
                    }
                    //加载图片成功
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingErr.setVisibility(View.GONE);
                        showImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(showImage);
    }
}
