package com.fiveoneofly.cgw.bridge;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.fiveoneofly.cgw.third.event.InvokeJsEvent;
import com.fiveoneofly.cgw.web.protocol.BridgeProtocol;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fiveoneofly.cgw.web.protocol.Plugin;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class BridgeService {

    private ObjectMapper objectMapper;
    private BridgeProtocol bridgeProtocol;

    public BridgeService(Context context) {
        try {
            objectMapper = new ObjectMapper();
            bridgeProtocol = new BridgeProtocol(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void appBridgeService(String datas) {

        try {
            JsonNode jsonNode = objectMapper.readTree(datas);
            String code = jsonNode.path("code").asText();
            String id = jsonNode.path("id").asText();

            JsonNode jsonData = jsonNode.get("data");
            ByteArrayOutputStream bops = new ByteArrayOutputStream();
            objectMapper.writeValue(bops, jsonData);
            String data = bops.toString();

            if (code.equals("200")) {
                String serviceId = jsonNode.get("serviceId").toString();
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("serviceId", serviceId);
                objectNode.put("data", data);

                data = objectMapper.writeValueAsString(objectNode);
            }

            bridgeProtocol.execute(code, id, data, new ICallback() {

                        @Override
                        public void callback(String id, String code, String data) {
                            Log.e("plugin", "处理" + code + "结束；--->id：" + id + " --->data：" + data);

                            EventBus.getDefault().post(new InvokeJsEvent(id, code, data));
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerPlugin(String pluginCode, Plugin plugin) {
        bridgeProtocol.registerPlugin(pluginCode, plugin);
    }
}
