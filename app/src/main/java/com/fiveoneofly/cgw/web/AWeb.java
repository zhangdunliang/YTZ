package com.fiveoneofly.cgw.web;

import android.content.Context;

import com.fiveoneofly.cgw.web.impl.AWebView;
import com.fiveoneofly.cgw.web.impl.FileChooser;


/**
 * Created by xiaochangyou on 2018/6/13.
 */

public class AWeb {
    private LayoutCreator mLayoutCreator;

    public AWeb(Builder builder) {
        mLayoutCreator = new LayoutCreator(builder.bContext);
    }

    public WebLayout getWebLayout() {
        return mLayoutCreator.getWebLayout();
    }

    public AWebView getAWebView() {
        return mLayoutCreator.getAWebView();
    }

    public UrlLoader getUrlLoader() {
        return mLayoutCreator.getUrlLoader();
    }

    public FileChooser getFileChooser() {
        return mLayoutCreator.getAWebChromeClient();
    }

    public static final class Builder {
        private Context bContext;

        public Builder(Context context) {
            this.bContext = context;
        }

        public AWeb build() {
            return new AWeb(this);
        }
    }
}
