/**
 * AESSecurity
 * cn.com.yxjr.credit.security
 * AESSecurity.java
 * <p>
 * XiaoChangYou 2016-5-6 下午3:10:20
 * 2016 YXJR-版权所有
 */
package com.card.calendar.security;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESSecurity {
    public static final String TAG = "AESSecurity";
    public static final String SEC_PROVIDER = "BC";
    public static final String CHARACTER_SET = "UTF-8";

    public static String encrypt(String data, String key) throws Exception {
        return encryptWithBase64(data, key.getBytes());
    }

    public static String decrypt(String data, String key) throws Exception {
        return decryptWithBase64(data, key.getBytes());
    }

    @SuppressLint("TrulyRandom")
    private static String encryptWithModeAndIV(String content, String key, String mode, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(mode, "BC");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        cipher.init(1, keyspec);
        byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
        return android.util.Base64.encodeToString(encrypted, 0);
    }

    @SuppressWarnings("unused")
    private static String decryptWithModeAndIV(String content, String key, String mode, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(mode, "BC");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        cipher.init(2, keyspec);
        byte[] encrypted = android.util.Base64.decode(content.getBytes(), 0);
        byte[] original = cipher.doFinal(encrypted);
        return new String(original, "UTF-8");
    }

    public static String decryptWithBase64(String base64encrypt, byte[] keyBytes) throws Exception {
        byte[] originBytes = null;
        byte[] content = Base64.decodeBase64(base64encrypt);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = getAesKey(keyBytes);
        cipher.init(2, key);
        originBytes = cipher.doFinal(content);
        return new String(originBytes);
    }

    public static String encryptWithBase64(String base64decrypt, byte[] keyBytes) throws Exception {
        SecretKeySpec key = getAesKey(keyBytes);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
        byte[] byteContent = base64decrypt.getBytes("UTF-8");
        cipher.init(1, key);
        byte[] result = cipher.doFinal(byteContent);
        String resultStr = Base64.encodeBase64String(result);
        return resultStr;
    }

    public static SecretKeySpec getAesKey(byte[] salt) throws Exception {
        SecretKeySpec key = null;
        if (salt != null) {
            if (salt.length != 16) {
                throw new Exception("AES decrypt key length not enough!");
            }
            key = new SecretKeySpec(salt, "AES");
        }
        return key;
    }
}
