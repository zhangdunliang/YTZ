package com.fiveoneofly.cgw.web;

import android.content.Context;

import com.fiveoneofly.cgw.web.impl.AWebChromeClient;
import com.fiveoneofly.cgw.web.impl.AWebView;
import com.fiveoneofly.cgw.web.impl.AWebViewClient;


/**
 * Created by xiaochangyou on 2018/4/13.
 */

public class LayoutCreator {

    private WebLayout mWebLayout;
    private AWebView mAWebView;
    private UrlLoader mUrlLoader;

    private AWebChromeClient mAWebChromeClient;

    public LayoutCreator(Context context) {
//        WebIndicator webIndicator = new LineIndicator(context);
//        WebIndicator.Controller webIndicatorController = new IndicatorController(webIndicator);

        mWebLayout = new WebLayout(context);
        mAWebView = new AWebView(context);
        mAWebView.setWebViewClient(new AWebViewClient());
//        mAWebView.setWebChromeClient(mAWebChromeClient = new AWebChromeClient(webIndicatorController));
        mAWebView.setWebChromeClient(mAWebChromeClient = new AWebChromeClient());
        mAWebView.setDownloadListener(new WebDownload(context, WebDownload.DOWNLOAD_SYSTEM));

        mWebLayout.addView(mAWebView);

//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) webIndicator.layoutParams();
//        layoutParams.gravity = Gravity.TOP;

//        mWebLayout.addView((View) webIndicator, layoutParams);

        mUrlLoader = new UrlLoader(mAWebView);

    }

    public WebLayout getWebLayout() {
        return mWebLayout;
    }

    public AWebView getAWebView() {
        return mAWebView;
    }

    public UrlLoader getUrlLoader() {
        return mUrlLoader;
    }

    public AWebChromeClient getAWebChromeClient() {
        return mAWebChromeClient;
    }
}
