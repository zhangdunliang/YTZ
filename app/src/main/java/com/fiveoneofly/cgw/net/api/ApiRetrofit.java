package com.fiveoneofly.cgw.net.api;

import android.util.Log;

import com.fiveoneofly.cgw.constants.HttpParam;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by xiaochangyou on 2017/6/14.
 */
public class ApiRetrofit {
    public static ApiRetrofit mInstance;

    private ApiRetrofit() {
    }

    public static ApiRetrofit getInstance() {
        if (null == mInstance) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    private final String TAG = "ApiRetrofit";

    public Retrofit getRetrofit() {

        //开启Log
//        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                Log.d(TAG, "message:" + message);
//            }
//        });
//        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        //缓存
//        File cacheFile = new File("", "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        Interceptor headInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .build();
                return chain.proceed(request);
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(HttpParam.READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(HttpParam.CONNECT_TIME_OUT, TimeUnit.SECONDS)
//                .addInterceptor(logInterceptor)
                .addInterceptor(headInterceptor)
                //.cache(cache)
                .build();

        return new Retrofit.Builder()
                .baseUrl(HttpParam.SERVER)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())//返回值String的支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
