package com.fiveoneofly.cgw.bridge;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ServerPlugin extends Plugin {

    @Override
    public void handle(final String id, JSONObject data, final ICallback responseCallback) throws JSONException {

        String serviceId = data.getString("serviceId");
        String requestData = data.getString("data");

        Map requestMap = null;

        try {
            requestMap = new ObjectMapper().readValue(requestData, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (requestMap == null) {
            Toast.makeText(mOnBridgeListener.getActivity(), "转换异常", Toast.LENGTH_SHORT).show();
        } else if (serviceId == null) {
            Toast.makeText(mOnBridgeListener.getActivity(), "必要参数为null", Toast.LENGTH_SHORT).show();
        } else {

            new ApiRealCall(mOnBridgeListener.getActivity(), serviceId.replaceAll("\"", ""))
                    .requestOld(requestMap, new ApiCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            responseCallback.callback(id, mPluginCode, s);
                        }

                        @Override
                        public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                            Toast.makeText(mOnBridgeListener.getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}