package com.yu.drysister.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jaren.lib.view.LikeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Dialog.DialogFromBottom;
import com.yu.drysister.R;
import com.yu.drysister.Utils.DbLikeDefine;
import com.yu.drysister.Utils.SisterDBHelper;

import java.io.File;

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
    @BindView(R.id.btn_like)
    LikeView btnLike;//收藏
    @BindView(R.id.btn_give)
    LikeView btnGive;//点赞
    @BindView(R.id.btn_dowonload)
    ImageView btnDowonload;
    @BindView(R.id.btn_share)
    ImageView btnShare;//分享
    @BindView(R.id.btn_group)
    RelativeLayout btnGroup;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ResultsBean resultsBean;
    private int page;
    private int position;
    private String destFileName = "妹子" + page + position + ".jpg";//文件名 按照 妹子加当前页加索引
    private Context mcontext;
    private static final String destFileDir = "/storage/emulated/0/Android/data/com.yu.drysister/SisterImage";//下载后文件夹名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mcontext = this;
        page = Integer.parseInt(getIntent().getStringExtra("page"));
        position = Integer.parseInt(getIntent().getStringExtra("position"));
        resultsBean = (ResultsBean) getIntent().getSerializableExtra("ResultsBean");
        if (SisterDBHelper.getInstance().getSisterFlagId(DbLikeDefine.DB_LIKE, resultsBean.get_id())) {
            btnLike.setChecked(true);
        }
        if (SisterDBHelper.getInstance().getSisterFlagId(DbLikeDefine.DB_COLLECTION, resultsBean.get_id())) {
            btnGive.setChecked(true);
        }
        if (getFilesAllName(destFileName)) {
            btnDowonload.setImageDrawable(getDrawable(R.drawable.ic_photo_downloaded));
        }
        load();
    }

    //下载图片
    private void dwonloadImage() {
        if (getFilesAllName(destFileName)) {
            ToastUtils.showShort("文件已存在");
            btnDowonload.setImageDrawable(getDrawable(R.drawable.ic_photo_downloaded));
        } else {
            OkGo.<File>get(resultsBean.getUrl())
                    .execute(new FileCallback(destFileDir, destFileName) {
                        @Override
                        public void onSuccess(Response<File> response) {
                            ToastUtils.showShort(response.body().getName() + "下载成功");
                            btnDowonload.setImageDrawable(getDrawable(R.drawable.ic_photo_downloaded));
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
//            Log.e(TAG, files[i].getName());
            if (FileName.equals(files[i].getName())) {
                return true;
            }
        }
        return false;
    }

    private void load() {
        Glide.with(this).load(resultsBean.getUrl())
                //.placeholder(R.drawable.icon)
                .listener(new RequestListener<Drawable>() {
                    //加载图片失败
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingErr.setVisibility(View.VISIBLE);
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
                        btnGroup.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(showImage);
    }

    //存储点赞收藏数据到数据库
    private void saveDb() {
        if (SisterDBHelper.getInstance().getSisterFlagId(DbLikeDefine.DB_LIKE, resultsBean.get_id())) {
            if (!btnLike.isChecked()) {
                //数据库中存在同时取消点赞，就删除
                SisterDBHelper.getInstance().deleteSisterByFlagId(DbLikeDefine.DB_LIKE, resultsBean.get_id());
            }
            if (!btnGive.isChecked()) {
                SisterDBHelper.getInstance().deleteSisterByFlagId(DbLikeDefine.DB_COLLECTION, resultsBean.get_id());
            }
        } else {
            //数据库不存在同时点赞，就插入数据库
            if (btnLike.isChecked()) {
                SisterDBHelper.getInstance().insertSister(resultsBean, DbLikeDefine.DB_LIKE);
            }
            if (btnGive.isChecked()) {
                SisterDBHelper.getInstance().insertSister(resultsBean, DbLikeDefine.DB_COLLECTION);
            }
        }
    }

    private void showDialog() {
        new DialogFromBottom(mcontext, true, true, new DialogFromBottom.onItemClicklisner() {
            @Override
            public void onclicklistner() {
                Toast.makeText(mcontext, "弹出框", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                //根据key来区分自定义按钮的类型，并进行对应的操作
                if (snsPlatform.mKeyword.equals("wechat")) {
                    //  Toast.makeText(ShareActivity.this,"add buttonsuccess",Toast.LENGTH_LONG).show();
                } else if (snsPlatform.mKeyword.equals("qq")) {

                }

            } else {//社交平台的分享行为
                new ShareAction(ShowActivity.this)
                        .setPlatform(share_media)
                        .setCallback(shareListener)
                        .withText("多平台分享")
                        .share();
            }
        }
    };

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            UMImage image = new UMImage(ShowActivity.this, resultsBean.getUrl());//网络图片
            image.setThumb(image);
            image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
            image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享压缩格式设置
            image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
            new ShareAction(ShowActivity.this).withMedia(image).share();
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtils.showShort("分享成功!");
            //  Toast.makeText(ShareDetailActivity.this,"成功了",Toast.LENGTH_LONG).show()；
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtils.showShort("分享失败！" + t.getMessage());
            //  Toast.makeText(ShareDetailActivity.this,"失败"+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.showShort("取消分享！");
            //   Toast.makeText(ShareDetailActivity.this,"取消了",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDb();
    }

    @OnClick({R.id.btn_like, R.id.btn_give, R.id.btn_dowonload, R.id.btn_share, R.id.btn_back, R.id.btn_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_like:
                btnLike.toggle();
                break;
            case R.id.btn_give:
                btnGive.toggle();
                break;
            case R.id.btn_dowonload:
                dwonloadImage();
                break;
            case R.id.btn_share:
                ToastUtils.showShort("待实现");
//                new ShareAction(this).withText("请选择分享平台").setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN)
//                        .setCallback(shareListener)
                //  .setShareboardclickCallback(shareBoardlistener)
//                        .open();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_more:
                // TODO: 2019/7/3 更多按钮
                //   ToastUtils.showShort("待实现");
                showDialog();
                break;
        }
    }
}