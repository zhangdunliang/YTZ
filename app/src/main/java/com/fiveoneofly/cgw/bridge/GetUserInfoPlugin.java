package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUserInfoPlugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException {

        if (UserManage.get(mOnBridgeListener.getActivity()).isLogin()) {

            UserManage userManage = UserManage.get(mOnBridgeListener.getActivity());

            JSONObject userInfo = new JSONObject();
            userInfo.put("partnerId", userManage.partnerId());
            userInfo.put("name", userManage.userName());
            userInfo.put("identityCard", userManage.userCert());
            userInfo.put("phoneNum", userManage.phoneNo());

            userInfo.put("currentSDKVersion", AndroidUtil.getVersionName(mContext));
            userInfo.put("supportSDKVersion", AndroidUtil.getVersionName(mContext));

            responseCallback.callback(id, mPluginCode, userInfo.toString());

        } else {
            responseCallback.callback(id, mPluginCode, new JSONObject().put("error", "未登录").toString());
        }

    }
}
