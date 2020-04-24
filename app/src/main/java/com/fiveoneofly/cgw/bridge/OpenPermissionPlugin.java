package com.fiveoneofly.cgw.bridge;

import android.content.Intent;
import android.net.Uri;


import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.json.JSONObject;

public class OpenPermissionPlugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) {
//        if (Build.VERSION.SDK_INT >= 9) {
//            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            intent.setData(Uri.fromParts("package", mOnBridgeListener.getActivity().getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
//            intent.putExtra("com.android.settings.ApplicationPkgName", mOnBridgeListener.getActivity().getPackageName());
//        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", mOnBridgeListener.getActivity().getPackageName(), null));
        mOnBridgeListener.getActivity().startActivity(intent);

    }
}
