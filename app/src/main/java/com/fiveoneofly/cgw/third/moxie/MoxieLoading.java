package com.fiveoneofly.cgw.third.moxie;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.fiveoneofly.cgw.R;

class MoxieLoading extends ProgressDialog {

    MoxieLoading(Context context) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1.0f; // 设置透明度为0.5
            window.setAttributes(lp);
            window.setDimAmount(0f);
            this.setMessage(context.getString(R.string.loading));
            this.setCancelable(false);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
