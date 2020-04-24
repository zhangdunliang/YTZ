package com.fiveoneofly.cgw.web.impl;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fiveoneofly.cgw.utils.NetUtil;


/**
 * Created by xiaochangyou on 2018/6/13.
 */

public class AWebViewClient extends WebViewClient {

    //    private WeakReference<WebViewClient> mWebViewClient;
    private WebViewClient mWebViewClientProxy;

    public void setWebViewClientProxy(WebViewClient webViewClient) {
        mWebViewClientProxy = webViewClient;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean self = true;
        if (!NetUtil.networked(view.getContext())) {
            Toast.makeText(view.getContext(), "请检查网络设置!", Toast.LENGTH_SHORT).show();
            view.loadUrl(AWebView.WEB_ERROR_URL);
        } else {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                self = super.shouldOverrideUrlLoading(view, url);
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "未安装相关APP！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

//        if (null != mWebViewClient && null != mWebViewClient.get()) {
//            return mWebViewClient.get().shouldOverrideUrlLoading(view, url);
//        }
        return self;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageFinished(final WebView view, String url) {

//        EventBus.getDefault().post(new InvokeJsEvent("", "000",null));

//        if (null != mWebViewClientProxy && null != mWebViewClientProxy.get()) {
//            mWebViewClientProxy.get().onPageFinished(view, url);
//        }
        if (null != mWebViewClientProxy) {
            mWebViewClientProxy.onPageFinished(view, url);
        }

        super.onPageFinished(view, url);
    }


    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Toast.makeText(view.getContext(), "加载错误!", Toast.LENGTH_SHORT).show();
        view.loadUrl(AWebView.WEB_ERROR_URL);

//        if (null != mWebViewClientProxy && null != mWebViewClientProxy.get()) {
//            mWebViewClientProxy.get().onReceivedError(view, errorCode, description, failingUrl);
//        }
        if (null != mWebViewClientProxy) {
            mWebViewClientProxy.onReceivedError(view, errorCode, description, failingUrl);
        }

        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        // handler.cancel(); // Android默认的处理方式
        handler.proceed(); // 接受所有网站的证书
        // handleMessage(Message msg); // 进行其他处理


//        if (null != mWebViewClientProxy && null != mWebViewClientProxy.get()) {
//            mWebViewClientProxy.get().onReceivedSslError(view, handler, error);
//        }
        if (null != mWebViewClientProxy) {
            mWebViewClientProxy.onReceivedSslError(view, handler, error);
        }

        super.onReceivedSslError(view, handler, error);
    }

}
