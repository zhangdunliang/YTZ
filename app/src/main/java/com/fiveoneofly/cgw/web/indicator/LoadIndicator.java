package com.fiveoneofly.cgw.web.indicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fiveoneofly.cgw.utils.LoadingUtil;

/**
 * Created by xiaochangyou on 2018/6/13.
 */

public class LoadIndicator extends FrameLayout implements WebIndicator {

    public LoadIndicator(@NonNull Context context) {
        super(context);
    }

    public LoadIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void reset() {

    }

    @Override
    public void setProgress(int progress) {

    }

    @Override
    public void show() {
        LoadingUtil.display(this.getContext());
    }

    @Override
    public void hide() {
        LoadingUtil.dismiss();
    }

    @Override
    public ViewGroup.LayoutParams layoutParams() {
        return null;
    }
}
