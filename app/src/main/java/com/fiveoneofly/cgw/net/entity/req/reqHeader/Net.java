package com.fiveoneofly.cgw.net.entity.req.reqHeader;

public class Net {

    private String netType = "aaaa";
    private String ipAddress = "aaaa";
    private String macAddress = "aaaa";
    private String gps = "aaaa";

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }
}
