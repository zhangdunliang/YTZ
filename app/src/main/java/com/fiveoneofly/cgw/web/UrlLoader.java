package com.fiveoneofly.cgw.web;

/**
 * Created by xiaochangyou on 2018/6/13.
 */

import android.os.Handler;
import android.os.Looper;


import com.fiveoneofly.cgw.web.impl.AWebView;
import com.fiveoneofly.cgw.utils.ThreadUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通过 UrlLoader 对 Url 处理
 */
public class UrlLoader {

    private AWebView mAWebView;
    private Handler mHandler;

    public UrlLoader(AWebView aWebView) {
        mAWebView = aWebView;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void invokeJS(String function) {
        this.loadUrl("javascript:" + function + "()");
    }

    public void invokeJS(String id, String code, String data) {
        JSONObject format = new JSONObject();
        try {
            JSONObject jsonDate;
            if (data == null) {
                jsonDate = new JSONObject();
            } else {
                if (data.contains("%")) {
                    data = data.replaceAll("%", "%25");// 替换%为转义符，防止被转义
                }
                jsonDate = new JSONObject(data);
            }
            format.put("id", id);
            format.put("code", code);
            format.put("data", jsonDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.loadUrl("javascript:webViewBridgeService('" + format.toString() + "')");
    }

    public void loadUrl(final String url) {
        if (ThreadUtil.uiThread()) {
            this.mAWebView.loadUrl(url);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl(url);
                }
            });
        }
    }

    public void loadDataWithBaseURL(final String data) {
        if (ThreadUtil.uiThread()) {
            this.mAWebView.loadDataWithBaseURL(
                    null,
                    data,
                    "text/html",
                    "utf-8",
                    null);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataWithBaseURL(data);
                }
            });
        }
    }

    public void reload() {
        if (ThreadUtil.uiThread()) {
            this.mAWebView.reload();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reload();
                }
            });
        }
    }

    public void stopLoading() {
        if (ThreadUtil.uiThread()) {
            this.mAWebView.stopLoading();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopLoading();
                }
            });
        }
    }

}
