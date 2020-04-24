package com.fiveoneofly.cgw.third.tongdun;

import android.content.Context;

import com.fiveoneofly.cgw.constants.ThirdParam;

import java.util.HashMap;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

/**
 * Created by xiaochangyou on 2018/6/1.
 */

public class Tongdun {

    private static Tongdun mInstance = null;

    private Tongdun() {
    }

    public static Tongdun getInstance() {
        if (mInstance == null) {
            synchronized (Tongdun.class) {
                if (mInstance == null) {
                    mInstance = new Tongdun();
                }
            }
        }
        return mInstance;
    }

    // 初始化
    public void initialize(Context context) throws FMException {
        HashMap<String, Object> options = new HashMap<>();
        options.put(FMAgent.OPTION_INIT_TIMESPAN, 0); //两次调用间隔设为0
        FMAgent.init(context.getApplicationContext(), ThirdParam.TONGDUN, options);
    }

    // 重新初始化
    public void reInitialize(Context context) {
        try {
            FMAgent.init(context, ThirdParam.TONGDUN);
        } catch (FMException e) {
            e.printStackTrace();
        }
    }

    // 是否初始化成功
    public boolean isSuccess(Context context) {
        String initStatus = FMAgent.getInitStatus();
        return initStatus.equals("successful");
    }

    // 获取 BlackBox 值
    public String getBlackBox(Context context) {
        return FMAgent.onEvent(context);
    }

}
