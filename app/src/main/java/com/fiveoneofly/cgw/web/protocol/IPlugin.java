package com.fiveoneofly.cgw.web.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public interface IPlugin {

    void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException;
}
