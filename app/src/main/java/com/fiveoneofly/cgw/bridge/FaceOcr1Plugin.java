package com.fiveoneofly.cgw.bridge;


import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.fiveoneofly.cgw.app.activity.MegviiOcr1Activity;
import com.fiveoneofly.cgw.third.megvii.AuthUtil;
import com.fiveoneofly.cgw.third.megvii.OcrServer;
import com.fiveoneofly.cgw.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class FaceOcr1Plugin extends Plugin {
    @Override
    public void handle(String id, final JSONObject data, ICallback responseCallback) throws JSONException {

        final JSONObject queryData = data.getJSONObject("queryData");

        AuthUtil.netOcrAuth(mContext, new AuthUtil.FaceAuthListener() {
            @Override
            public void authSucces() {

                String scanType = JsonUtil.getString(data, "scanType");
                String appNo = JsonUtil.getString(queryData, "appNo");
                String categoryCode = JsonUtil.getString(queryData, "categoryCode");
                MegviiOcr1Activity.startMegviiOcr(
                        mContext,
                        scanType,
                        OcrServer.MODE_CREDIT,
                        appNo,
                        categoryCode
                );

            }
        });


    }
}
