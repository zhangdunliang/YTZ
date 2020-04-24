package com.fiveoneofly.cgw.bridge;


import com.fiveoneofly.cgw.third.tongdun.Tongdun;
import com.fiveoneofly.cgw.utils.DialogUtil;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.json.JSONException;
import org.json.JSONObject;

// 获取同盾设备指纹
public class GetTdPlugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException {

        if (Tongdun.getInstance().isSuccess(mContext)) {
            JSONObject obj = new JSONObject();
            obj.put("blackBox", Tongdun.getInstance().getBlackBox(mContext));
            responseCallback.callback(id, mPluginCode, obj.toString());
        } else {
            DialogUtil.displayByConfirm(mContext, "请稍候重试！");
            Tongdun.getInstance().reInitialize(mContext);
        }
    }


}
