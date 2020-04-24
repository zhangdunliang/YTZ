package com.fiveoneofly.cgw.net;

import android.support.annotation.StringDef;

import com.fiveoneofly.cgw.app.activity.IdCardVerifyActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ServiceCode {

    public static final String INIT_TOKEN                = "";
//    public static final String NOTICE                    = "7007";// 消息查询接口
    public static final String NOTICE_TYPE               = "7009";// 获取消息类型
    public static final String NOTICE_LIST               = "7010";// 获取消息列表
    public static final String NOTICE_READ               = "7011";// 全部消息设为已读
    public static final String AD_INFO                   = "A1001";// 广告信息查询接口
    public static final String MAIN_BIZ                  = "A1002";// 主业务信息查询接接口
    public static final String SUB_BIZ                   = "A1003";// 附业务信息查询接口
    public static final String LOGIN_IN                  = "1007";// 登录接口
    public static final String USER_INFO                 = "A1005";// 用户信息查询接口
    public static final String LOGIN_OUT                 = "1009";// 登出接口
    public static final String LOGIN_SMS                 = "1008";// 登陆发送短信验证码
    public static final String UPLOAD_HEAD               = "A1008";// 上传头像

    public static final String IDCARD_VERIFY             = "1002";// 身份证图片地址上传及验证
    public static final String IDCARD_VERIFY_CONFIRM     = "1003";// 实名认证信息确认
    public static final String CHECK_LOGIN               = "1010";// 免登陆校验
    public static final String VERSION_UPDATE            = "7008";// 版本检测
    public static final String CUST_BUSINESS_STATUS      = "1001";// 业务开通状态
    public static final String DATA_GRAB_PARAM           = "6668";// 数据抓取-参数



    @StringDef({
            INIT_TOKEN,
//            NOTICE,
            NOTICE_TYPE,
            NOTICE_LIST,
            NOTICE_READ,
            AD_INFO,
            MAIN_BIZ,
            SUB_BIZ,
            LOGIN_IN,
            USER_INFO,
            LOGIN_OUT,
            LOGIN_SMS,
            UPLOAD_HEAD,
            IDCARD_VERIFY,
            IDCARD_VERIFY_CONFIRM,
            CHECK_LOGIN,
            VERSION_UPDATE,
            CUST_BUSINESS_STATUS,
            DATA_GRAB_PARAM
    })
    @Retention(RetentionPolicy.SOURCE) //表示注解所存活的时间,在运行时,而不会存在. class 文件.

    @interface Code { //接口，定义新的注解类型
    }

}
