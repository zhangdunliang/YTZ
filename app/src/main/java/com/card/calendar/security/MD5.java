package com.card.calendar.security;

import java.security.MessageDigest;

public class MD5 {

    private static com.card.calendar.security.MD5 MD5 = null;

    private MD5() {
    }

    public static synchronized com.card.calendar.security.MD5 getInstance() {
        if (MD5 == null)
            MD5 = new MD5();
        return MD5;
    }

    /**
     * 将字符串加密
     *
     * @param string
     * @return
     */
    public static String encrypt(String string) {
        if (string != null && !"".equals(string)) {
            return getInstance().getMD5ByBytes(string.getBytes());
        } else {
            return string;
        }
    }

    private String getMD5ByBytes(byte[] source) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};// 用来将字节转换成16进制表示的字符
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个128位的长整数， 用字节表示就是16个字节
            char str[] = new char[16 * 2]; // 每个字节用16进制表示的话，使用两个字符，所以表示成16进制需要32个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5的每一个字节，转换成16进制字符的转换
                byte byte0 = tmp[i]; // 取第i个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高4位的数字转换,>>>为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低4位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @SuppressWarnings("unused")
    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        int t = 0;
        for (int i = 0; i < b.length; i++) {
            t = b[i];
            if (t < 0) {
                t += 256;
            }
            if (t < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(t));
        }
        return sb.toString();
    }

}
