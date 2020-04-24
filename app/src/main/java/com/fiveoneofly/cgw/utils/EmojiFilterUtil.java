package com.fiveoneofly.cgw.utils;

import java.util.regex.Pattern;

/**
 * 用于表情筛选
 */
public class EmojiFilterUtil {

    /**
     * 匹配非表情符号的正则表达式
     */
    private static final String reg = "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";

    public static final Pattern pattern = Pattern.compile(reg);

    /**
     * 检测是否有emoji字符
     *
     * @param source 字符
     * @return FALSE，包含图片
     */
    public static boolean containsEmoji(String source) {
        boolean isEmoji = false;
        if (source.equals("")) {
            return isEmoji;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                // do nothing，判断到了这里表明，确认有表情字符
                isEmoji = true;
                break;
            }
        }
        return isEmoji;
    }

    public static boolean filter(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length() - 1; i++) {
            int ch = str.charAt(i);
            int min = Integer.parseInt("E001", 16);
            int max = Integer.parseInt("E537", 16);
            if (ch >= min && ch <= max) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        boolean isEmoji = false;
        if (codePoint == 0x0) {
            isEmoji = true;
        } else if (codePoint == 0x9) {
            isEmoji = true;
        } else if (codePoint == 0xA) {
            isEmoji = true;
        } else if (codePoint == 0xD) {
            isEmoji = true;
        } else if ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) {
            isEmoji = true;
        } else if ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) {
            isEmoji = true;
        }
        //		else if ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)) {
        //			isEmoji = true;
        //		}//findBugs：char只能存65535个字，0x10000(65536)、0x10FFFF(1114111)已经超长了
        return isEmoji;
    }

    /**
     * @return 过滤emoji 或者 其他非文字类型的字符
     */
    public static String filterEmoji(String source) {
        if (!containsEmoji(source)) {
            return source;// 如果不包含，直接返回
        }
        StringBuilder buf = null;// 到这里铁定包含
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codePoint);
            } else {
            }
        }
        if (buf == null) {
            return source;// 如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }
}