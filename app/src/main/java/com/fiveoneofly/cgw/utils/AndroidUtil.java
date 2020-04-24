package com.fiveoneofly.cgw.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.card.calendar.security.MD5;

import java.io.File;

public class AndroidUtil {

    /**
     * @param context ctx
     * @return uuid
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getUUID(Context context) {
        String uuid = RandomUtil.generateAllChar(10);
        try {
            String androidId = "";
            String deviceId = "";
            if (context != null) {

                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null)
                    deviceId = telephonyManager.getDeviceId();

                androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }

            deviceId = TextUtils.isEmpty(deviceId) ? "" : deviceId;
            androidId = TextUtils.isEmpty(androidId) ? "" : androidId;

            if (!TextUtils.isEmpty(deviceId) || !TextUtils.isEmpty(androidId)) {
                uuid = deviceId + androidId;
            }

            uuid = MD5.encrypt(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid;

    }

    /**
     * @param context 上下文
     * @return int 版本号「Code」
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * @param context 上下文
     * @return String 版本号「Name」
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * @param context 上下文
     * @return App包名
     */
    public static String getPackageName(Context context) {
        String pkName = "";
        if (context != null && context.getPackageName() != null) {
            pkName = context.getPackageName();
        }
        return pkName;
    }

    /**
     * @param context 上下文
     * @return App名
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            appName = context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * @return 系统版本
     */
    public static String getOsVersion() {
        String osVersion = android.os.Build.VERSION.RELEASE;
        return TextUtils.isEmpty(osVersion) ? "" : osVersion;
    }

    /**
     * @return String 手机厂商信息
     */
    public static String getBrand() {
        String brand = android.os.Build.BRAND;
        return TextUtils.isEmpty(brand) ? "" : brand;
    }

    /**
     * @return String 设备型号信息
     */
    public static String getModel() {
        String model = android.os.Build.MODEL;
        return TextUtils.isEmpty(model) ? "" : model;
    }

    /**
     * @return 是否root
     */
    public static boolean isRootSystem() {
        File f;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (String kSuSearchPath : kSuSearchPaths) {
                f = new File(kSuSearchPath + "su");
                if (f.exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return sim序列号
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getSimSerialNumber(Context context) {
        String simSerialNumber = "null";
        try {
            if (context != null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (tm.getSimSerialNumber() != null)
                        simSerialNumber = tm.getSimSerialNumber();
            }
        } catch (Exception e) {
            simSerialNumber = "error";
            e.printStackTrace();
        }
        return simSerialNumber;
    }

    /**
     * @return 获取IMSI
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMSI(Context context) {
        String imsi = "null";
        try {
            if (context != null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (tm.getSubscriberId() != null)
                        imsi = tm.getSubscriberId();//获取手机IMSI号

            }
        } catch (Exception e) {
            imsi = "error";
            e.printStackTrace();
        }
        return imsi;
    }

    /**
     * @return 手机序列号
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceId(Context context) {
        String deviceId = "null";
        try {
            if (context != null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (!TextUtils.isEmpty(tm.getDeviceId()))
                        deviceId = tm.getDeviceId();
            }
        } catch (Exception e) {
            deviceId = "error";
            e.printStackTrace();
        }
        return deviceId;
    }

    /**
     * @return 获取手机号
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getNativePhoneNumber(Context context) {
        String phoneNum = "null";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null)
                if (telephonyManager.getLine1Number() != null)
                    phoneNum = telephonyManager.getLine1Number();
        } catch (Exception e) {
            phoneNum = "error";
            e.printStackTrace();
        }
        return phoneNum;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        String androidId;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            androidId = null == androidId ? "null" : androidId;
        } catch (Exception e) {
            androidId = "error";
            e.printStackTrace();
        }
        return androidId;
    }

    /**
     * @return 出厂ID
     */
    public static String getFactoryId(Context context) {
        @SuppressLint("HardwareIds")
        String factoryId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return factoryId;
    }

    /**
     * @return 获取 MetaData
     */
    public static String getAppMetaDataChannel(Context context, String metaName, String defaultValue) {
        try {
            //application标签下用getApplicationinfo，如果是activity下的用getActivityInfo
            return context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString(metaName, defaultValue);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
