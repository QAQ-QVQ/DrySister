package com.yu.drysister.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.yu.drysister.R;

public class ImageLoad  {

    public ImageLoad(String url, final ImageView image, final Context context){
        OkGo.<Bitmap>post(url).execute(new BitmapCallback() {
            @Override
            public void onSuccess(Response<Bitmap> response) {
                image.setImageBitmap(response.body());
            }

            @Override
            public void onError(Response<Bitmap> response) {
                image.setImageDrawable(context.getDrawable(R.drawable.icon));
            }
        });
    }
}
