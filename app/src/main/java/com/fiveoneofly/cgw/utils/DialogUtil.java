package com.fiveoneofly.cgw.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by xiaochangyou on 2018/5/9.
 */

public class DialogUtil {

    public static void display(Context context,
                               String message,
                               String cancelTitle,
                               DialogInterface.OnClickListener cancelListener,
                               String confirmTitle,
                               DialogInterface.OnClickListener confirmListener) {
        if (context != null) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setPositiveButton(confirmTitle, confirmListener)
                    .setNegativeButton(cancelTitle, cancelListener)
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }
    }

    public static void displayByConfirm(Context context,
                                        CharSequence title) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    public static void displayForActivity(final Activity activity,
                                          String title) {
        if (activity != null) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    })
                    .setCancelable(false)
                    .create();
            if (!activity.isFinishing()) {
                alertDialog.show();
            }
        }
    }

}
