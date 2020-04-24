package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.fiveoneofly.cgw.utils.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class GetPermissionPlugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException {
        JSONObject perInfo = new JSONObject();
        perInfo.put("authGPS", PermissionUtil.hasGpsServer(mContext));
        perInfo.put("authGPSApp", PermissionUtil.hasLocation(mContext));
        perInfo.put("authContacts", PermissionUtil.hasContacts(mContext));
        perInfo.put("authSMS", PermissionUtil.hasSMS(mContext));
        perInfo.put("authCall", PermissionUtil.hasCalllog(mContext));
        perInfo.put("authAppInfo", PermissionUtil.hasApplist(mContext));
        perInfo.put("authPhoneInfo", PermissionUtil.hasPhoneState(mContext));

        responseCallback.callback(id, mPluginCode, perInfo.toString());
    }


}
