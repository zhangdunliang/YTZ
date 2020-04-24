package com.fiveoneofly.cgw.net.entity.bean;

import java.util.Map;

public class IdcardVerifyResponse extends BasicResponse {

    private Map<String, String> map;// 仅正面有值

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

}
