package com.fiveoneofly.cgw.net.entity.bean;

public class VersionRequest {

    private String appType;// 安卓：1
    private String bundleId;// 包名

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }
}
