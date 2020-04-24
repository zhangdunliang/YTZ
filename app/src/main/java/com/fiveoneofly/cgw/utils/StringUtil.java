package com.fiveoneofly.cgw.utils;

public class StringUtil {

    //是否为空
    public static boolean isEmpty(CharSequence str) {
        if (null == str || str.length() == 0)
            return true;
        else
            return false;
    }

    //是否不为空
    public static boolean isNotEmpty(CharSequence str) {
        if (null != str && str.length() > 0)
            return true;
        else
            return false;
    }

    //获取最后四位字符
    public static String laterFour(String str) {
        if (isEmpty(str)) {
            return null;
        }
        if (str.length() >= 4) {// 判断是否长度大于等于4
            return str.substring(str.length() - 4, str.length());
        } else {
            return str;
        }
    }
}
