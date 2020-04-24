package com.fiveoneofly.cgw.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by xiaochangyou on 2018/6/4.
 */

public class PermissionUtil {

    /**
     * SDK version < 23 全部状态为无法获取，不额外做处理
     */
    public static final String STATUS_UNAUTHORIZED = "0";   // 未授权
    public static final String STATUS_AUTHORIZED = "1";     // 已授权
    public static final String STATUS_UNABLE_GET = "2";     // 无法获取

    public static String getAllPermissionStatus(Context context) {
        String authGPS = hasGpsServer(context);
        String authGPSApp = hasLocation(context);
        String authContacts = hasContacts(context);
        String authSMS = hasSMS(context);
        String authCall = hasCalllog(context);
        String authAppInfo = hasApplist(context);
        String authPhoneInfo = hasPhoneState(context);
        String authBrower = hasBrowser(context);
        String data = authGPS + "|"
                + authGPSApp + "|"
                + authContacts + "|"
                + authSMS + "|"
                + authCall + "|"
                + authBrower + "|"
                + authPhoneInfo + "|"
                + authAppInfo;
        // 1|1|1|0|0|2|1|1
        return data;
    }

    private static String hasPermissions(Context context, String... perms) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(context, perms)) {
                return STATUS_AUTHORIZED;
            } else {
                return STATUS_UNAUTHORIZED;
            }
        } else {
            return STATUS_UNABLE_GET;
        }
    }

    /**
     * 手机定位服务
     */
    public static String hasGpsServer(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean isGpsServer = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsServer ? STATUS_AUTHORIZED : STATUS_UNAUTHORIZED;
    }

    /**
     * 定位权限
     */
    public static String hasLocation(Context context) {
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        return hasPermissions(context, perms);
    }

    /**
     * 通讯录读写权限
     */
    public static String hasContacts(Context context) {
        String[] perms = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        return hasPermissions(context, perms);
    }

    /**
     * 短信读权限
     */
    public static String hasSMS(Context context) {
        String[] perms = {
                Manifest.permission.READ_SMS,
        };
        return hasPermissions(context, perms);
    }

    /**
     * 通话记录读写权限
     */
    public static String hasCalllog(Context context) {
        String[] perms = {
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG
        };
        return hasPermissions(context, perms);
    }

    /**
     * 应用列表权限
     */
    public static String hasApplist(Context context) {
        String status = STATUS_UNABLE_GET;
        try {
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (ApplicationInfo app : listAppcations) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {// 第三方APP
                    String appPkgName = app.packageName;// 包名
                    if (!appPkgName.equals(AndroidUtil.getPackageName(context))) {
                        return STATUS_AUTHORIZED;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = STATUS_UNABLE_GET;
        }
        return status;
    }

    /**
     * 手机设备信息权限
     */
    public static String hasPhoneState(Context context) {
        String[] perms = {
                Manifest.permission.READ_PHONE_STATE
        };
        return hasPermissions(context, perms);
    }

    /**
     * 浏览器历史记录读权限
     */
    public static String hasBrowser(Context context) {
        String[] perms = {
                "com.android.browser.permission.READ_HISTORY_BOOKMARKS"
        };
        return hasPermissions(context, perms);
    }

//    private static boolean checkPermission(Context context, String permission) throws RuntimeException {
//        boolean bool = false;
//        if (context == null) {
//            return bool;
//        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            try {
//                Class localClass = Class.forName("android.content.Context");
//                Method localMethod = localClass.getMethod("checkSelfPermission", new Class[]{String.class});
//                int i = ((Integer) localMethod.invoke(context, new Object[]{permission})).intValue();
//                if (i == 0) {
//                    bool = true;
//                } else {
//                    bool = false;
//                }
//            } catch (Throwable localThrowable) {
//                bool = false;
//            }
//        } else {
////            PackageManager packageManager = context.getPackageManager();
////            if (packageManager.checkPermission(permission, context.getPackageName()) == 0) {
////                bool = true;
////            }
//            throw new RuntimeException("Build.VERSION < 23 unable check Permission!");
//        }
//        return bool;
//    }

//    /**
//     * 位置信息
//     * 存储空间
//     * 电话
//     * 相机
//     * 短信
//     * 通讯录
//     * ===> 以上权限可设置并申请
//     * <p>
//     * GPS服务开启
//     * 浏览器
//     * 手机信息
//     * 应用程序列表
//     * ===> 额外判断
//     */
//
//    /**
//     * ==> check 权限组
//     * ==> 请求，回调处理
//     * ==>
//     */
//    public static void checkAllPermission(Context context) {
//        String info = "";
//        List<String> tempPermissions = new ArrayList<>();
//        if (checkGpsServer(context).equals(STATUS_UNAUTHORIZED)) {
//            info = "> 手机定位服务\n";
//        }
//        if (checkAppList(context).equals(STATUS_UNAUTHORIZED)) {
//            info += "> 应用程序列表\n";
//        }
//        if (checkLocation(context).equals(STATUS_UNAUTHORIZED)) {
//            info += "> 应用GPS定位\n";
//            tempPermissions.add(PermissionConstants.LOCATION);
//        }
//        if (checkContacts(context).equals(STATUS_UNAUTHORIZED)) {
//            info += "> 手机通讯录\n";
//            tempPermissions.add(PermissionConstants.CONTACTS);
//        }
//        if (checkSMS(context).equals(STATUS_UNAUTHORIZED)) {
//            info += "> 手机短信\n";
//            tempPermissions.add(PermissionConstants.SMS);
//        }
//        if (checkCallLog(context).equals(STATUS_UNAUTHORIZED)) {
//            info += "> 通话记录\n";
//            tempPermissions.add(PermissionConstants.PHONE);
//        }
//        if (checkPhoneInfo(context).equals(STATUS_UNAUTHORIZED)) {
//            info += "> 手机信息[识别码、IMEI]\n";
//            if (!tempPermissions.contains(PermissionConstants.PHONE))
//                tempPermissions.add(PermissionConstants.PHONE);
//        }
//
//        String[] permissions = new String[tempPermissions.size()];
//        for (int i = 0; i < tempPermissions.size(); i++) {
//            permissions[i] = tempPermissions.get(i);
//        }
//        requestPermissions(permissions);
//
////        if (StringUtil.isNotEmpty(info)) {
////            showUnAuthorizedItem(info);
////        }
//    }
//
//    @SuppressLint("WrongConstant")
//    private static void requestPermissions(String... permissions) {
//        Permission.permission(permissions)
//                .rationale(new Permission.OnRationaleListener() {
//                    @Override
//                    public void rationale(final ShouldRequest shouldRequest) {
//                        // 申请授权前
//                        showRationaleDialog(shouldRequest);
////                        LogUtil.d("=================================> rationale");
//
//                    }
//                })
//                .callback(new Permission.FullCallback() {
//                    @Override
//                    public void onGranted(List<String> permissionsGranted) {
//                        // 允许
////                        LogUtil.d("=================================> onGranted：" + permissionsGranted);
//                    }
//
//                    @Override
//                    public void onDenied(List<String> permissionsDeniedForever,
//                                         List<String> permissionsDenied) {
//                        // 拒绝
//                        if (!permissionsDeniedForever.isEmpty()) {
//                            showOpenAppSettingDialog();
//                        }
////                        LogUtil.d("=================================> onDenied：\n===> permissionsDeniedForever："
////                                + permissionsDeniedForever + "\n===> permissionsDenied：" + permissionsDenied);
//                    }
//                })
//                .theme(new Permission.ThemeCallback() {
//                    @Override
//                    public void onActivityCreate(Activity activity) {
//                        ScreenUtils.setFullScreen(activity);
//                    }
//                })
//                .request();
//    }
//
//    private static void showOpenAppSettingDialog() {
//        Activity topActivity = ActivityUtils.getTopActivity();
//        if (topActivity == null) return;
//        new AlertDialog.Builder(topActivity)
//                .setTitle(android.R.string.dialog_alert_title)
//                .setMessage("我们需要一些您拒绝的权限或系统未能应用失败，请手动设置为页面授权，否则该功能将无法正常使用！")
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Permission.launchAppDetailsSettings();
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//    }
//
//    private static void showUnAuthorizedItem(String message) {
//        Activity topActivity = ActivityUtils.getTopActivity();
//        if (topActivity == null)
//            return;
//        new AlertDialog.Builder(topActivity)
//                .setTitle("为了您的账户安全,请打开以下授权,授权后尝试重新提交")
//                .setMessage(message)
//                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Permission.launchAppDetailsSettings();
//                    }
//                })
//                .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//    }
//
//    private static void showRationaleDialog(
//            final Permission.OnRationaleListener.ShouldRequest shouldRequest) {
//        Activity topActivity = ActivityUtils.getTopActivity();
//        if (topActivity == null)
//            return;
//        new AlertDialog.Builder(topActivity)
//                .setTitle(android.R.string.dialog_alert_title)
//                .setMessage("您已拒绝我们申请授权，请同意授权，否则将无法正常使用！")
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        shouldRequest.again(true);
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        shouldRequest.again(false);
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//
//    }
//
//    public static void showOpenGPSServer() {
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//    }
//
//    public static void requestLocationPermission() {
//        Permission.permission(PermissionConstants.LOCATION)
//                .rationale(new Permission.OnRationaleListener() {
//                    @Override
//                    public void rationale(final ShouldRequest shouldRequest) {
//                        // 申请授权前
//                        showRationaleDialog(shouldRequest);
//                    }
//                })
//                .callback(new Permission.FullCallback() {
//                    @Override
//                    public void onGranted(List<String> permissionsGranted) {
//                        // 允许
////                        LogUtil.d("=================================> onGranted：" + permissionsGranted);
//                    }
//
//                    @Override
//                    public void onDenied(List<String> permissionsDeniedForever,
//                                         List<String> permissionsDenied) {
//                        // 拒绝
//                        if (!permissionsDeniedForever.isEmpty()) {
//                            showLocation();
//                        }
////                        LogUtil.d("=================================> onDenied：\n===> permissionsDeniedForever："
////                                + permissionsDeniedForever + "\n===> permissionsDenied：" + permissionsDenied);
//                    }
//                })
//                .theme(new Permission.ThemeCallback() {
//                    @Override
//                    public void onActivityCreate(Activity activity) {
//                        ScreenUtils.setFullScreen(activity);
//                    }
//                })
//                .request();
//    }
//
//    private static void showLocation() {
//        Activity topActivity = ActivityUtils.getTopActivity();
//        new AlertDialog.Builder(topActivity)
//                .setTitle("为了您的账户安全")
//                .setMessage("请打开[定位]授权,授权后尝试重新提交")
//                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Permission.launchAppDetailsSettings();
//                    }
//                })
//                .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//    }
//
//    public static void requestStoragePermission(final Activity activity) {
//
//        new AlertDialog.Builder(activity)
//                .setTitle("注意")
//                .setMessage("请授予存储权限！")
//                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        realRequestStoragePermission(activity);
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        activity.finish();
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//    }
//
//    private static void realRequestStoragePermission(final Activity activity) {
//        Permission.permission(PermissionConstants.STORAGE)
//                .rationale(new Permission.OnRationaleListener() {
//                    @Override
//                    public void rationale(final ShouldRequest shouldRequest) {
//                        showStorage(shouldRequest, activity);
//                    }
//                })
//                .callback(new Permission.FullCallback() {
//                    @Override
//                    public void onGranted(List<String> permissionsGranted) {
//                        // 允许
////                        LogUtil.d("=================================> onGranted：" + permissionsGranted);
//                    }
//
//                    @Override
//                    public void onDenied(List<String> permissionsDeniedForever,
//                                         List<String> permissionsDenied) {
//                        // 拒绝
//                        if (!permissionsDeniedForever.isEmpty()) {
//                            showStorage(null, activity);
//                        }
////                        LogUtil.d("=================================> onDenied：\n===> permissionsDeniedForever："
////                                + permissionsDeniedForever + "\n===> permissionsDenied：" + permissionsDenied);
//                    }
//                })
//                .theme(new Permission.ThemeCallback() {
//                    @Override
//                    public void onActivityCreate(Activity activity) {
//                        ScreenUtils.setFullScreen(activity);
//                    }
//                })
//                .request();
//    }
//
//
//    private static void showStorage(final Permission.OnRationaleListener.ShouldRequest shouldRequest,
//                                    final Activity activity) {
//        new AlertDialog.Builder(activity)
//                .setTitle("注意")
//                .setMessage("请授予存储权限，否则将无法正常使用")
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (shouldRequest != null) {
//                            shouldRequest.again(true);
//                        } else {
//                            Permission.launchAppDetailsSettings();
//                        }
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        activity.finish();
//                    }
//                })
//                .setCancelable(false)
//                .create()
//                .show();
//    }

}
