package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.app.activity.Web1Activity;
import com.fiveoneofly.cgw.app.activity.Web2Activity;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class WebNewPlugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException {

        if (mOnBridgeListener.getActivity() instanceof Web1Activity) {
            String url = getString(data, "entryUrl");//url链接
            String backHint = getString(data, "backHint");//退出提示语
            String isRefresh = getString(data, "refresh");//是否请求刷新第一层
            String htmlLabel = getString(data, "htmlLabel");//html标签「与url互斥！！！」
            String type = getString(data, "type");//认证类型「目前仅银联使用」
            /*
             * case1: url不为空「仅加载该url页面」
             * case2：url为空 → htmlLabel && type 不为空「仅加载html标签」
             * 说明：
             * 1、backHint不为空:关闭Web时dialog提示（提示语为backHint）后再确认关闭；反之直接关闭
             * 2、isRefresh为true：关闭Web2Activity后 → 刷新Web1Activity内容
             */
            if (StringUtil.isNotEmpty(url)) {
                Web2Activity.startWebActivityForUrl(mOnBridgeListener.getActivity(), url, backHint, isRefresh.equals("true"), type);
            } else if (StringUtil.isNotEmpty(htmlLabel) && StringUtil.isNotEmpty(type)) {
                Web2Activity.startWebActivityForHtml(mOnBridgeListener.getActivity(), url, backHint, isRefresh.equals("true"), type);
            } else {
                responseCallback.callback(id, mPluginCode, new JSONObject().put("error", "必要参数为空").toString());
            }
        } else {
            responseCallback.callback(id, mPluginCode, new JSONObject().put("error", "错误添加").toString());
        }
    }
}
