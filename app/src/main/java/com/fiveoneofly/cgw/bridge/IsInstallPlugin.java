package com.fiveoneofly.cgw.bridge;

import android.content.pm.PackageManager;


import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.json.JSONException;
import org.json.JSONObject;

public class IsInstallPlugin extends Plugin {

    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException {

        String CAN_JUMP = "1";// 1表示能跳转
        String CAN_NOT_JUMP = "0";// 0表示未安装不能跳转
        String pkgName = getString(data, "path");
        String isIntall = CAN_NOT_JUMP;

        if (pkgName != null) {
            try {
                PackageManager packageManager = mContext.getPackageManager();
                packageManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
                isIntall = CAN_JUMP;
            } catch (Exception e) {
                isIntall = CAN_NOT_JUMP;
                e.printStackTrace();
            }

        }

        responseCallback.callback(id, mPluginCode, new JSONObject().put("canJump", isIntall).toString());
    }
}
