package com.fiveoneofly.cgw.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.constants.ThirdParam;
import com.fiveoneofly.cgw.net.ApiBatch;
import com.fiveoneofly.cgw.utils.LogUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.yxjr.credit.constants.HttpService;
import com.yxjr.credit.constants.SharedKey;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

/**
 * Created by xiaochangyou on 2018/4/18.
 */
public class Service extends android.app.Service {

    private ObjectMapper mObjectMapper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectMapper = new ObjectMapper();

        final int HEART_SLEEP_TIME = formatSecond(SharedUtil.getString(this, SharedKey.HEARTBEAT_SPACING));
        if (HEART_SLEEP_TIME != 0) {
            HEART_IS_EXIT = true;
        }
        if (HEART_IS_EXIT) {
            Thread mHeartThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (HEART_IS_EXIT) {
                        try {
                            send(HttpService.HEART_BEAT);
                            LogUtil.d("normal heart");
                            Thread.sleep(HEART_SLEEP_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mHeartThread.start();
        }

        // 同盾
        final int BLACK_BOX_SLEEP_TIME = formatSecond(SharedUtil.getString(this, SharedKey.FM_HEARTBEAT_SPACING));
        if (BLACK_BOX_SLEEP_TIME != 0)

        {
            BLACK_BOX_IS_EXIT = true;
        }

        Thread mBlackBoxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (BLACK_BOX_IS_EXIT) {
                    try {
                        send(HttpService.BLACK_BOX_HEART_BEAT);
                        Thread.sleep(BLACK_BOX_SLEEP_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (BLACK_BOX_IS_EXIT) {
            String initStatus = FMAgent.getInitStatus();
            if (initStatus.equals("successful")) {
                mBlackBoxThread.start();
            } else {
                if (initStatus.equals("failed")) {
                    try {
                        FMAgent.init(this, ThirdParam.TONGDUN);
                    } catch (FMException e) {
                        e.printStackTrace();
                    }
                }
                mBlackBoxThread.start();
            }
        }

    }

    @Override
    public void onDestroy() {
        HEART_IS_EXIT = false;
        BLACK_BOX_IS_EXIT = false;
        super.onDestroy();
    }

    private boolean HEART_IS_EXIT = false;
    private boolean BLACK_BOX_IS_EXIT = false;

    private int formatSecond(String str) {
        int timeInt = 0;
        String time;// 时间
        String unit = null;// 单位d:天,h：消失,m：分钟,s：秒
        // str:间隔时间，带单位的(服务器返回)
        if (StringUtil.isNotEmpty(str)) {
            time = str.substring(0, str.length() - 1);
            unit = str.substring(str.length() - 1, str.length());
            if (StringUtil.isNotEmpty(time)) {
                timeInt = Integer.parseInt(time);
            }
        }
        int second = 0;// 1000等于1秒
        if (unit != null) {
            switch (unit) {
                case "d":// 天
                    second = timeInt * 24 * 60 * 60 * 1000;
                    break;
                case "h":// 小时
                    second = timeInt * 60 * 60 * 1000;
                    break;
                case "m":// 分钟
                    second = timeInt * 60 * 1000;
                    break;
                case "s":// 秒
                    second = timeInt * 1000;
                    break;
                default:
                    break;
            }
        }
        return second;
    }

    // 发送心跳
    private void send(final String serviceId) {

        if (!UserManage.get(this).isLogin() || !UserManage.get(this).identityStatus()) {
            return;
        }

        String idCardNum = UserManage.get(this).userCert();
        String phoneNumber = UserManage.get(this).phoneNo();
        JSONObject jsonObj = new JSONObject();

        if (serviceId.equals(HttpService.HEART_BEAT)) {
            // 正常心跳发送
            String name = SharedUtil.getString(this, SharedKey.PARTNER_REAL_NAME);

            try {
                jsonObj.put("cert", idCardNum);
                jsonObj.put("name", name);
                jsonObj.put("mobileNo", phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (serviceId.equals(HttpService.BLACK_BOX_HEART_BEAT)) {
            // 同盾心跳数据发送
            String blackBox = FMAgent.onEvent(this);
            try {
                jsonObj.put("cert", idCardNum);
                jsonObj.put("mobileNo", phoneNumber);
                jsonObj.put("blackBox", blackBox);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            return;
        }


        try {
            Map map = mObjectMapper.readValue(jsonObj.toString(), Map.class);
            new ApiBatch().request(this, serviceId, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
