package com.yu.drysister.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yu.drysister.R;

public class DialogFromBottom  extends Dialog {
    private OnItemClicklisner onItemClicklisner;
    private boolean iscancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable;//控制返回键是否dismiss
    private View view;
    private Context context;
    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public DialogFromBottom(Context context, View view, boolean isCancelable,boolean isBackCancelable,OnItemClicklisner onItemClicklisner) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.view = view;
        this.iscancelable = isCancelable;
        this.isBackCancelable = isBackCancelable ;
        this.onItemClicklisner = onItemClicklisner;
    }

    /**
     * @param context
     * @param isCancelable 点击外部dismiss
     * @param isBackCancelable 控制返回键是否dismiss
     * @param onItemClicklisner 点击回调
     */
    public DialogFromBottom(Context context, boolean isCancelable,boolean isBackCancelable,OnItemClicklisner onItemClicklisner) {
        super(context, R.style.MyDialog);
        View root =  LayoutInflater.from(context).inflate(R.layout.dialog_buttom, null);
        this.context = context;
        this.view = root;
        this.iscancelable = isCancelable;
        this.isBackCancelable = isBackCancelable ;
        this.onItemClicklisner = onItemClicklisner;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClicklisner!=null){
                    onItemClicklisner.onclicklistner();
                }
            }
        });
        setContentView(view);//这行一定要写在前面
        setCancelable(iscancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(isBackCancelable);//控制返回键是否dismiss
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 300;
        window.setAttributes(params);
    }

    public interface OnItemClicklisner{
        void onclicklistner();
    }
}
