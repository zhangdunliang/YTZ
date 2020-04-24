package com.fiveoneofly.cgw.web.indicator;


import android.view.ViewGroup;

/**
 * WebView加载进度显示器
 */
public interface WebIndicator {
    //重置
    void reset();

    //设值
    void setProgress(int progress);

    //显示
    void show();

    //隐藏
    void hide();

    ViewGroup.LayoutParams layoutParams();

    abstract class Controller {

        public abstract void show();

        public abstract void hide();

        public abstract void reset();

        public abstract void progress(int pro);

    }
}
