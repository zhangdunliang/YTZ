package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.json.JSONObject;

// 退出「关闭」WebView页面
public class ExitWeb1Plugin extends Plugin {

    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) {
        mOnBridgeListener.getActivity().finish();
    }
}
