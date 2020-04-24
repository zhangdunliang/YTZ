package com.fiveoneofly.cgw.web;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by xiaochangyou on 2018/4/13.
 */

public class WebLayout extends FrameLayout {
    public WebLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public WebLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
