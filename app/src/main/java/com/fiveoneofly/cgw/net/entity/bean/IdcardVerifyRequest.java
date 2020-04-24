package com.fiveoneofly.cgw.net.entity.bean;


public class IdcardVerifyRequest {

    private String custId;
    private String mobileNo;// 手机号
    private String certFront;// 身份证正面
    private String certBack;// 身份证反面
    private String certFrontPortrait;// 身份证正面人像
    private String picType;// 图片类型

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCertFront() {
        return certFront;
    }

    public void setCertFront(String certFront) {
        this.certFront = certFront;
    }

    public String getCertBack() {
        return certBack;
    }

    public void setCertBack(String certBack) {
        this.certBack = certBack;
    }

    public String getCertFrontPortrait() {
        return certFrontPortrait;
    }

    public void setCertFrontPortrait(String certFrontPortrait) {
        this.certFrontPortrait = certFrontPortrait;
    }

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }
}
