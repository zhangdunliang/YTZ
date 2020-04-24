package com.fiveoneofly.cgw.net;

import android.content.Context;

import com.fiveoneofly.cgw.app.MainApplication;
import com.fiveoneofly.cgw.constants.HttpParam;
import com.fiveoneofly.cgw.net.api.ApiHandler;
import com.fiveoneofly.cgw.net.api.NetException;
import com.fiveoneofly.cgw.net.entity.req.Request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ApiBatch extends ApiHandler {

    public void request(Context context, String serviceId, Map jsonObject) throws NetException {

        tempToken = MainApplication.token;
        if (tempToken == null)
            return;
        Request req = handleRequest(TYPE_NATIVE, serviceId, handleHeader(context), jsonObject);
        String paramEncrypt = obj2String(req);

        ByteArrayOutputStream baos;
        OutputStream httpOutputStream;
        BufferedInputStream httpInputStream;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(HttpParam.SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.connect();

            httpOutputStream = connection.getOutputStream();
            httpOutputStream.write(paramEncrypt.getBytes());
            httpOutputStream.flush();
            httpOutputStream.close();
            httpInputStream = new BufferedInputStream(connection.getInputStream());
            int count;
            byte[] outputByte = new byte[1024];
            baos = new ByteArrayOutputStream();
            while ((count = httpInputStream.read(outputByte, 0, 1024)) != -1) {
                baos.write(outputByte, 0, count);
            }
            baos.flush();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
