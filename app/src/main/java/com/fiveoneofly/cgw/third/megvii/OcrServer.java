package com.fiveoneofly.cgw.third.megvii;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.activity.MegviiOcr1Activity;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.third.event.InvokeJsEvent;
import com.fiveoneofly.cgw.utils.DateUtil;
import com.fiveoneofly.cgw.utils.LoadingUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;
import com.yxjr.credit.constants.BridgeCode;
import com.yxjr.credit.constants.HttpService;
import com.yxjr.credit.constants.SharedKey;
import com.fiveoneofly.cgw.net.upload.FileCallBack;
import com.fiveoneofly.cgw.net.upload.FileUpload;
import com.fiveoneofly.cgw.third.megvii.util.Util;
import com.fiveoneofly.cgw.utils.StringUtil;
import com.fiveoneofly.cgw.statistics.BuriedStatistics;
import com.fiveoneofly.cgw.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by xiaochangyou on 2018/5/16.
 */
public class OcrServer {

    private int mScanType = 1;//扫描类型
    private String mMode;

    private String mAppNo;//申请件
    private String mCategoryCode;//补件码

    private String mCustId;//客户编号
    private String mQueryData;//

    public static final String MODE_CREDIT = "credit";//信贷流程
    public static final String MODE_ALONG = "along";//单独模块；目前使用者：无卡取现

    public static final String ONLY_FRONT = "1";//1单独正面扫描
    public static final String ONLY_BACK = "2";//2单独反面扫描
    public static final String DEFALUT_NORMAL = "0";//0正面扫描上传成功紧接反面扫描

    public static final String paramScanType = "scanType";
    public static final String paramMode = "mode";
    public static final String paramAppNo = "appNo";
    public static final String paramCategoryCode = "categoryCode";
    public static final String paramCustId = "custId";
    public static final String paramQueryData = "queryData";

    private String jsCode;//回调JS接口号
    private String errorStrHead;//埋点
    private String sessionId;//埋点[此处仅用于文件上传结果]：信贷→appNo；无卡→custId；

    private String mServiceId;

    private BasicMegviiOcrActivity mActivity;

    private final String P1 = "P1";//身份证正面图片标识
    private final String P2 = "P2";//身份证反面图片标识
    private final String P5 = "P5";//身份证正面人像图片标识

    public OcrServer(BasicMegviiOcrActivity activity) {
        this.mActivity = activity;
    }

    private void restartActivity(Context context) {
        Intent intent = new Intent(context, MegviiOcr1Activity.class);
        intent.putExtra(paramScanType, mScanType);
        intent.putExtra(paramMode, mMode);
        if (mAppNo != null) {
            intent.putExtra(paramAppNo, mAppNo);
            intent.putExtra(paramCategoryCode, mCategoryCode);
        } else if (mCustId != null) {
            intent.putExtra(paramCustId, mCustId);
            intent.putExtra(paramQueryData, mQueryData);
        }
        context.startActivity(intent);
        mActivity.finish();
    }

