package com.yxjr.http.core.connection;

import android.os.Build;
import android.text.TextUtils;

import com.yxjr.http.HttpClient;
import com.yxjr.http.builder.Request;
import com.yxjr.http.core.Response;
import com.yxjr.http.core.call.IRequestCallBack;
import com.yxjr.http.core.call.IUploadListener;
import com.yxjr.http.core.io.AbsHttpContent;
import com.yxjr.http.core.io.IO;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * http连接
 */
public class HttpConnection extends Connection {
    private HttpURLConnection mHttpUrlConnection;
    private IUploadListener mListener;

    public HttpConnection(HttpClient client, Request request) {
        super(client, request);
    }

    public HttpConnection(HttpClient client, Request request, IUploadListener listener) {
        super(client, request);
        this.mListener = listener;
    }

    @SuppressWarnings("deprecation")
    @Override
    void connect(URLConnection connection, String method) throws IOException {

        mHttpUrlConnection = (HttpURLConnection) connection;
        mHttpUrlConnection.setRequestMethod(method);
        //		mHttpUrlConnection.setUseCaches(true);
        mHttpUrlConnection.setConnectTimeout(mRequest.timeout());

        //		mHttpUrlConnection.setChunkedStreamingMode(1024);//这样设置有问题
        //		if (mHttpUrlConnection.getContentLength() > 0) {
        //			mHttpUrlConnection.setFixedLengthStreamingMode(mHttpUrlConnection.getContentLength());
        //		} else {
        //			mHttpUrlConnection.setChunkedStreamingMode(1024 * 1024);// 输出流块大小
        //		}

        //		mHttpUrlConnection.setRequestProperty("Accept-Language", "zh-CN");
        mHttpUrlConnection.setUseCaches(false);//POST方法不能缓存，要手动设置为false
        mHttpUrlConnection.setRequestProperty("Charset", mRequest.encode());
        mHttpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
        mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
        if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
            mHttpUrlConnection.setRequestProperty("Connection", "close");
        }
    }

    @Override
    void post() throws IOException {
        mHttpUrlConnection.setDoOutput(true);
        mHttpUrlConnection.setRequestProperty("Content-Type", getContentType(mRequest.params()));
        mOutputStream = new DataOutputStream(mHttpUrlConnection.getOutputStream());
        AbsHttpContent body = mRequest.content();
        if (body != null) {
            body.setOutputStream(mOutputStream);
            body.doOutput(mListener);
        }
    }

    @Override
    void get() throws IOException {

    }

    @Override
    void put() throws IOException {
        post();
    }

    @Override
    void delete() throws IOException {

    }

    @Override
    void patch() throws IOException {

    }

    @Override
    void onResponse(IRequestCallBack callBack) throws IOException {
        if (null != callBack) {
            String serviceId = mHttpUrlConnection.getRequestProperty("serviceId");
            if (null == serviceId || TextUtils.isEmpty(serviceId))
                callBack.onResponse(new Response(mHttpUrlConnection.getResponseCode(), mInputStream, mHttpUrlConnection.getHeaderFields(), mRequest.encode(), mHttpUrlConnection.getContentLength()));
            else
                callBack.onResponse(new Response(serviceId, mHttpUrlConnection.getResponseCode(), mInputStream, mHttpUrlConnection.getHeaderFields(), mRequest.encode(), mHttpUrlConnection.getContentLength()));
        }
    }

    @Override
    Response getResponse() throws IOException {
        String serviceId = mHttpUrlConnection.getRequestProperty("serviceId");
        if (null == serviceId || TextUtils.isEmpty(serviceId))
            return new Response(
                    mHttpUrlConnection.getResponseCode(),
                    mInputStream,
                    mHttpUrlConnection.getHeaderFields(),
                    mRequest.encode(),
                    mHttpUrlConnection.getContentLength()
            );
        else
            return new Response(
                    serviceId,
                    mHttpUrlConnection.getResponseCode(),
                    mInputStream,
                    mHttpUrlConnection.getHeaderFields(),
                    mRequest.encode(),
                    mHttpUrlConnection.getContentLength()
            );
    }

    @Override
    public void disconnect() {
        if (mHttpUrlConnection != null) {
            IO.close(mInputStream, mOutputStream);
            //mHttpUrlConnection.disconnect();
        }

    }

    @Override
    void finish() {
        IO.close(mOutputStream, mInputStream);
    }
}
