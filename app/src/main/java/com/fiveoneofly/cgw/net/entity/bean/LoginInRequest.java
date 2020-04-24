package com.fiveoneofly.cgw.net.entity.bean;

public class LoginInRequest {

    private String phoneNo;// 手机号
    private String verifyCode;// 验证码
    private String seq;
    private static String cid;//个推id
    private String sourceName;//渠道号

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getCid() {
        return cid;
    }

    public static void setCid(String cid) {
        LoginInRequest.cid = cid;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
