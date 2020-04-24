package com.fiveoneofly.cgw.bridge;


import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.fiveoneofly.cgw.app.activity.MegviiFace1Activity;
import com.fiveoneofly.cgw.third.megvii.AuthUtil;
import com.fiveoneofly.cgw.utils.JsonUtil;

import org.json.JSONObject;

public class FaceLivePlugin extends Plugin {
    @Override
    public void handle(final String id, final JSONObject data, ICallback responseCallback) {

        AuthUtil.netFaceAuth(mContext, new AuthUtil.FaceAuthListener() {
            @Override
            public void authSucces() {
                String liveAppNo = JsonUtil.getString(data, "appNo");
                String liveCategoryCode = JsonUtil.getString(data, "categoryCode");

                MegviiFace1Activity.startMegviiFace(mContext, id, liveAppNo, liveCategoryCode);
            }
        });
    }
}
