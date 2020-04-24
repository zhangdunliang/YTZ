package com.fiveoneofly.cgw.net.api;

import com.fiveoneofly.cgw.constants.HttpParam;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by xiaochangyou on 2017/6/14.
 */
public interface ApiInterface {

    @Headers({
            "Content-Type:application/json",
            "User-Agent:Android",
            "connection:keep-alive",
            "Charset:UTF-8"})
    @POST("/" + HttpParam.API)
    Observable<String> call(@Body String data);

}