    public IDCardAttr.IDCardSide initializeParam(MegviiOcr1Activity activity) {
        IDCardAttr.IDCardSide side;
        Intent intent = activity.getIntent();
        /*
         * 1、scanType = 0
         * 扫描正面 → 扫描反面 → 结束
         * side=FRONT → side=BACK → 结束
         *
         * 2、scanType = 1
         * 扫描正面 → 结束
         * side=FRONT → 结束
         *
         * 3、scanType = 2
         * 扫描反面 → 结束
         * side=BACK → 结束
         */
        mScanType = intent.getIntExtra(paramScanType, Integer.parseInt(DEFALUT_NORMAL));
        if (mScanType == Integer.parseInt(ONLY_FRONT)) {
            side = IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT;
        } else if (mScanType == Integer.parseInt(ONLY_BACK)) {
            side = IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
        } else {
            side = IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT;
        }

        mMode = intent.getStringExtra(paramMode);
        if (null != mMode) {
            switch (mMode) {
                case MODE_CREDIT:
                    mAppNo = intent.getStringExtra(paramAppNo);
                    mCategoryCode = intent.getStringExtra(paramCategoryCode);
                    mAppNo = StringUtil.isEmpty(mAppNo) ? "" : mAppNo;
                    mCategoryCode = StringUtil.isEmpty(mCategoryCode) ? "" : mCategoryCode;
                    break;
                case MODE_ALONG:
                    mCustId = intent.getStringExtra(paramCustId);
                    mQueryData = intent.getStringExtra(paramQueryData);
                    mCustId = StringUtil.isEmpty(mCustId) ? "" : mCustId;
                    mQueryData = StringUtil.isEmpty(mQueryData) ? "" : mQueryData;
                    break;
                default:
                    ToastUtil.showToast(activity, "参数错误");
                    activity.finish();
                    break;
            }
        } else {
            ToastUtil.showToast(activity, "参数错误");
            activity.finish();
        }
        return side;
    }

    public void handleResult(int side, IDCardQualityResult result) {

        mServiceId = StringUtil.isEmpty(mCategoryCode) ? HttpService.IDENTITY_INFO : HttpService.PATCH_IDENTITY_INFO;// 补件码不为空→补件

        if (side == 0) {
            File[] files = new File[2];
            files[0] = Util.bmp2File(result.croppedImageOfIDCard(), P1 + "_" + DateUtil.getDate() + ".jpg");//扣出图像中身份证的部分。
            files[1] = Util.bmp2File(result.croppedImageOfPortrait(), P5 + "_" + DateUtil.getDate() + ".jpg");//扣出身份证中人脸的部分。
            upload(true, files);
        } else {
            File[] files = new File[1];
            files[0] = Util.bmp2File(result.croppedImageOfIDCard(), P2 + "_" + DateUtil.getDate() + ".jpg");//扣出图像中身份证的部分。
            upload(false, files);
        }
    }

