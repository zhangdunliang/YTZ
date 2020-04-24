package com.fiveoneofly.cgw.controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.fiveoneofly.cgw.utils.SharedUtil;
import com.yxjr.credit.constants.SharedKey;

/**
 * Created by xiaochangyou on 2018/4/18.
 */

public class BatteryController {

    private BroadcastReceiver mBatteryReceiver;
    private Context mContext;

    private BatteryController(Context context) {
        this.mContext = context;
        this.mBatteryReceiver = new BatteryReceiver();
    }

    @SuppressLint("StaticFieldLeak")
    private static BatteryController mInstance = null;

    public static BatteryController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BatteryController.class) {
                if (mInstance == null) {
                    mInstance = new BatteryController(context);
                }
            }
        }
        return mInstance;
    }


    public void refresh() {
        SharedUtil.save(mContext, SharedKey.BATTERY, "");
        register();
    }

    private void register() {
        if (mBatteryReceiver != null && mContext != null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            mContext.registerReceiver(mBatteryReceiver, filter);// 注册BroadcastReceiver
        }
    }

    private void unRergister() {
        if (mBatteryReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mBatteryReceiver);// 解除电量广播
        }
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent && null != intent.getExtras()) {
                int current = intent.getExtras().getInt("level");// 获得当前电量
                int total = intent.getExtras().getInt("scale");// 获得总电量
                int percent = current * 100 / total;
                SharedUtil.save(mContext, SharedKey.BATTERY, percent + "");
            }
            unRergister();
        }
    }
}
