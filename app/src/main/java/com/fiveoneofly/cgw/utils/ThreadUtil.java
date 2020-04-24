package com.fiveoneofly.cgw.utils;

import android.os.Looper;

/**
 * Created by xiaochangyou on 2018/4/16.
 */

public class ThreadUtil {

    /**
     * 当前线程是否为 UI 线程
     *
     * @return
     */
    public static boolean uiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
