package com.fiveoneofly.cgw.utils;

import android.os.Environment;

public class FileUtil {

    // 文件存储根目录
    public static String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CredCalendar/";
    // 图片存储目录
    public static String IMG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/CredCalendar/";

}
