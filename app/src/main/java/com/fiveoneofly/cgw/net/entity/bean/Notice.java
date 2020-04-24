package com.fiveoneofly.cgw.net.entity.bean;


public class Notice {

    private String smsType;
    private String typeDesc;
    private String smsTitle;
    private String smsContent;
    private String smsSendTime;
    private String minContent;
    private String smsId;
    private String picUrl;
    private String linkUrl;
    private String msgType;
    private String readStatus;


    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getSmsTitle() {
        return smsTitle;
    }

    public void setSmsTitle(String smsTitle) {
        this.smsTitle = smsTitle;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getSmsSendTime() {
        return smsSendTime;
    }

    public void setSmsSendTime(String smsSendTime) {
        this.smsSendTime = smsSendTime;
    }

    public String getMinContent() {
        return minContent;
    }

    public void setMinContent(String minContent) {
        this.minContent = minContent;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }
}
