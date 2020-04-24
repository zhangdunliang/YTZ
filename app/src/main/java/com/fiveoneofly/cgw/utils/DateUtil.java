package com.fiveoneofly.cgw.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * @param data       Date类型的时间
     * @param formatType 格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     */
    @SuppressLint("SimpleDateFormat")
    public static String date2String(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * @param currentTime 转换的long类型的时间
     * @param formatType  转换的string类型的时间格式
     */
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = long2Date(currentTime, formatType); // long类型转成Date类型
        return date2String(date, formatType); // date类型转成String
    }

    /**
     * strTime的时间格式必须要与formatType的时间格式相同
     *
     * @param strTime    要转换的string类型的时间
     * @param formatType 转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     */
    public static Date string2Date(String strTime, String formatType)
            throws ParseException {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        return formatter.parse(strTime);
    }

    /**
     * @param currentTime 要转换的long类型的时间
     * @param formatType  要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     */
    public static Date long2Date(long currentTime, String formatType) throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = date2String(dateOld, formatType); // 把date类型的时间转换为string
        return string2Date(sDateTime, formatType); // 把String类型转换为Date类型
    }

    /**
     * strTime的时间格式和formatType的时间格式必须相同
     *
     * @param strTime    要转换的String类型的时间
     * @param formatType 时间格式
     */
    public static long string2Long(String strTime, String formatType) throws ParseException {
        Date date = string2Date(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            return date2Long(date); // date类型转成long类型
        }
    }

    /**
     * @return date要转换的date类型的时间
     */
    public static long date2Long(Date date) {
        return date.getTime();
    }

    /**
     * 「String」yyyy-MM-dd HH:mm:ss 转 long
     */
    public static long date2Long(String str) {
        long time = 0;
        if (null == str || "".equals(str))
            return time;
        String replace0 = str.replace("-", "");
        String replace1 = replace0.replace(" ", "");
        String replace2 = replace1.replace(":", "");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 24小时制
        Date date = null;

        try {
            date = format.parse(replace2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null != date) {
            time = date.getTime();
        }
        return time;
    }

    /**
     * 「long」yyyy-MM-dd HH:mm:ss 转 date
     */
    public static String long2Date(long date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(date));
    }

    /**
     * @return 当前时间 年月日时分秒 yyyyMMddhhmmss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = format.format(new Date());
        return currentTime;
    }
}
