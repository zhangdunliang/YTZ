package com.card.calendar.security;

public class CCSecurity {

    static {
        System.loadLibrary("_cc_security");
    }

    /**
     * 对 string 进行 rsa 加密
     */
    public static native String encryptString(String data);


}


