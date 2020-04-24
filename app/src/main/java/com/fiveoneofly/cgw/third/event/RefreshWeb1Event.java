package com.fiveoneofly.cgw.third.event;

/**
 * Created by xiaochangyou on 2018/6/13.
 */
public class RefreshWeb1Event {
    private String id;
    private String code;
    private String data;

    public RefreshWeb1Event(String id, String code, String data) {
        this.id = id;
        this.code = code;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
