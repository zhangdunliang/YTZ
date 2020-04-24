package com.fiveoneofly.cgw.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.app.manage.VersionManage;
import com.fiveoneofly.cgw.calm.CalmController;
import com.fiveoneofly.cgw.constants.CacheKey;
import com.fiveoneofly.cgw.controller.LocationController;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.LoginInResponse;
import com.fiveoneofly.cgw.service.Service;
import com.fiveoneofly.cgw.third.getui.PushController;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class FirstActivity extends BasicActivity implements EasyPermissions.PermissionCallbacks {

    // 引导页请求码
    private final int REQUEST_CODE_INTRO = 100;
//    private SkipCountDownTimer mSkipCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        /*
         * 首次逻辑：
         *
         * 首次或版本首次      → first = 0 或 first < versionCode
         * 清除缓存「视为首次」 → first = 0
         * 非首次             → first = versionCode
         * 非正常情况          → first > versionCode
         */
        int first = SharedUtil.getInt(this, CacheKey.SKEY_FIRST);
        int versionCode = AndroidUtil.getVersionCode(this);

        boolean isFirst;
        isFirst = first <= 0 || first != versionCode;


        if (isFirst) {
            startIntroActivity();
            SharedUtil.save(this, CacheKey.SKEY_FIRST, versionCode);
            return;// 注意：首次仅执行到这，终止！
        }

//        mSkipCountDownTimer = new SkipCountDownTimer(1000, 1000);
//        mSkipCountDownTimer.start();
        requestPerms();
    }



    @Override
    protected void onResume() {
        super.onResume();
        PushController.initialize(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mSkipCountDownTimer != null)
//            mSkipCountDownTimer.cancel();
//        mSkipCountDownTimer = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            requestPerms();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        check();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        EasyPermissions.requestPermissions(this, AndroidUtil.getAppName(this) + "需要存储数据至SD卡的权限", 10, perms.toArray(new String[perms.size()]));
    }

    private void requestPerms() {
        String[] perms = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.WRITE_CONTACTS,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_CALL_LOG,
//                Manifest.permission.WRITE_CALL_LOG,
//                Manifest.permission.BODY_SENSORS

        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            check();
        } else {
            EasyPermissions.requestPermissions(this, AndroidUtil.getAppName(this) + "需要存储数据至SD卡的权限", 10, perms);
        }
    }

    /**
     * 1、版本校验：
     * 检测到强制更新：→
     * 检测到更新：→
     * 检测为最新：放行 →
     */
    private void check() {
        final VersionManage versionManage = new VersionManage(this);

        versionManage.checkVersion(false, new VersionManage.OnCheckVersionCallback() {
            @Override
            public void onLatest() {
                if (UserManage.get(FirstActivity.this).isLogin())
                    checkLogin();
                else
                    calm();
            }

            @Override
            public void onUpdate(boolean isLowVersion, String desc, String url) {
                versionManage.showUpdateDialog(desc, url, isLowVersion, new VersionManage.OnDialogCallback() {
                    @Override
                    public void onWait() {
                        if (UserManage.get(FirstActivity.this).isLogin())
                            checkLogin();
                        else
                            calm();
                    }
                });
            }
        });
    }

    // 2、免登陆校验
    private void checkLogin() {
        Map<String, String> request = new HashMap<>();
        request.put("custId", UserManage.get(this).custId());
        new ApiRealCall(this, ServiceCode.CHECK_LOGIN)
                .request(request, LoginInResponse.class, new ApiCallback<LoginInResponse>() {
                    @Override
                    public void onSuccess(LoginInResponse response) {
                        UserManage.get(FirstActivity.this).loginIn(response);
                        calm();
                    }

                    @Override
                    public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
//                        Toast.makeText(FirstActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                        if (errorCode.equals(ApiCode.E9020.getCode())) {
                            UserManage.get(FirstActivity.this).loginOut();
//                        }
                        calm();
                    }

                });
    }

    @SuppressLint("MissingPermission")
    private void calm() {
        if (UserManage.get(this).isLogin() && UserManage.get(this).identityStatus()) {
            LocationController locationController = new LocationController(this);
            locationController.register();

            CalmController.getInstance().start(this);
            startService(new Intent(this, Service.class));

//        stopService(new Intent(this, Service.class));
//        CalmController.getInstance().stop();
//
//        if (locationController != null) {
//            mLocationController.unRegister();
//        }
        }
        startMainActivity();
    }

    private void startMainActivity() {
        if (UserManage.get(this).isLogin()) {
            if (UserManage.get(this).identityStatus()) {
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
                FirstActivity.this.finish();
            } else {// 未实名
                startIdCardVerifyActivity();
            }
        } else {// no login
            startLoginActivity();
        }

    }

    private void startIdCardVerifyActivity() {
        startActivity(new Intent(FirstActivity.this, IdCardVerifyActivity.class));
        FirstActivity.this.finish();
    }

    private void startLoginActivity() {
        startActivity(new Intent(FirstActivity.this, LoginActivity.class));
        FirstActivity.this.finish();
    }

    private void startIntroActivity() {
        startActivityForResult(new Intent(FirstActivity.this, IntroActivity.class), REQUEST_CODE_INTRO);
    }
}
