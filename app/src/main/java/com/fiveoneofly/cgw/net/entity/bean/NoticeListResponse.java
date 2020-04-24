package com.fiveoneofly.cgw.net.entity.bean;

import java.util.List;

public class NoticeListResponse {

    private NoticeMap map;

    public NoticeMap getMap() {
        return map;
    }

    public void setMap(NoticeMap map) {
        this.map = map;
    }

    public class NoticeMap {
        private String totalCnt;
        private List<Notice> smsList;

        public String getTotalCnt() {
            return totalCnt;
        }

        public void setTotalCnt(String totalCnt) {
            this.totalCnt = totalCnt;
        }

        public List<Notice> getSmsList() {
            return smsList;
        }

        public void setSmsList(List<Notice> smsList) {
            this.smsList = smsList;
        }
    }

}
