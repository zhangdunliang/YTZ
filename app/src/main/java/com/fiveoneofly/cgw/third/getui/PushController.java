package com.fiveoneofly.cgw.third.getui;

import android.content.Context;

import com.fiveoneofly.cgw.constants.CacheKey;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.igexin.sdk.PushManager;

public class PushController {

    /**
     * 官方话：反复初始化并不会有什么副作用
     * <p>
     * 进入页、首页、登录页 → onResume 中使用
     */
    public static void initialize(Context context) {
        if (SharedUtil.getBooleanT(context, CacheKey.SKEY_PUSH)) {
            startPush(context);
        } else {
            stopPush(context);
        }
    }

    /**
     * 启动推送
     */
    public static void startPush(Context context) {
        PushManager.getInstance().initialize(context.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(context.getApplicationContext(), IntentService.class);
    }

    /**
     * 关闭推送
     */
    public static void stopPush(Context context) {
        PushManager.getInstance().turnOffPush(context);
        PushManager.getInstance().stopService(context);
    }

    /**
     * clientid 「CID」
     */
    public static String getClientId(Context context) {
        return SharedUtil.getString(context, CacheKey.GT_CID);
    }
}
