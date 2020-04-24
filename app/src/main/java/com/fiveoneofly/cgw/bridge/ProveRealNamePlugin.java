package com.fiveoneofly.cgw.bridge;

import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
//import com.yxjr.credit.fragment.VerifiedFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ProveRealNamePlugin extends Plugin {
    @Override
    public void handle(String id, JSONObject data, ICallback responseCallback) throws JSONException {
//        String certId = JsonUtil.getString(data, "certId");
//        String categoryCode = JsonUtil.getString(data, "categoryCode");
//
//        final Bundle bundle = new Bundle();
//        bundle.putString("certId", certId);
//        bundle.putString("categoryCode", categoryCode);
//        VerifiedFragment fragment = new VerifiedFragment();
//        fragment.setArguments(bundle);
//        iBridgeService.addFragment(fragment);
    }

}
