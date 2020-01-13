package com.yu.drysister.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ImageUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowActivity extends BaseActivity {
    @BindView(R.id.loading_err_text)
    TextView loadingErrText;
    @BindView(R.id.loading_err)
    RelativeLayout loadingErr;
    @BindView(R.id.show_image)
    ImageView showImage;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_dowonload)
    ImageView btnDowonload;
    @BindView(R.id.btn_share)
    ImageView btnShare;

    private ResultsBean resultsBean;
    private int page;
    private int position;
    private String destFileName = "sister" + page + position + ".jpg";//文件名 按照 妹子加当前页加索引
    private Context mcontext;
    private static String destFileDir;//下载后文件夹名称
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        //进入动画
        getWindow().setEnterTransition(new Fade().setDuration(500).setInterpolator(new AccelerateInterpolator()));
        initData();
    }

    private void initData() {
        mcontext = this;
        destFileDir = getResources().getString(R.string.file_path);
        page = Integer.parseInt(getIntent().getStringExtra("page"));
        position = Integer.parseInt(getIntent().getStringExtra("position"));
        resultsBean = (ResultsBean) getIntent().getSerializableExtra("ResultsBean");
//        if (SisterDBHelper.getInstance().getSisterFlagId(DbLikeDefine.DB_LIKE, resultsBean.get_id())) {
//            btnLike.setChecked(true);
//        }
//        if (SisterDBHelper.getInstance().getSisterFlagId(DbLikeDefine.DB_COLLECTION, resultsBean.get_id())) {
//            btnGive.setChecked(true);
//        }
//        if (getFilesAllName(destFileName)) {
//            btnDowonload.setImageDrawable(getDrawable(R.drawable.ic_photo_downloaded));
//        }
        load();
    }

    //下载图片
    private void dwonloadImage() {
        if (getFilesAllName(destFileName)) {
            ToastUtils.showShort(getResources().getString(R.string.file_already_exist));
            btnDowonload.setImageDrawable(getDrawable(R.drawable.ic_photo_downloaded));
        } else {
            OkGo.<File>get(resultsBean.getUrl())
                    .execute(new FileCallback(destFileDir, destFileName) {
                        @Override
                        public void onSuccess(Response<File> response) {
                            ToastUtils.showShort(getResources().getString(R.string.download_true));
                            btnDowonload.setImageDrawable(getDrawable(R.drawable.ic_photo_downloaded));
                        }

                        @Override
                        public void downloadProgress(Progress progress) {
                            Log.e("EasyHttpActivity", "当前下载了:" + progress.currentSize + ",总共有:" + progress.totalSize + ",下载速度为:" + progress.speed);//这个totalSize一直是初始值-1，很尴尬
                            Log.e("xinxi", progress.toString());
                        }

                        @Override
                        public void onError(Response<File> response) {
                            ToastUtils.showShort(getResources().getString(R.string.download_false));
                        }
                    });
        }
    }

    /**
     * @param FileName
     * @return
     */
    //获取文件夹下所有文件名称并判断是否重名
    public static boolean getFilesAllName(String FileName) {
        File filePath = new File(destFileDir);
        if (!filePath.exists())
            filePath.mkdirs();//不存在就新建一个目录
        File[] files = filePath.listFiles();
        for (int i = 0; i < files.length; i++) {
//            Log.e(TAG, files[ISisterListener].getName());
            if (FileName.equals(files[i].getName())) {
                return true;
            }
        }
        return false;
    }

    private void load() {
        Glide.with(this)
                .load(resultsBean.getUrl())
                //.placeholder(R.drawable.icon)
                .listener(new RequestListener<Drawable>() {
                    //加载图片失败
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingErr.setVisibility(View.VISIBLE);
                        loadingErrText.setText(getResources().getString(R.string.loading_err));
                        loadingErr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadingErrText.setText(getResources().getString(R.string.loading));
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
//                        btnGroup.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        showImage.setImageDrawable(resource);
                        Bitmap bitmap = ImageUtils.drawable2Bitmap(resource);
                        uri = getImageUri(mcontext, bitmap);
                        Log.e("uri:", uri.toString());
                    }
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ShowActivity.this.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }
    //存储点赞收藏数据到数据库
//    private void saveDb() {
//        if (SisterDBHelper.getInstance().getSisterFlagId(DbLikeDefine.DB_LIKE, resultsBean.get_id())) {
//            if (!btnLike.isChecked()) {
//                //数据库中存在同时取消点赞，就删除
//                SisterDBHelper.getInstance().deleteSisterByFlagId(DbLikeDefine.DB_LIKE, resultsBean.get_id());
//            }
//            if (!btnGive.isChecked()) {
//                SisterDBHelper.getInstance().deleteSisterByFlagId(DbLikeDefine.DB_COLLECTION, resultsBean.get_id());
//            }
//        } else {
//            //数据库不存在同时点赞，就插入数据库
//            if (btnLike.isChecked()) {
//                SisterDBHelper.getInstance().insertSister(resultsBean, DbLikeDefine.DB_LIKE);
//            }
//            if (btnGive.isChecked()) {
//                SisterDBHelper.getInstance().insertSister(resultsBean, DbLikeDefine.DB_COLLECTION);
//            }
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().setReturnTransition(new Explode().setDuration(500));
//        saveDb();
    }

    //R.id.btn_like, R.id.btn_give, R.id.btn_dowonload, R.id.btn_share,
    @OnClick({R.id.btn_back, R.id.btn_share,R.id.btn_dowonload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.btn_like:
//                btnLike.toggle();
//                break;
//            case R.id.btn_give:
//                btnGive.toggle();
//                break;
            case R.id.btn_dowonload:
                dwonloadImage();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                if(getPackageManager().queryIntentActivities(intent,0).size() > 0)
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_to)));
                break;
        }
    }
}