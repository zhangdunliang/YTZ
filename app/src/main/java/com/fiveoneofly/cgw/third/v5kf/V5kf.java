package com.fiveoneofly.cgw.third.v5kf;

import android.app.ActivityManager;
import android.content.Context;

import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.tencent.android.tpush.XGPushConfig;
import com.v5kf.client.lib.Logger;
import com.v5kf.client.lib.V5ClientAgent;
import com.v5kf.client.lib.V5ClientConfig;
import com.v5kf.client.lib.callback.V5InitCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class V5kf {

    public static void initialize(Context context) {
        if (isMainProcess(context)) { // 判断为主进程，在主进程中初始化，多进程同时初始化可能导致不可预料的后果
            Logger.w("MyApplication", "onCreate isMainProcess V5ClientAgent.init");
            V5ClientConfig.FILE_PROVIDER = AndroidUtil.getPackageName(context) + ".fileprovider"; // 设置fileprovider的authorities
            // 站点编号和appid配置
            V5ClientAgent.init(context, "149809", "2493108011c3a", new V5InitCallback() {

                @Override
                public void onSuccess(String response) {
                    Logger.i("MyApplication", "V5ClientAgent.init(): " + response);
                }

                @Override
                public void onFailure(String response) {
                    Logger.e("MyApplication", "V5ClientAgent.init(): " + response);
                }
            });
        }
    }

    public static void config(Context context) {
        V5ClientConfig config = V5ClientConfig.getInstance(context);
        // V5客服系统客户端配置
        // config.setShowLog(true); // 显示日志，默认为true

        /*** 客户信息设置 ***/
        // 如果更改了用户信息，需要在设置前调用shouldUpdateUserInfo
        config.shouldUpdateUserInfo();
        // 【建议】设置用户昵称
        config.setNickname(UserManage.get(context).phoneNo());
        // 设置用户性别: 0-未知 1-男 2-女
        config.setGender(0);
        // 【建议】设置用户头像URL
//        config.setAvatar("http://debugimg-10013434.image.myqcloud.com/fe1382d100019cfb572b1934af3d2c04/thumbnail");
        /**
         *【建议】设置用户OpenId，以识别不同登录用户，不设置则默认由SDK生成，替代v1.2.0之前的uid,
         *  openId将透传到座席端(长度32字节以内，建议使用含字母数字和下划线的字符串，尽量不用特殊字符，若含特殊字符系统会进行URL encode处理，影响最终长度和座席端获得的结果)
         *	若您是旧版本SDK用户，只是想升级，为兼容旧版，避免客户信息改变可继续使用config.setUid，可不用openId
         */
//        config.setOpenId("android_sdk_test");
        // config.setUid(uid); //【弃用】请使用setOpenId替代
        // 设置用户VIP等级(0-5)
        config.setVip(0);
        // 使用消息推送时需设置device_token:集成第三方推送(腾讯信鸽、百度云推)或自定义推送地址时设置此 参数以在离开会话界面时接收推送消息
        config.setDeviceToken(XGPushConfig.getToken(context.getApplicationContext()));

        // [1.3.0新增]设置V5系统内置的客户基本信息，区别于setUserInfo，这是V5系统内置字段
//        JSONObject baseInfo = new JSONObject();
//        try {
//            baseInfo.put("country", "中国");
//            baseInfo.put("province", "广东");
//            baseInfo.put("city", "深圳");
//            baseInfo.put("language", "zh-cn");
//            // nickname,gender,avatar,vip也可在此设置
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        config.setBaseInfo(baseInfo);

        // 客户信息键值对，下面为示例（JSONObject）
        JSONObject customContent = new JSONObject();
        try {
            customContent.put("来源", AndroidUtil.getAppName(context));
            customContent.put("姓名", UserManage.get(context).userName());
            customContent.put("手机号", UserManage.get(context).phoneNo());
            customContent.put("身份证", UserManage.get(context).userCert());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 设置客户信息（自定义字段名称与值，自定义JSONObjectjian键值对，开启会话前设置，替代之前通过`setUserWillSendMessageListener`在消息中携带信息的方式，此方式更加安全便捷）
        config.setUserInfo(customContent);
    }

    public static void start(Context context) {
        V5ClientAgent.getInstance().startV5ChatActivity(context.getApplicationContext());
    }

    private static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (null != am) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            String mainProcessName = context.getPackageName();
            int myPid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
