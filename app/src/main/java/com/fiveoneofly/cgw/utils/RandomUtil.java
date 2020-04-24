package com.fiveoneofly.cgw.utils;

import java.util.Random;

public class RandomUtil {

    public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * @param length 随机字符串长度
     * @return String 返回一个定长的随机字符串(只包含大小写字母、数字)
     */
    public static String generateAllChar(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }

    /**
     * @param length 随机字符串长度
     * @return String 返回一个定长的随机纯字母字符串(只包含大小写字母)
     */
    public static String generateLetterChar(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(letterChar.length())));
        }
        return sb.toString();
    }

}
