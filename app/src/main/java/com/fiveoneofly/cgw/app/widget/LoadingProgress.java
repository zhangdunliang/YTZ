package com.fiveoneofly.cgw.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import com.fiveoneofly.cgw.R;

import java.lang.ref.SoftReference;

/**
 * Created by xiaochangyou on 2018/6/13.
 */
public class LoadingProgress extends Dialog {

    private LoadingProgress(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    private static SoftReference<LoadingProgress> mLoadingProgress = null;

    public static void showProgress(Context context) {
        if (mLoadingProgress == null || mLoadingProgress.get() == null) {
            mLoadingProgress = new SoftReference<>(new LoadingProgress(context, R.style.loadingProgress));
            LoadingProgress loadingProgress = mLoadingProgress.get();
            loadingProgress.setCanceledOnTouchOutside(false);// 触摸外部无法取消,必须
            loadingProgress.setTitle("");
            ProgressBar progressBar = new ProgressBar(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.RED));
                progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
            }
            loadingProgress.setContentView(progressBar);
            if (loadingProgress.getWindow() != null)
                loadingProgress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loadingProgress.setCancelable(true);//按返回键响应是否取消等待框的显示
        }
        if (!mLoadingProgress.get().isShowing()) {
            mLoadingProgress.get().show();
        }
    }

    public static void dismissProgress() {
        if (mLoadingProgress != null && mLoadingProgress.get() != null) {
            mLoadingProgress.get().cancel();
            mLoadingProgress = null;
        }
    }
}
