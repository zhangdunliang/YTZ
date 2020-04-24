/**
 * RSASecurity
 * cn.com.yxjr.credit.security
 * RSASecurity.java
 * <p>
 * XiaoChangYou 2016-5-6 下午3:21:50
 * 2016 YXJR-版权所有
 */
package com.card.calendar.security;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSASecurity {

    private static Cipher cipher;

    static {
        try {
            try {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥对
     *
     * @param filePath 生成密钥的路径
     * @return
     */
    @SuppressLint("TrulyRandom")
    public static Map<String, String> generateKeyPair(String filePath) {

        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);// 密钥位数
            KeyPair keyPair = keyPairGen.generateKeyPair();// 密钥对
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();// 公钥
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();// 私钥
            String publicKeyString = getKeyString(publicKey);// 得到公钥字符串
            String privateKeyString = getKeyString(privateKey);// 得到私钥字符串
            // 将密钥对写入到文件
            FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");
            FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");
            BufferedWriter pubbw = new BufferedWriter(pubfw);
            BufferedWriter pribw = new BufferedWriter(prifw);
            pubbw.write(publicKeyString);
            pribw.write(privateKeyString);
            pubbw.flush();
            pubbw.close();
            pubfw.close();
            pribw.flush();
            pribw.close();
            prifw.close();
            // 将生成的密钥对返回
            Map<String, String> map = new HashMap<String, String>();
            map.put("publicKey", publicKeyString);
            map.put("privateKey", privateKeyString);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {

        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {

        byte[] keyBytes;
        keyBytes = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @return
     */
    public static String getKeyString(Key key) throws Exception {

        byte[] keyBytes = key.getEncoded();
        return new String(Base64.encode(keyBytes, Base64.DEFAULT), "UTF-8");
    }

    /**
     * 使用公钥对明文进行加密，返回BASE64编码的字符串
     *
     * @param plainText
     * @param publicKey
     * @return
     */
    public static String encrypt(String plainText, PublicKey publicKey) {

        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return new String(Base64.encode(enBytes, Base64.DEFAULT), "UTF-8");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用keystore对明文进行加密
     *
     * @param plainText       明文
     * @param publicKeyString 公钥文件路径
     * @return
     */
    public static String encrypt(String plainText, String publicKeyString) {

        try {
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyString));
            byte[] enBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return new String(Base64.encode(enBytes, Base64.DEFAULT), "UTF-8");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用私钥对明文密文进行解密
     *
     * @param privateKey
     * @param enStr
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String enStr) {

        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(Base64.decode(enStr, Base64.DEFAULT));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用keystore对密文进行解密
     *
     * @param privateKeyString 私钥路径
     * @param enStr            密文
     * @return
     */
    public static String decrypt(String privateKeyString, String enStr) {

        try {
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyString));
            byte[] deBytes = cipher.doFinal(Base64.decode(enStr, Base64.DEFAULT));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
