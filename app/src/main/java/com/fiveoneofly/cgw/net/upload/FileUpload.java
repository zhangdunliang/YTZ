package com.fiveoneofly.cgw.net.upload;

import android.content.Context;

import com.fiveoneofly.cgw.constants.HttpParam;
import com.yxjr.http.HttpClient;
import com.yxjr.http.builder.Headers;
import com.yxjr.http.builder.Request;
import com.yxjr.http.builder.RequestParams;

import java.io.File;

/**
 * Created by xiaochangyou on 2018/5/15.
 */

public class FileUpload {
    private FileUpload(Builder builder, FileCallBack callBack) throws Exception {
        String appNo = builder.bAppNo;
        String custId = builder.bCustId;

        if (appNo == null && custId == null) {//必须有一个
            callBack.onFailure("8888", "缺少必要参数");
            return;
        }
        if (appNo != null && custId != null) {//只能有一个
            callBack.onFailure("8888", "过多参数");
            return;
        }

        File[] files = builder.bFiles;
        if (files == null || files.length == 0) {
            callBack.onFailure("9999", "无文件");
            return;
        }


        RequestParams params = new RequestParams();
        String name = "";
        if (null != appNo) {
            name = "appNo";
            params.put(name, appNo);
        } else if (null != custId) {
            name = "custId";
            params.put(name, custId);
        }

        for (int i = 0; i < files.length; i++) {
            if (null == files[i]) {
                callBack.onFailure("9999", "空文件");
                return;
            } else {
                params.putFile("files", files[i]);
            }
        }

        Request request = new Request.Builder()
                .url(HttpParam.UPLOAD)
                .method("POST")
                .params(params)
                .headers(new Headers.Builder()
                        .addHeader("Cookie", " name=" + name))
                .build();

        new HttpClient()
                .newCall(request)
                .intercept(callBack)
                .execute(callBack);
    }

    public static final class Builder {
        private Context bContext;
        private File[] bFiles;

        private String bAppNo;
        private String bCustId;

//        public Builder(Context context) {
//            this.bContext = context;
//        }

        public Builder files(File[] files) {
            this.bFiles = files;
            return this;
        }

        public Builder appNo(String appNo) {
            this.bAppNo = appNo;
            return this;
        }

        public Builder custId(String custId) {
            this.bCustId = custId;
            return this;
        }

        public FileUpload upload(FileCallBack callBack) {
            try {
                return new FileUpload(this, callBack);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
