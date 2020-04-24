package com.fiveoneofly.cgw.app.fragment;

import java.util.List;

/**
 * Created by zhangdunliang on 2018/7/17.
 */

public interface HomeView {
    /**
     * 获取消息
     */
    void onMessage(List<String> list);

    /**
     * 得到用户状态
     */
    void custBusinessStatus(boolean isOpenNoCard, boolean isOpenCredit);

    /**
     * 停止刷新
     */
    void finishRefresh();
}
