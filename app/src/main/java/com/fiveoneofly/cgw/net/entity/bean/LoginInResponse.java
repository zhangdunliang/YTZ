package com.fiveoneofly.cgw.net.entity.bean;

import java.io.Serializable;

public class LoginInResponse extends BasicResponse implements Serializable {

    private String phoneNo;// 用户手机号
    private String userName;// 用户姓名
    private String userPic;// 用户头像
    private String identityResult;// 实名认证状态
    private String custCert;// 用户身份证号
    private String custId;// 用户custId
    private String appId;
    private String smsNum;// 消息未读数

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getIdentityResult() {
        return identityResult;
    }

    public void setIdentityResult(String identityResult) {
        this.identityResult = identityResult;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustCert() {
        return custCert;
    }

    public void setCustCert(String custCert) {
        this.custCert = custCert;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSmsNum() {
        return smsNum;
    }

    public void setSmsNum(String smsNum) {
        this.smsNum = smsNum;
    }
}
