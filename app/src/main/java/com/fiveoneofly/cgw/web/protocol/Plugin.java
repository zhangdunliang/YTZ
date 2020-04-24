package com.fiveoneofly.cgw.web.protocol;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class Plugin implements IPlugin {

    protected Context mContext;
    protected String mPluginCode;
    protected OnBridgeListener mOnBridgeListener;

    protected String getString(JSONObject obj, String key) {
        String data = null;
        try {
            if (null != obj && null != key) {
                data = obj.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public Plugin() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    void initialize(String pluginCode, Context context, OnBridgeListener onBridgeListener) {
        this.mContext = context;
        this.mPluginCode = pluginCode;
        this.mOnBridgeListener = onBridgeListener;
    }


}