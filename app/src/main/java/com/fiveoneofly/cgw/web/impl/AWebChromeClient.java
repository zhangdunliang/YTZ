package com.fiveoneofly.cgw.web.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.fiveoneofly.cgw.third.event.OpenCameraEvent;
import com.fiveoneofly.cgw.third.event.OpenFileManageEvent;
import com.fiveoneofly.cgw.third.event.OpenGalleryEvent;
import com.fiveoneofly.cgw.web.indicator.WebIndicator;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by xiaochangyou on 2018/6/13.
 * <p>
 * WebChromeClient
 */
public class AWebChromeClient extends WebChromeClient implements FileChooser {

    private WebIndicator.Controller mWebIndicatorController;
    private Context mContext;
    //    private WeakReference<WebChromeClient> mWebChromeClientProxy;
    private WebChromeClient mWebChromeClientProxy;

//    public AWebChromeClient(WebIndicator.Controller webIndicatorController) {
//        this.mWebIndicatorController = webIndicatorController;
//    }

    public void setIndicator(WebIndicator.Controller webIndicatorController) {
        this.mWebIndicatorController = webIndicatorController;
    }

    public void setWebChromeClientProxy(WebChromeClient webChromeClient) {
//        mWebChromeClientProxy = new WeakReference<>(webChromeClient);
        mWebChromeClientProxy = webChromeClient;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

//        if (null != mWebChromeClientProxy && null != mWebChromeClientProxy.get()) {
//            mWebChromeClientProxy.get().onReceivedTitle(view, title);
//        }
        if (null != mWebChromeClientProxy) {
            mWebChromeClientProxy.onReceivedTitle(view, title);
        }

        super.onReceivedTitle(view, title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

        if (mWebIndicatorController != null) {
            mWebIndicatorController.progress(newProgress);
        }
        if (null == mContext)
            mContext = view.getContext();

//        if (null != mWebChromeClientProxy && null != mWebChromeClientProxy.get()) {
//            mWebChromeClientProxy.get().onProgressChanged(view, newProgress);
//        }
        if (null != mWebChromeClientProxy) {
            mWebChromeClientProxy.onProgressChanged(view, newProgress);
        }

        super.onProgressChanged(view, newProgress);
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        showDialog();
        mValueCallback = valueCallback;

//        if (null != mWebChromeClientProxy && null != mWebChromeClientProxy.get()) {
//            invokeWebChromeClient(this.mWebChromeClientProxy.get(), "openFileChooser", new Object[]{valueCallback}, ValueCallback.class);
//        }
        if (null != mWebChromeClientProxy) {
            invokeWebChromeClient(this.mWebChromeClientProxy, "openFileChooser", new Object[]{valueCallback}, ValueCallback.class);
        }
    }

    // For Android  >= 3.0
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        showDialog();
        mValueCallback = valueCallback;

//        if (null != mWebChromeClientProxy && null != mWebChromeClientProxy.get()) {
//            invokeWebChromeClient(this.mWebChromeClientProxy.get(), "openFileChooser", new Object[]{valueCallback, acceptType}, ValueCallback.class, String.class);
//        }
        if (null != mWebChromeClientProxy) {
            invokeWebChromeClient(this.mWebChromeClientProxy, "openFileChooser", new Object[]{valueCallback, acceptType}, ValueCallback.class, String.class);
        }
    }

    // For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        showDialog();
        mValueCallback = valueCallback;

//        if (null != mWebChromeClientProxy && null != mWebChromeClientProxy.get()) {
//            invokeWebChromeClient(this.mWebChromeClientProxy.get(), "openFileChooser", new Object[]{valueCallback, acceptType, capture}, ValueCallback.class, String.class, String.class);
//        }
        if (null != mWebChromeClientProxy) {
            invokeWebChromeClient(this.mWebChromeClientProxy, "openFileChooser", new Object[]{valueCallback, acceptType, capture}, ValueCallback.class, String.class, String.class);
        }
    }

    // For Android >= 5.0
    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        showDialog();
        mValueCallbacks = filePathCallback;

//        if (null != mWebChromeClientProxy && null != mWebChromeClientProxy.get()) {
//            return mWebChromeClientProxy.get().onShowFileChooser(webView, filePathCallback, fileChooserParams);
//        }
        if (null != mWebChromeClientProxy) {
            return mWebChromeClientProxy.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }

        return true;
    }

    private void invokeWebChromeClient(WebChromeClient webChromeClient, String mothed, Object[] objs, Class... clazzs) {
        try {
            if (webChromeClient == null) {
                return;
            }
            Class<?> clazz = webChromeClient.getClass();
            Method mMethod = clazz.getMethod(mothed, clazzs);
            mMethod.invoke(webChromeClient, objs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallbacks;

    @Override
    public void returnFilePath(String path) {
        Uri uri = Uri.fromFile(new File(path));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 && null != mValueCallbacks) {//18
            mValueCallbacks.onReceiveValue(new Uri[]{uri});
        } else {
            if (null != mValueCallback)
                mValueCallback.onReceiveValue(uri);
            else if (null != mValueCallbacks)
                mValueCallbacks.onReceiveValue(new Uri[]{uri});
        }
        if (null != mValueCallback)
            mValueCallback = null;
        if (null != mValueCallbacks)
            mValueCallbacks = null;
    }

    private void showDialog() {
        new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setItems(new String[]{"拍照", "相册选择", "文件选择"}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            EventBus.getDefault().post(new OpenCameraEvent());
                        } else if (which == 1) {
                            EventBus.getDefault().post(new OpenGalleryEvent());
                        } else if (which == 2) {
                            EventBus.getDefault().post(new OpenFileManageEvent());
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }
}
