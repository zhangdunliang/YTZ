package com.fiveoneofly.cgw.net.entity.req.reqHeader;

public class Device {

    /* 设备唯一标识 */
    private String uuid;
    /* IMEI */
    private String imei;
    /* IMSI */
    private String imsi;
    /* 设备厂商 */
    private String brand;
    /* 设备型号 */
    private String model;
    /* 设备系统类型 */
    private String osType;
    /* 设备系统版本 */
    private String osVersion;
    /* 设备系统是否越狱 */
    private String osJailbreak;
    /* 设备出场ID */
    private String factoryId;
    /* 设备安卓ID */
    private String androidId;
    /* 设备SIM卡编号 */
    private String simNo;
    /* 设备SIM卡手机号 */
    private String simMobileNo;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsJailbreak() {
        return osJailbreak;
    }

    public void setOsJailbreak(String osJailbreak) {
        this.osJailbreak = osJailbreak;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public String getSimMobileNo() {
        return simMobileNo;
    }

    public void setSimMobileNo(String simMobileNo) {
        this.simMobileNo = simMobileNo;
    }
}
