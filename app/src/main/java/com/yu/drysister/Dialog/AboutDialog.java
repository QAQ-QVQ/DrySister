package com.yu.drysister.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yu.drysister.R;

public class AboutDialog extends Dialog {
    private Context mContext;
    private boolean cancelable;
    private onItemClicklisner cancelListener;
    private View view;

    /**
     * @param context
     * @param cancelable 点击外部，返回是否可以dismiss
     * @param cancelListener 点击回调
     */
    public AboutDialog(@NonNull Context context, boolean cancelable,View view, @Nullable onItemClicklisner cancelListener) {
        super(context,R.style.MyDialog);
        this.mContext = context;
        this.cancelable = cancelable;
        this.cancelListener = cancelListener;
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view == null) view =  LayoutInflater.from(mContext).inflate(R.layout.dialog_about, null);
        setContentView(view);//这行一定要写在前面
        setCancelable(cancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(cancelable);//控制返回键是否dismiss
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 300;
        window.setAttributes(params);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener!=null){
                    cancelListener.onclicklistner();
                }
            }
        });

    }
    public interface onItemClicklisner{
        void onclicklistner();
    }
}
