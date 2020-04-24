package com.yxjr.credit.constants;

/**
 * Created by xiaochangyou on 2018/5/9.
 */

public class HttpService {

    /* 获取产品 */
    public static String GET_PRODUCT = "1000";
    /* 初始化Token */
    public static String INIT_SESSION_TOKEN = "1010";
    /* 获取用户状态或登陆状态 */
    public static String GET_USER_STATE = "1011";
    /* 心跳 */
    public static String HEART_BEAT = "1020";
    /* 同盾心跳 */
    public static String BLACK_BOX_HEART_BEAT = "1021";
    /* 实名认证－提交验证 || 补件 */
    public static String NAME_ASSET_APPROVE = "2020";
    /* 车产认证－提交验证 || 补件 */
    public static String CAR_ASSET_APPROVE = "2027";
    /* 房产认证－提交验证 || 补件 */
    public static String HOUSE_ASSET_APPROVE = "2028";
    /* 获取开关参数 */
    public static String GET_PARAM = "2058";
    /* 补件实名认证－提交验证 */
    public static String NAME_ASSET_APPROVE_PATCH = "2061";
    /* 补件车产认证－提交验证 */
    public static String CAR_ASSET_APPROVE_PATCH = "2062";
    /* 补件房产认证－提交验证 */
    public static String HOUSE_ASSET_APPROVE_PATCH = "2063";
    /* 提交觐见 */
    public static String SUBMIT = "2098";
    /* 记录用户操作 */
    public static String RECORD = "2102";
    /* 发送通讯录 */
    public static String SEND_CONTACTS = "3010";
    /* 发送短信 */
    public static String SEND_SMS = "3011";
    /* 发送浏览器访问记录 */
    public static String SEND_BROWSER_HISTORY = "3012";
    /* 发送通话记录 */
    public static String SEND_CALL_LOG = "3013";
    /* 发送app列表 */
    public static String SEND_APP_LIST = "3014";
    /* 发送照片信息 */
    public static String SEND_IMG_EXIF = "3015";
    public static String SEND_SMALL_DATA = "3016";
    /* 放款确认 */
    public static String DEPOSIT_CONFIRM = "5020";
    /* 查询账户信息 */
    public static String GET_ACCOUNT_STATE = "5002";
    /* 查询合同列表 */
    public static String GET_CONTRACT_LIST = "5021";

    /* 用户身份证信息，扫描身份证 */
    public static String IDENTITY_INFO = "2103";
    /* 用户人脸信息，活体验证 */
    public static String FACE_INFO = "2106";
    /* 补件，用户身份证信息，扫描身份证 */
    public static String PATCH_IDENTITY_INFO = "2107";
    /* 补件，用户人脸信息，活体验证 */
    public static String PATCH_FACE_INFO = "2109";

    public static String OCR = "1002";
}
