package com.fiveoneofly.cgw.bridge;

import android.app.Fragment;
import android.widget.Toast;

import com.fiveoneofly.cgw.constants.ThirdParam;
import com.fiveoneofly.cgw.third.moxie.MoxieFragment;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.manager.StatusViewHandler;
import com.moxie.client.model.MxParam;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MoxiePlugin extends Plugin {

    @Override
    public void handle(final String id, JSONObject data, final ICallback responseCallback) {

        String taskType = getString(data, "taskType");//魔蝎code码，用于进入不同魔蝎认证
        String certId = getString(data, "certId");//申请件编号
        String mLoginCustom = getString(data, "loginCustom");//自定义登录，由js控制，假如有值，赋值到魔蝎所需参数loginCustom中去

        String mAgreementUrl = "https://api.51datakey.com/h5/agreement.html"; //SDK里显示的用户使用协议
        String mAgreementEntryText = "同意《用户使用协议》";//SDK里显示的同意协议描述语

        MxParam mxParam = new MxParam();
        mxParam.setUserId(certId);// 必传// 合作方系统中的客户ID
        mxParam.setApiKey(ThirdParam.MOXIE);// 必传
        mxParam.setFunction(taskType);// 必传
        mxParam.setAgreementUrl(mAgreementUrl);// 自定义用户使用协议页面
        mxParam.setAgreementEntryText(mAgreementEntryText);// SDK里显示的同意协议描述语
        mxParam.setCacheDisable(MxParam.PARAM_COMMON_YES);// 是否使用缓存。默认账号会记住，密码不会记住。如果设置YES，则不会有任何缓存
        mxParam.setLoadingViewText("验证过程中不会浪费您任何流量\n请稍等片刻");// 设置导入过程中的自定义提示文案，为居中显示
        mxParam.setQuitDisable(true);// 设置导入过程中，触发返回键或者点击actionbar的返回按钮的时候，不执行魔蝎的默认行为。这个在拦截onBackPressed事件的时候必须要设置
        mxParam.setQuitLoginDone(MxParam.PARAM_COMMON_NO);// 登录成功后直接退出SDK

        if (!taskType.equals("")) {
            if (taskType.equals(MxParam.PARAM_FUNCTION_ONLINEBANK)) {
                String loginCode = null;
                String loginType = null;
                if (mLoginCustom != null) {
                    try {
                        JSONObject loginCustom = new JSONObject(mLoginCustom);
                        loginCode = loginCustom.getString("loginCode");// 银行编码
                        loginType = loginCustom.getString("loginType");// 卡类型
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (loginCode != null && loginType != null) {
                    HashMap<String, String> loginCustomBank = new HashMap<>();
                    loginCustomBank.put(MxParam.PARAM_CUSTOM_LOGIN_TYPE, loginType);// MxParam.PARAM_ITEM_TYPE_CREDITCAR:信用卡 MxParam.PARAM_ITEM_TYPE_DEBITCARD:借记卡
                    loginCustomBank.put(MxParam.PARAM_CUSTOM_LOGIN_CODE, loginCode); // ABC:代表农业银行
                    mxParam.setLoginCustom(loginCustomBank);
                }
            }
        }

        // 自定义导入页面
        MoxieSDK.getInstance().setStatusViewHandler(new StatusViewHandler() {
            @Override
            public Fragment statusViewForMoxieSDK() {
                return new MoxieFragment();
            }
        });

        QbSdk.forceSysWebView();
        MoxieSDK.getInstance().start(mOnBridgeListener.getActivity(), mxParam, new MoxieCallBack() {
            @Override
            public boolean callback(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
                /*
                 *  MoxieCallBackData的格式如下：
                 *  1.1.没有进行账单导入(后台没有通知)
                 *      "code" : MxParam.ResultCode.IMPORT_UNSTART,"taskType" : "mail","searchId" : "","taskId" : "","message" : "","account" : ""
                 *  1.2.平台方服务问题(后台没有通知)
                 *      "code" : MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR,"taskType" : "mail","searchId" : "","taskId" : "","message" : "","account" : "xxx"
                 *  1.3.魔蝎数据服务异常(后台没有通知)
                 *      "code" : MxParam.ResultCode.MOXIE_SERVER_ERROR,"taskType" : "mail","searchId" : "","taskId" : "","message" : "","account" : "xxx"
                 *  1.4.用户输入出错（密码、验证码等输错且未继续输入）
                 *      "code" : MxParam.ResultCode.USER_INPUT_ERROR,"taskType" : "mail","searchId" : "","taskId" : "","message" : "密码错误","account" : "xxx"
                 *  2.账单导入失败(后台有通知)
                 *      "code" : MxParam.ResultCode.IMPORT_FAIL,"taskType" : "mail","searchId" : "3550622685459407187","taskId" : "ce6b3806-57a2-4466-90bd-670389b1a112","account" : "xxx"
                 *  3.账单导入成功(后台有通知)
                 *      "code" : MxParam.ResultCode.IMPORT_SUCCESS,"taskType" : "mail","searchId" : "3550622685459407187","taskId" : "ce6b3806-57a2-4466-90bd-670389b1a112","account" : "xxx"
                 *  4.账单导入中(后台有通知)
                 *      "code" : MxParam.ResultCode.IMPORTING,"taskType" : "mail","searchId" : "3550622685459407187","taskId" : "ce6b3806-57a2-4466-90bd-670389b1a112","account" : "xxx"
                 */
                if (moxieCallBackData != null) {
                    JSONObject mxData = new JSONObject();
                    try {
                        mxData.put("code", moxieCallBackData.getCode() + "");
                        mxData.put("taskType", moxieCallBackData.getTaskType());
                        mxData.put("taskId", moxieCallBackData.getTaskId());
                        mxData.put("message", moxieCallBackData.getMessage());
                        mxData.put("account", moxieCallBackData.getAccount());
                        mxData.put("loginDone", moxieCallBackData.isLoginDone());
                        //						mxData.put("businessUserId", moxieCallBackData.getBusinessUserId());
                    } catch (JSONException e) {
                        moxieContext.finish();
                        e.printStackTrace();
                    }

                    responseCallback.callback(id, mPluginCode, mxData.toString());
                } else {
                    Toast.makeText(mOnBridgeListener.getActivity(), "null", Toast.LENGTH_SHORT).show();
                }
                moxieContext.finish();
                return false;
            }
        });

    }
}
