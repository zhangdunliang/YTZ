package com.fiveoneofly.cgw.net.api;

/**
 * Created by xiaochangyou on 2017/6/14.
 */
public enum ApiCode {


    R001("001", "appToken失效"),
    R002("002", "系统升级中"),
    R003("003", "APP客户端版本过低"),
    R004("004", "未登录"),//登录过期
    R005("005", "您已经在另一台机器上登录，请重新登录！"),
    R000("000", "成功"),// Header成功

    E1000("1000", "未知错误"),// 未知

    E2001("2001", "请求报文为空"),//

    E3001("3001", "NoSuchAlgorithmException"),//NoSuchAlgorithmException
    E3002("3002", "BadPaddingException"),//BadPaddingException
    E3003("3003", "加密异常3"),//InvalidKeyException
    E3004("3004", "加密异常4"),//InvalidAlgorithmParameterException
    E3005("3005", "加密异常5"),//NoSuchPaddingException
    E3006("3006", "加密异常6"),//IllegalBlockSizeException
    E3007("3007", "加密异常7"),//UnsupportedEncodingException
    E3008("3008", "AES加密失败"),//UnsupportedEncodingException
    E3009("3009", "AES解密失败"),//UnsupportedEncodingException

    E4001("4001", "网络错误1"),//EOFException
    E4002("4002", "网络错误2"),//ConnectException
    E4003("4003", "网络错误3"),//SocketException
    E4004("4004", "网络错误4"),//SocketTimeoutException
    E4005("4005", "网络错误5"),//BindException
    E4006("4006", "网络错误6"),//UnknownHostException

    E5001("5001", "解析异常1"),//JSONException
    E5002("5002", "解析异常2"),//JsonProcessingException

    E6001("6001", "请求类型错误"),//
    E6002("6002", "报文解析异常"),

    E9020("9020", "未登录"),//未登录或登录过期

    S0000("0000", "成功");// Content成功

    private String code;
    private String message;

    ApiCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
