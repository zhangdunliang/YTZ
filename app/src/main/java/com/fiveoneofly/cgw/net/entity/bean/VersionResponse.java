package com.fiveoneofly.cgw.net.entity.bean;

public class VersionResponse extends BasicResponse {

    private String appVersionId;// 最新版本编号「整数」
    private String appVersionIdOld;// 上一个版本编号「整数」
    private String publishDateTime;// 发布日期时间
    private String appUrl;// 更新路径
    private String lowVersionNo;// 最低版本号，当app版本低此版本时，需要进行强制更新「整数」
    private String appDesc;// 更新详情

    public String getAppVersionId() {
        return appVersionId;
    }

    public void setAppVersionId(String appVersionId) {
        this.appVersionId = appVersionId;
    }

    public String getAppVersionIdOld() {
        return appVersionIdOld;
    }

    public void setAppVersionIdOld(String appVersionIdOld) {
        this.appVersionIdOld = appVersionIdOld;
    }

    public String getPublishDateTime() {
        return publishDateTime;
    }

    public void setPublishDateTime(String publishDateTime) {
        this.publishDateTime = publishDateTime;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getLowVersionNo() {
        return lowVersionNo;
    }

    public void setLowVersionNo(String lowVersionNo) {
        this.lowVersionNo = lowVersionNo;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }
}
