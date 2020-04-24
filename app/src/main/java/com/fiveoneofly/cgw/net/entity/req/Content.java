package com.fiveoneofly.cgw.net.entity.req;

public class Content<B> {
    private String reqService;
    private ReqHeader reqHeader;
    private B reqBody;

    public String getReqService() {
        return reqService;
    }

    public void setReqService(String reqService) {
        this.reqService = reqService;
    }

    public ReqHeader getReqHeader() {
        return reqHeader;
    }

    public void setReqHeader(ReqHeader reqHeader) {
        this.reqHeader = reqHeader;
    }

    public B getReqBody() {
        return reqBody;
    }

    public void setReqBody(B reqBody) {
        this.reqBody = reqBody;
    }
}
