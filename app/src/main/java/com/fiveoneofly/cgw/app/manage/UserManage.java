package com.fiveoneofly.cgw.app.manage;

import android.content.Context;
import android.text.TextUtils;

import com.card.calendar.security.MD5;
import com.fiveoneofly.cgw.app.activity.LoginActivity;
import com.fiveoneofly.cgw.cache.helper.Cache;
import com.fiveoneofly.cgw.cache.helper.CacheManager;
import com.fiveoneofly.cgw.constants.CacheKey;
import com.fiveoneofly.cgw.net.entity.bean.LoginInResponse;
import com.fiveoneofly.cgw.utils.SharedUtil;

/**
 * 用户管理
 */
public class UserManage {

    private static volatile UserManage sUserManage;
    private CacheManager mCacheManager;
    private Context mContext;

    private UserManage(Context context) {
        mContext = context.getApplicationContext();
        mCacheManager = Cache.with(context);
    }

    public static UserManage get(Context context) {
        if (sUserManage == null) {
            synchronized (UserManage.class) {
                if (sUserManage == null) {
                    sUserManage = new UserManage(context.getApplicationContext());
                }
            }
        }
        return sUserManage;
    }

    /**
     * 登录
     */
    public void loginIn(LoginInResponse loginInResponse) {
        SharedUtil.remove(mContext, CacheKey.NOW_PHONE);
        mCacheManager.saveCache(CacheKey.KEY_USER_INFO, loginInResponse);
    }

    /**
     * 登出
     */
    public void loginOut() {
        SharedUtil.save(mContext, CacheKey.NOW_PHONE, phoneNo());
        mCacheManager.remove(CacheKey.KEY_USER_INFO);
    }

    /**
     * 获取用户信息
     */
    public LoginInResponse getUser() {
        LoginInResponse loginInResponse = mCacheManager.getCache(CacheKey.KEY_USER_INFO, LoginInResponse.class);
        if (loginInResponse == null) {
            loginInResponse = new LoginInResponse();
        }
        return loginInResponse;
    }

    /**
     * 是否登录
     */
    public boolean isLogin() {
        boolean isLogin = false;
        LoginInResponse loginInResponse = mCacheManager.getCache(CacheKey.KEY_USER_INFO, LoginInResponse.class);
        if (loginInResponse != null) {
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 是否实名
     */
    public boolean identityStatus() {
        String identityResult = getUser().getIdentityResult();
        identityResult = TextUtils.isEmpty(identityResult) ? "N" : identityResult;
        return identityResult.equals("Y");
    }

    /**
     * 用户ID
     */
    public String custId() {
        return TextUtils.isEmpty(getUser().getCustId()) ? "" : getUser().getCustId();
    }

    /**
     * 姓名
     */
    public String userName() {
        return TextUtils.isEmpty(getUser().getUserName()) ? "" : getUser().getUserName();
    }

    /**
     * 身份证
     */
    public String userCert() {
        return TextUtils.isEmpty(getUser().getCustCert()) ? "" : getUser().getCustCert();
    }

    /**
     * 身份证MD5
     */
    public String userCertMD5() {
        return MD5.encrypt(userCert());
    }

    /**
     * 手机号
     */
    public String phoneNo() {
        return TextUtils.isEmpty(getUser().getPhoneNo()) ? "" : getUser().getPhoneNo();
    }

    /**
     * 头像地址
     */
    public String headPic() {
        return TextUtils.isEmpty(getUser().getUserPic()) ? "null" : getUser().getUserPic();
    }

    /**
     * 机构号
     */
    public String partnerId() {
        return "20000051";
    }

    /**
     * 消息未读数
     */
    public String smsNum() {
        return TextUtils.isEmpty(getUser().getSmsNum()) ? "0" : getUser().getSmsNum();
    }

}
