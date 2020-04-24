package com.yxjr.credit.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import com.yxjr.credit.R;

import java.lang.ref.SoftReference;

/**
 * Created by xiaochangyou on 2018/5/8.
 */
public class LoadingProgress extends Dialog {

//    public LoadingProgress(@NonNull Context context) {
//        super(context);
//    }

    public LoadingProgress(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

//    protected LoadingProgress(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//    }

//    private static LoadingProgress mLoadingProgress = null;
    private static SoftReference<LoadingProgress> mLoadingProgress = null;

    public static void showProgress(Context context) {
        if (mLoadingProgress == null || mLoadingProgress.get() == null) {
//        if (mLoadingProgress == null) {
            //自定义style文件主要让背景变成透明并去掉标题部分
            mLoadingProgress = new SoftReference<LoadingProgress>(new LoadingProgress(context, R.style.loadingProgress));
//            mLoadingProgress = new LoadingProgress(context, R.style.loadingProgress);
            LoadingProgress loadingProgress = mLoadingProgress.get();
//            LoadingProgress loadingProgress = mLoadingProgress;
            loadingProgress.setCanceledOnTouchOutside(false);// 触摸外部无法取消,必须
            loadingProgress.setTitle("");
            ProgressBar progressBar = new ProgressBar(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.RED));
                progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
            }
            loadingProgress.setContentView(progressBar);
            loadingProgress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadingProgress.setCancelable(true);//按返回键响应是否取消等待框的显示
        }
//        if (!mLoadingProgress.isShowing()) {
//            mLoadingProgress.show();
//        }
        if (!mLoadingProgress.get().isShowing()) {
            mLoadingProgress.get().show();
        }
    }

    public static void dismissProgress() {
//        if (mLoadingProgress != null) {
//            mLoadingProgress.cancel();
////            mLoadingProgress.get().dismiss();
////            mLoadingProgress.hide();
//            mLoadingProgress = null;
//        }
        if (mLoadingProgress != null && mLoadingProgress.get() != null) {
            mLoadingProgress.get().cancel();
//            mLoadingProgress.get().dismiss();
//            mLoadingProgress.hide();
            mLoadingProgress = null;
        }
    }
}
