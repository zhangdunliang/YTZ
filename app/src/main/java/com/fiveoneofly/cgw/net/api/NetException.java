package com.fiveoneofly.cgw.net.api;

/**
 * Created by xiaochangyou on 2017/6/14.
 */
public class NetException extends Exception {

    private String mCode;
    private String mMessage;

    private NetException() {
    }

    public NetException(ApiCode apiCode) {
        super(apiCode.getMessage());
        this.mCode = apiCode.getCode();
        this.mMessage = apiCode.getMessage();
    }

    NetException(String code, String message) {
        super(message);
        this.mCode = code;
        this.mMessage = message;
    }

    public String getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

}
