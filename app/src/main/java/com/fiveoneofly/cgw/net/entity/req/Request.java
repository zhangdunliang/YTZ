package com.fiveoneofly.cgw.net.entity.req;

public class Request {
    private Header header;
    private String content;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
