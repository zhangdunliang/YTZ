package com.fiveoneofly.cgw.third.megvii;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.upload.FileCallBack;
import com.fiveoneofly.cgw.net.upload.FileUpload;
import com.fiveoneofly.cgw.statistics.BuriedStatistics;
import com.fiveoneofly.cgw.third.event.InvokeJsEvent;
import com.fiveoneofly.cgw.utils.LoadingUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.fiveoneofly.cgw.utils.StringUtil;
import com.fiveoneofly.cgw.utils.ToastUtil;
import com.yxjr.credit.constants.BridgeCode;
import com.yxjr.credit.constants.HttpService;
import com.yxjr.credit.constants.SharedKey;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaochangyou on 2018/5/17.
 */
public class FaceServer {

    private BasicMegviiFaceActivity mActivity;
    private String mId = "";
    private String mAppNo = "";
    private String mCategoryCode = "";

    public FaceServer(BasicMegviiFaceActivity activity) {
        this.mActivity = activity;
    }

    public void initializeParam(BasicMegviiFaceActivity activity) {

        Intent intent = activity.getIntent();
        mId = intent.getStringExtra("id");
        mAppNo = intent.getStringExtra("appNo");
        mAppNo = mAppNo.equals("empty") ? "" : mAppNo;
        mCategoryCode = intent.getStringExtra("categoryCode");
        mCategoryCode = mCategoryCode.equals("empty") ? "" : mCategoryCode;
    }

    public void upload(final File[] files, final String delta) {

        LoadingUtil.display(mActivity);
        new FileUpload.Builder()
                .appNo(mAppNo)
                .files(files)
                .upload(new FileCallBack() {
                    @Override
                    public void onSucces(String result) {
                        super.onSucces(result);

                        sendError("人脸识别图片上传成功");
                        String[] filePathAfterSplit;
                        filePathAfterSplit = result.split(","); // 以“,”作为分隔符来分割date字符串，并把结果放入3个字符串中。
                        final JSONObject json = new JSONObject();
                        try {
                            json.put("mobileNo", UserManage.get(mActivity).phoneNo());// 手机号
                            json.put("name", UserManage.get(mActivity).userName());// 姓名
                            json.put("cert", UserManage.get(mActivity).userCert());// 身份证
                            json.put("userheadPortrait", filePathAfterSplit[1].contains("P3") ? filePathAfterSplit[1] : filePathAfterSplit[0]);
                            json.put("picType", "P3");
                            json.put("imageEnv", filePathAfterSplit[0].contains("P4") ? filePathAfterSplit[0] : filePathAfterSplit[1]);
                            json.put("delta", delta);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new BuriedStatistics.Builder().addContext(mActivity)
                                .addOperElementName("ocr")
                                .addOperElementType("button")
                                .addOperPageName("人脸识别页面")
                                .addErrorInfo("活体图像上传成功")
                                .addSessionId("")
                                .build();


                        send(json);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        super.onFailure(errorCode, errorMsg);
                        sendError("人脸识别图片上传失败" + errorMsg);
                        ToastUtil.showToast(mActivity, "上传失败！");
                        new BuriedStatistics.Builder().addContext(mActivity)
                                .addOperElementName("ocr")
                                .addOperElementType("button")
                                .addOperPageName("人脸识别页面")
                                .addErrorInfo("活体图像上传失败")
                                .addSessionId("")
                                .build();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LoadingUtil.dismiss();
                    }
                });
    }

    @SuppressLint("WrongConstant")
    private void send(final JSONObject json) {

        final String serviceId = StringUtil.isEmpty(mCategoryCode) ? HttpService.FACE_INFO : HttpService.PATCH_FACE_INFO;
        Map requestMap;
        try {
            requestMap = new ObjectMapper().readValue(json.toString(), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            requestMap = new HashMap();
        }

        new ApiRealCall(mActivity, serviceId).requestOld(requestMap, new ApiCallback<String>() {
            @Override
            public void onSuccess(String s) {

                EventBus.getDefault().post(new InvokeJsEvent(mId, BridgeCode.LIVENESS, null));

                ToastUtil.showToast(mActivity, "提交成功");
                sendError("人脸识别验证成功");
                mActivity.finish();
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
                sendError("人脸识别验证失败，");
            }
        });

    }

    private void sendError(String error) {
        new BuriedStatistics.Builder().addContext(mActivity).addErrorInfo(error).addOperPageName("Face++人脸识别").addOperElementType("ocr").addOperElementName("活体扫描").addSessionId("").build();
    }
}
