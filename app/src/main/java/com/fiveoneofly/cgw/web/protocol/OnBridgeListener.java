package com.fiveoneofly.cgw.web.protocol;

import android.app.Activity;
import android.content.Intent;

import java.util.concurrent.ExecutorService;

public interface OnBridgeListener {
    /**
     * startActivityForResult
     */
    void startActivityForResult(Plugin plugin, Intent intent, int requestCode);

    /**
     * 传递plugin给实现者
     */
    void setActivityResultCallback(Plugin plugin);

    /**
     * 当前activity
     */
    Activity getActivity();

    /**
     * 加载url
     */
    void loadUrl(String url);

    /**
     * 可用于后台任务的共享线程池。
     */
    ExecutorService getThreadPool();
}
