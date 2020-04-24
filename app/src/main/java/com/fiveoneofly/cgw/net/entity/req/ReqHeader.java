package com.fiveoneofly.cgw.net.entity.req;


import com.fiveoneofly.cgw.net.entity.req.reqHeader.App;
import com.fiveoneofly.cgw.net.entity.req.reqHeader.Device;
import com.fiveoneofly.cgw.net.entity.req.reqHeader.Net;

public class ReqHeader {

    private Device device;
    private Net net;
    private App app;
    private String sessionId;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
