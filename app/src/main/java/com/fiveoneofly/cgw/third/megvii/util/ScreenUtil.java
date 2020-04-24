package com.fiveoneofly.cgw.third.megvii.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {

    private static int mWidth;//竖屏屏幕宽度
    private static int mHeight;//竖屏屏幕高度

    private static void initialize(Context context) {
        if (mWidth == 0 || mHeight == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(metrics);

            mWidth = metrics.widthPixels;
            mHeight = metrics.heightPixels;

            if (mWidth > mHeight) {//如果是横屏，需换一下值，
                int temp = mWidth;
                mWidth = mHeight;
                mHeight = temp;
            }
        }
    }

    //isVertical 是否竖屏，是横屏则高为宽，宽为高；getHeight一样
    public static int getWidth(Context context, boolean isVertical) {
        if (mWidth == 0 || mHeight == 0) {
            initialize(context);
        }
        if (!isVertical) {
            return mHeight;
        }
        return mWidth;
    }

    public static int getHeight(Context context, boolean isVertical) {
        if (mHeight == 0) {
            initialize(context);
        }
        if (!isVertical) {
            return mWidth;
        }
        return mHeight;
    }

}