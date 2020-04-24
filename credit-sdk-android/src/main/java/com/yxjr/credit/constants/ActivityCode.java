package com.yxjr.credit.constants;

/**
 * Created by xiaochangyou on 2018/5/29.
 */

public interface ActivityCode {

    interface Request {
        /**
         * 身份证正面拍照
         */
        int AUTONYM_CERTIFY_ID_CARD_FRONT = 1001;
        /**
         * 身份证反面拍照
         */
        int AUTONYM_CERTIFY_ID_CARD_VERSO = 1002;
        /**
         * 手持身份证拍照
         */
        int AUTONYM_CERTIFY_ID_CARD_HAND = 1003;
        /**
         * 行驶证拍照
         */
        int CAR_CREDENTIAL = 2001;
        /**
         * 房产证拍照
         */
        int HOUSE_CREDENTIAL = 3001;
        /**
         * 刷卡付款
         */
        int SWIPING_CARD_PAY = 4001;
        /**
         * 调用系统通讯录
         **/
        int SYSTEM_CONTACT = 5001;
        /**
         * 打开通讯录
         */
        int CONTACTS_REQUEST_CODE = 5001;
        /**
         * webveiw打开系统相机
         */
        int OPEN_SYS_CRMERA = 6001;
        /**
         * webview打开系统相册
         */
        int OPEN_SYS_GALLERY = 6002;
        /**
         * webview打开系统文件
         */
        int OPEN_SYS_FILE = 6003;
    }

    interface Result {
        /**
         * 刷卡完成回传返回码
         */
        int SWIPING_CARD_PAY_BACK_RESCODE = 100;

    }
}
