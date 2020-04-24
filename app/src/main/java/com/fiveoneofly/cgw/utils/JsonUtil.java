package com.fiveoneofly.cgw.utils;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaochangyou on 2018/4/26.
 * <p>
 * Json 处理
 */
public class JsonUtil {

    /**
     * 解析单个 json 字段
     *
     * @param obj JSONObject
     * @param key 字段名
     * @return 字段值
     */
    public static String getString(JSONObject obj, String key) {
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

}
