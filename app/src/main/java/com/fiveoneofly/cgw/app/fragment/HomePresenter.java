package com.fiveoneofly.cgw.app.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.app.activity.NoticeListActivity;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.constants.NoticeType;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.Notice;
import com.fiveoneofly.cgw.net.entity.bean.NoticeListRequest;
import com.fiveoneofly.cgw.net.entity.bean.NoticeListResponse;
import com.fiveoneofly.cgw.utils.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangdunliang on 2018/7/17.
 */

public class HomePresenter {

    private HomeView mHomeView;
    private Context context;

    HomePresenter(Context context, HomeView llistener) {
        this.mHomeView = llistener;
        this.context = context;
    }

    public void requestMessage() {
        NoticeListRequest request = new NoticeListRequest();
        request.setCustId(UserManage.get(context).custId());
        request.setSmsType(NoticeType.NOTICE);
        request.setPageNo("1");
        request.setPageSize("3");
        new ApiRealCall(context, ServiceCode.NOTICE_LIST).request(request, NoticeListResponse.class, new ApiCallback<NoticeListResponse>() {
            @Override
            public void onSuccess(NoticeListResponse response) {
                List<Notice> notices = response.getMap().getSmsList();
                List<String> noticeStr = new ArrayList<>();
                if (notices.size() > 0) {
                    for (int i = 0; i < notices.size(); i++) {
                        noticeStr.add(notices.get(i).getSmsContent());
                    }
                } else {
                    noticeStr.add("");
                }
                mHomeView.onMessage(noticeStr);
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void requestBusinessStatus() {
        Map<String, String> request = new HashMap<>();
        request.put("mobileNo", UserManage.get(context).phoneNo());

        new ApiRealCall(context, ServiceCode.CUST_BUSINESS_STATUS).request(request, String.class, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
//        {
//            "responseMsg":null,
//            "map":{
//                "noCardFirst":"Y",是否必须无卡
//                "noCardResult":"N",无卡开通结果
//                "creditResult":"N",是否开通信贷
//                "custId":"51a97a607a6f4be39e555596ca949e53",
//                "skqResult":"N",刷卡器购买状态
//                "identityResult":"Y"实名认证完成情况
//            }
//        }
                boolean isOpenNoCard = false;
                boolean isOpenCredit = false;
                try {
                    JsonNode jsonNode = new ObjectMapper().readTree(response);
                    JsonNode map = jsonNode.path("map");
                    String noCardResult = map.path("noCardResult").asText();
                    String creditResult = map.path("creditResult").asText();
                    isOpenNoCard = StringUtil.isNotEmpty(noCardResult) && noCardResult.equals("Y");
                    isOpenCredit = StringUtil.isNotEmpty(creditResult) && creditResult.equals("Y");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHomeView.custBusinessStatus(isOpenNoCard, isOpenCredit);
                mHomeView.finishRefresh();
            }


            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                mHomeView.finishRefresh();
            }
        });

    }

}
