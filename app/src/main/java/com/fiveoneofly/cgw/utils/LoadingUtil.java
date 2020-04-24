package com.fiveoneofly.cgw.utils;

import android.content.Context;

import com.fiveoneofly.cgw.app.widget.LoadingProgress;


/**
 * Created by xiaochangyou on 2018/5/8.
 */

public class LoadingUtil {

    //显示
    public static void display(Context context) {
        LoadingProgress.showProgress(context);
    }

    //销毁
    public static void dismiss() {
        LoadingProgress.dismissProgress();
    }
}
