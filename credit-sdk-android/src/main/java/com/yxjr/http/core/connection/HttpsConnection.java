package com.yxjr.http.core.connection;

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
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * https连接
 */
public class HttpsConnection extends Connection {
    private HttpsURLConnection mHttpsUrlConnection;
    private IUploadListener mListener;

    public HttpsConnection(HttpClient client, Request request) {
        super(client, request);
    }

    public HttpsConnection(HttpClient client, Request request, IUploadListener listener) {
        super(client, request);
        this.mListener = listener;
    }

    @Override
    void connect(URLConnection connection, String method) throws IOException {
        mHttpsUrlConnection = (HttpsURLConnection) connection;
        mHttpsUrlConnection.setSSLSocketFactory(mClient.getSslSocketFactory());
        mHttpsUrlConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mHttpsUrlConnection.setRequestMethod(method);
        mHttpsUrlConnection.setUseCaches(true);
        mHttpsUrlConnection.setConnectTimeout(mRequest.timeout());
        mHttpsUrlConnection.setRequestProperty("Accept-Language", "zh-CN");
        mHttpsUrlConnection.setRequestProperty("Charset", mRequest.encode());
        mHttpsUrlConnection.setRequestProperty("Connection", "Keep-Alive");
    }

    @Override
    void post() throws IOException {
        mHttpsUrlConnection.setDoOutput(true);
        mHttpsUrlConnection.setRequestProperty("Content-Type", getContentType(mRequest.params()));
        mOutputStream = new DataOutputStream(mHttpsUrlConnection.getOutputStream());
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
            String serviceId = mHttpsUrlConnection.getRequestProperty("serviceId");
            if (null == serviceId || TextUtils.isEmpty(serviceId))
                callBack.onResponse(new Response(mHttpsUrlConnection.getResponseCode(), mInputStream, mHttpsUrlConnection.getHeaderFields(), mRequest.encode(), mHttpsUrlConnection.getContentLength()));
            else
                callBack.onResponse(new Response(serviceId, mHttpsUrlConnection.getResponseCode(), mInputStream, mHttpsUrlConnection.getHeaderFields(), mRequest.encode(), mHttpsUrlConnection.getContentLength()));
        }
    }

    @Override
    Response getResponse() throws IOException {
        return new Response(mHttpsUrlConnection.getResponseCode(), mInputStream, mHttpsUrlConnection.getHeaderFields(), mRequest.encode(), mHttpsUrlConnection.getContentLength());
    }

    @Override
    public void disconnect() {
        if (mHttpsUrlConnection != null)
            mHttpsUrlConnection.disconnect();
    }

    @Override
    void finish() {
        IO.close(mOutputStream, mInputStream);
    }
}
