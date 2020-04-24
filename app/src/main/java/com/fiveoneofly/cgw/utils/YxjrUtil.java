package com.fiveoneofly.cgw.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.fiveoneofly.cgw.controller.SensorController;
import com.yxjr.credit.constants.SharedKey;

import java.io.File;

/**
 * Created by xiaochangyou on 2018/6/4.
 */

public class YxjrUtil {

    /**
     * @return String 传感器数据组装
     */
    public static String getSensorsInfo(Context context) {

        String orientation = SharedUtil.getString(context, SharedKey.SENSOR_ORIENTATION);
        if (StringUtil.isEmpty(orientation)) {
            orientation = "||";
        }
        String accelerometer = SharedUtil.getString(context, SharedKey.SENSOR_ACCELEROMETER);
        if (StringUtil.isEmpty(accelerometer)) {
            accelerometer = "||";
        }
        String gravity = SharedUtil.getString(context, SharedKey.SENSOR_GRAVITY);
        if (StringUtil.isEmpty(gravity)) {
            gravity = "||";
        }
        String gyroscopes = SharedUtil.getString(context, SharedKey.SENSOR_GYROSCOPES);
        if (StringUtil.isEmpty(gyroscopes)) {
            gyroscopes = "||";
        }
        SensorController.getInstance(context).refresh();
        //顺序：方向传感器、加速传感器、重力传感器、陀螺仪传感器
        return orientation + "," + accelerometer + "," + gravity + "," + gyroscopes;
    }

    /**
     * 手机存储容量
     * 机身存储容量|机身可用存储容量|SD卡存储容量|SD卡可用存储容量
     */
    public static String getPhoneStorageSize(Context context) {
        String romTotalSize = getRomTotalSize(context);
        String romFreeSize = getRomFreeSize(context);
        String sdTotalSize = getSDTotalSize(context);
        String sdFreeSize = getSDFreeSize(context);
        return romTotalSize + "|" + romFreeSize + "|" + sdTotalSize + "|" + sdFreeSize;
    }

    /**
     * 获得SD卡存储容量总大小
     */
    private static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        @SuppressWarnings("deprecation")
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation")
        long totalBlocks = stat.getBlockCount();
        return formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡可用存储容量
     */
    private static String getSDFreeSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        @SuppressWarnings("deprecation")
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation")
        long availableBlocks = stat.getAvailableBlocks();
        return formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身存储容量大小
     */
    private static String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        @SuppressWarnings("deprecation")
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation")
        long totalBlocks = stat.getBlockCount();
        return formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用存储容量
     */
    private static String getRomFreeSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        @SuppressWarnings("deprecation")
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation")
        long availableBlocks = stat.getAvailableBlocks();
        return formatFileSize(context, blockSize * availableBlocks);
    }

    @SuppressLint("DefaultLocale")
    private static String formatFileSize(Context context, long number) {
        if (context == null) {
            return "";
        }
        float result = number;
        //		String suffix = "byteShort";
        if (result > 900) {//kb
            //			suffix = "kilobyteShort";
            result = result / 1024;
        }
        if (result > 900) {//mb
            //			suffix = "megabyteShort";
            result = result / 1024;
        }
        //		if (result > 900) {//gb
        //			suffix = "gigabyteShort";
        //			result = result / 1024;
        //		}
        //		if (result > 900) {/t
        //			suffix = "terabyteShort";
        //			result = result / 1024;
        //		}
        //		if (result > 900) {//
        //			suffix = "petabyteShort";
        //			result = result / 1024;
        //		}
        return String.format("%.4f", result);
    }
}
