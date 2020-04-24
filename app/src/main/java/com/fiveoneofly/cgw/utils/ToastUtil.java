package com.fiveoneofly.cgw.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static Toast mToast;

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    /**
     * 输出toast
     */
    public static void showToast(Context context, String str) {
        if (context != null) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.TOP, 0, 30);// 控制toast显示的位置
            mToast.show();
        }
    }

    /**
     * 取消弹出toast
     */
    public static void cancleToast(Context context) {
        if (context != null) {
            if (mToast != null) {
                mToast.cancel();
            }
        }
    }
}
