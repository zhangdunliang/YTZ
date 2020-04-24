package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.utils.SharedUtil;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.yxjr.credit.constants.SharedKey;
import com.fiveoneofly.cgw.utils.JsonUtil;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class GetProductIdPlugin extends Plugin {

    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) {

        String productId = JsonUtil.getString(data, "productId");// 产品ID
        String productIdChild = JsonUtil.getString(data, "productIdChild");// 产品子ID

        if (null != productId && null != productIdChild) {
            SharedUtil.save(mContext, SharedKey.PRODUCT_ID, productId);
            SharedUtil.save(mContext, SharedKey.PRODUCT_ID_CHILD, productIdChild);
        }

        JSONObject saveState = new JSONObject();
        String saveProductId = SharedUtil.getString(mContext, SharedKey.PRODUCT_ID);
        String saveProductIdChild = SharedUtil.getString(mContext, SharedKey.PRODUCT_ID_CHILD);
        try {
            if (StringUtil.isNotEmpty(saveProductId) && StringUtil.isNotEmpty(saveProductIdChild)) {
                saveState.put("state", "1");// 已保存
            } else {
                saveState.put("state", "");// 保存失败
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        responseCallback.callback(id, mPluginCode, saveState.toString());
    }

}
