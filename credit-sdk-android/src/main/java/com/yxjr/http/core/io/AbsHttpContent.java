package com.yxjr.http.core.io;

import com.yxjr.http.builder.RequestParams;
import com.yxjr.http.core.call.IUploadListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 定义HttpContent 传输数据，使用策略模式
 * ***** FormContent
 * ***** JsonContent
 * ***** MultiPartContent
 */
public abstract class AbsHttpContent {
    public static final String BOUNDARY = "http-net";
    static final String DATA_TAG = "--";
    static final String END = "\r\n";
    String mEncode;
    RequestParams mParams;
    DataOutputStream mOutputStream;

    AbsHttpContent(RequestParams mParams, String encode) {
        this.mEncode = encode == null ? "UTF-8" : encode;
        this.mParams = mParams;
    }

    public void setOutputStream(DataOutputStream outputStream) throws IOException {
        this.mOutputStream = outputStream;
    }

    /**
     * URL编码表单
     */
    String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void doOutput() throws IOException;

    public abstract void doOutput(IUploadListener listener) throws IOException;

    public abstract String intoString();

    void outputEnd() throws IOException {
        mOutputStream.writeBytes(END + DATA_TAG + BOUNDARY + DATA_TAG + END);
        mOutputStream.flush();
        mOutputStream.close();
    }
}
