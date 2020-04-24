package com.fiveoneofly.cgw.third.xinge;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

public class Xinge {
    /**
     * 信鸽平台QQ账号：1606649453
     */

    public static void initialize(Context context) {
        XGPushManager.registerPush(context.getApplicationContext(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
//        XGPushManager.bindAccount(getApplicationContext(), "XINGE");
    }
}