    private void upload(final boolean isFront, final File[] files) {

        FileUpload.Builder builder = null;

        if (mMode.equals(MODE_CREDIT)) {
            builder = new FileUpload.Builder().appNo(mAppNo);
            errorStrHead = "无卡身份证";
            sessionId = mAppNo;
        } else if (mMode.equals(MODE_ALONG)) {
            builder = new FileUpload.Builder().custId(mCustId);
            errorStrHead = "身份证";
            sessionId = mCustId;
        }

        if (builder != null) {
            LoadingUtil.display(mActivity);
            builder.files(files).upload(new FileCallBack() {
                @Override
                public void onSucces(String result) {
                    super.onSucces(result);
                    if (isFront) {
                        String[] filePathAfterSplit = new String[2];
                        StringTokenizer token = new StringTokenizer(result, ",");
                        while (token.hasMoreTokens()) {//校验字符串正确
                            String path = token.nextToken();
                            if (path.contains(P1)) {
                                filePathAfterSplit[0] = path;
                            } else if (path.contains(P5)) {
                                filePathAfterSplit[1] = path;
                            }
                        }
                        showError(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT, errorStrHead + "正面照片上传成功", sessionId);
                        try {
                            send(P1, filePathAfterSplit[0], filePathAfterSplit[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showError(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK, errorStrHead + "反面照片上传成功", sessionId);
                        try {
                            send(P2, result, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    super.onFailure(errorCode, errorMsg);
                    if (isFront) {
                        showError(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT, errorStrHead + "正面照片上传失败" + errorMsg, mAppNo);
                        ToastUtil.showToast(mActivity, R.string.idcard_front_up_fail);
                    } else {
                        showError(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK, errorStrHead + "反面照片上传失败" + errorMsg, mAppNo);
                        ToastUtil.showToast(mActivity, R.string.idcard_back_up_fail);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (!isFront) {
                        for (File file : files) {
                            if (null != file) {
                                Util.deleteTempFile(file.getAbsolutePath());
                            }
                        }
                    }
                    LoadingUtil.dismiss();
                }
            });
        }
    }

    private void send(final String picType,
                      String certFilePath,
                      String certPortraitFilePath) throws JSONException {

        final IDCardAttr.IDCardSide side = picType.equals(P1) ? IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT : IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;

        JSONObject jsonObj = null;

        if (mMode.equals(MODE_CREDIT)) {
            jsonObj = new JSONObject();
            errorStrHead = "";
            jsCode = BridgeCode.SCAN_IDCARD;
        } else if (mMode.equals(MODE_ALONG)) {
            jsonObj = new JSONObject(mQueryData);
            errorStrHead = "无卡";
            jsCode = BridgeCode.J259;
        }

        if (jsonObj != null) {
            jsonObj.put("mobileNo", SharedUtil.getString(mActivity, SharedKey.PARTNER_PHONENUMBER));// 手机号
            jsonObj.put("name", SharedUtil.getString(mActivity, SharedKey.PARTNER_REAL_NAME));// 姓名
            jsonObj.put("cert", SharedUtil.getString(mActivity, SharedKey.PARTNER_ID_CARD_NUM));// 身份证
            jsonObj.put("certFront", picType.equals(P1) ? certFilePath : "");// 身份证正面
            jsonObj.put("certBack", picType.equals(P2) ? certFilePath : "");// 身份证反面
            jsonObj.put("certFrontPortrait", picType.equals(P1) ? certPortraitFilePath : "");// 身份证正面人像
            jsonObj.put("picType", picType);


            Map requestMap;
            try {
                requestMap = new ObjectMapper().readValue(jsonObj.toString(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
                requestMap = new HashMap();
            }

            new ApiRealCall(mActivity, mServiceId).requestOld(requestMap, new ApiCallback<String>() {
                @Override
                public void onSuccess(String s) {

                    showError(side, picType + errorStrHead + "验证成功", mServiceId);

                    if (picType.equals(P1)) {
                        try {
                            EventBus.getDefault().post(new InvokeJsEvent(jsCode, jsCode, new JSONObject().put("identityStatus", "front").toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (mScanType == Integer.parseInt(DEFALUT_NORMAL)) {
                            mScanType = Integer.parseInt(ONLY_BACK);
                            restartActivity(mActivity);
                        }
                    } else if (picType.equals(P2)) {
                        try {
                            EventBus.getDefault().post(new InvokeJsEvent(jsCode, jsCode, new JSONObject().put("identityStatus", "reverse").toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showDialog("扫描完成");
                        closeActivity();
                    }

                }

                @Override
                public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
                    showError(side, picType + errorStrHead + "验证失败" + errorMessage, mServiceId);
                    showDialog("验证失败");
                    closeActivity();

                    showDialog("验证失败");
                    closeActivity();
                }
            });

        }
    }

    private Handler mHandler;

    private void closeActivity() {
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                ToastUtil.cancleToast(mActivity);
                if (hintDialog != null && hintDialog.isShowing())
                    hintDialog.dismiss();
                mActivity.finish();
            }

        }, 2000);//延时关闭
    }

    private AlertDialog hintDialog;

    private void showDialog(String message) {
        if (hintDialog == null)
            hintDialog = new AlertDialog.Builder(mActivity).setMessage(message).setCancelable(false).create();
        else
            hintDialog.setMessage(message);
        hintDialog.show();
    }

    private void showError(IDCardAttr.IDCardSide side, String errorStr, String sessionId) {
        new BuriedStatistics.Builder().addContext(mActivity).addErrorInfo(errorStr).addOperPageName(side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? "Face++身份证识别-正面" : "Face++身份证识别-反面").addOperElementType("ocr")
                .addOperElementName("身份证扫描").addSessionId(sessionId).build();
    }
}
