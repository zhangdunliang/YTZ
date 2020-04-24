package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.app.activity.Web2Activity;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.json.JSONObject;

// 退出「关闭」WebView 2 页面
public class ExitWeb2Plugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) {

        responseCallback.callback(id, mPluginCode, data.toString());

        if (mOnBridgeListener.getActivity() instanceof Web2Activity) {
            mOnBridgeListener.getActivity().finish();
        }
    }

}
