package com.fiveoneofly.cgw.net;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ProgressBar;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.MainApplication;
import com.fiveoneofly.cgw.app.activity.LoginActivity;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.calm.CalmController;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.api.ApiCode;
import com.fiveoneofly.cgw.net.api.ApiHandler;
import com.fiveoneofly.cgw.net.api.ApiInterface;
import com.fiveoneofly.cgw.net.api.ApiObserver;
import com.fiveoneofly.cgw.net.api.ApiRetrofit;
import com.fiveoneofly.cgw.net.api.NetException;
import com.fiveoneofly.cgw.net.entity.req.ReqHeader;
import com.fiveoneofly.cgw.net.entity.req.Request;
import com.fiveoneofly.cgw.utils.LogUtil;
import com.fiveoneofly.cgw.utils.RandomUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.fiveoneofly.cgw.utils.StringUtil;
import com.yxjr.credit.constants.SharedKey;


import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiaochangyou on 2017/6/14.
 */
public class ApiRealCall extends ApiHandler {


    @NonNull
    @ServiceCode.Code
    private String serviceCode;
    private Context context;

    private Dialog dialog;

    public ApiRealCall(@NonNull Context context, @NonNull @ServiceCode.Code String code) {
        this.context = context.getApplicationContext();
        this.serviceCode = code;

        dialog = new Dialog(context, R.style.loadingProgress);
        dialog.setCanceledOnTouchOutside(false);// 触摸外部无法取消
        dialog.setTitle("");
        ProgressBar progressBar = new ProgressBar(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.RED));
            progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
        }
        dialog.setContentView(progressBar);
        Window window = dialog.getWindow();
        if (window != null)
            window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);//按返回键响应是否取消等待框的显示
    }

    /**
     * 生成token
     *
     * @param callback 回调
     */
    public void request(ApiCallback<String> callback) {
        try {
            execute(TYPE_TOKEN, RandomUtil.generateLetterChar(16), String.class, callback);
        } catch (NetException e) {
            e.printStackTrace();
            callFailure(callback, e);
        }
    }

    /**
     * 老接口请求
     *
     * @param request  请求报文
     * @param callback 回调
     */
    public void requestOld(@NonNull final Map request,
                           ApiCallback<String> callback) {
        request(putOldParam(context, request), callback);
    }

    /**
     * js 使用
     *
     * @param request  请求报文
     * @param callback 回调
     */
    public <Q> void request(@NonNull final Q request,
                            final ApiCallback<String> callback) {

        try {

            if (StringUtil.isEmpty(MainApplication.token)) {
                request(new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            execute(TYPE_JS, request, String.class, callback);
                        } catch (NetException e) {
                            e.printStackTrace();
                            callFailure(callback, e);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                        callFailure(callback, errorCode, errorMessage);
                    }
                });
            } else {
                execute(TYPE_JS, request, String.class, callback);
            }

        } catch (NetException e) {
            e.printStackTrace();
            callFailure(callback, e);
        }
    }

    /**
     * native 使用
     *
     * @param request  请求报文
     * @param clazz    返回cls
     * @param callback 回调
     * @param <Q>      请求
     * @param <P>      返回
     */
    public <Q, P> void request(@NonNull final Q request,
                               @NonNull final Class<P> clazz,
                               final ApiCallback<P> callback) {
        try {

            if (StringUtil.isEmpty(MainApplication.token)) {
                request(new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            execute(TYPE_NATIVE, request, clazz, callback);
                        } catch (NetException e) {
                            e.printStackTrace();

                            callFailure(callback, e);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {

                        callFailure(callback, errorCode, errorMessage);
                    }
                });
            } else {
                execute(TYPE_NATIVE, request, clazz, callback);
            }
        } catch (NetException e) {
            e.printStackTrace();

            callFailure(callback, e);
        }
    }

    /**
     * @param type     请求类型
     * @param request  请求报文
     * @param clazz    返回cls
     * @param callback 回调
     * @param <Q>      请求
     * @param <P>      返回
     */
    private <Q, P> void execute(final int type,
                                @NonNull final Q request,// reqBody
                                @NonNull final Class<P> clazz,
                                final ApiCallback<P> callback) throws NetException {

        String requestMessage;
        ReqHeader reqHeader = handleHeader(context);

        /*
         * 主要区别，token接口不要serviceCode
         */
        if (type == TYPE_TOKEN) {
            tempToken = (String) request;
            Request req = handleRequest(type, null, reqHeader, request);
            requestMessage = obj2String(req);
        } else if (type == TYPE_JS || type == TYPE_NATIVE) {
            /*
             * 切勿放在3016使用的地方
             * 会死循环
             */
            if (SharedUtil.getString(context, SharedKey.IDS).contains(serviceCode)) {
                CalmController.getInstance().executeSmallData(context);
            }
            tempToken = MainApplication.token;
            Request req = handleRequest(type, serviceCode, reqHeader, request);
            requestMessage = obj2String(req);
        } else {
            throw new NetException(ApiCode.E6001);
        }
//        LogUtil.json(serviceCode + "原始请求报文" + requestMessage);

        ApiInterface apiInterface = ApiRetrofit.getInstance().getRetrofit().create(ApiInterface.class);
        apiInterface.call(requestMessage)
                .subscribeOn(Schedulers.io())//发送事件在IO线程：被观察者：上游
                .observeOn(AndroidSchedulers.mainThread())//接收事件在主线程：观察者：下游
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onStart() {
                        showDialog();
                        if (callback != null) {
                            callback.onStart();
                        }
                    }

                    @Override
                    public void onException(Throwable e) {
                        dismissDialog();
                        LogUtil.e(e);
                        callFailure(callback, handleException(e));
                    }

                    @Override
                    public void onSuccess(String responseMessage) {
                        try {
                            LogUtil.json(serviceCode + "原始返回报文", responseMessage);
                            String response = handleResponse(serviceCode, type, responseMessage);
                            LogUtil.json(serviceCode + "返回报文处理成功", response);

                            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                            if (callback != null) {
                                P p;
                                if (clazz == String.class) {
                                    p = (P) response;
                                } else {
                                    p = objectMapper.readValue(response, clazz);
                                }

                                cancelDialog();
                                callback.onSuccess(p);
                            }

                        } catch (NetException e) {
                            cancelDialog();
                            LogUtil.e(e);
                            callFailure(callback, e);
                            handleErrorCode(e);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            cancelDialog();
                            callFailure(callback, handleException(e));
                        }
                    }
                });
    }

    private void handleErrorCode(NetException e) {

        if (e.getCode().equals(ApiCode.R004.getCode())
                || e.getCode().equals(ApiCode.R005.getCode())) {
            UserManage.get(context).loginOut();
            context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void callFailure(ApiCallback callback, NetException e) {
        if (callback != null) {
            callback.onFailure(e.getCode(), e.getMessage());
        }
    }

    private void callFailure(ApiCallback callback, ApiCode apiCode) {
        if (callback != null) {
            callback.onFailure(apiCode.getCode(), apiCode.getMessage());
        }
    }

    private void callFailure(ApiCallback callback, String code, String message) {
        if (callback != null) {
            callback.onFailure(code, message);
        }
    }

    private void showDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

}
