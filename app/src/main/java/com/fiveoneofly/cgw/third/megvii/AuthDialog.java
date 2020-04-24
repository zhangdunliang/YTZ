package com.fiveoneofly.cgw.third.megvii;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxjr.credit.R;

/**
 * Created by xiaochangyou on 2018/5/11.
 */

public class AuthDialog {

    private Dialog dialog;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private Button mButton;


    public AuthDialog(Context context) {
        dialog = new Dialog(context, android.R.style.Theme_Wallpaper_NoTitleBar);
        dialog.setContentView(R.layout.yxjr_credit_face_auth);
        dialog.setCancelable(true);
        mProgressBar = (ProgressBar) dialog.findViewById(R.id.yx_credit_WarrantyBar);
        mTextView = (TextView) dialog.findViewById(R.id.yx_credit_WarrantyText);
        mButton = (Button) dialog.findViewById(R.id.yx_credit_againWarrantyBtn);
    }

    public void showAuth() {
        if (dialog != null)
            dialog.dismiss();
        mButton.setVisibility(View.GONE);
        mTextView.setText("正在授权...");
        mProgressBar.setVisibility(View.VISIBLE);
        if (dialog != null)
            dialog.show();
    }

    public void showAuthFail(View.OnClickListener againClick) {
        if (dialog != null)
            dialog.dismiss();
        mButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("联网授权失败，请点击按钮重新授权");
        mButton.setOnClickListener(againClick);
        if (dialog != null)
            dialog.show();
    }

    public void showAuthSuccess() {
        dismiss();
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }
}
