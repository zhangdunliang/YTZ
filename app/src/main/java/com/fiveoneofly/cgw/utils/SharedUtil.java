package com.fiveoneofly.cgw.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by xiaochangyou on 2018/6/25.
 * <p>
 * SharedPreferences
 */
public class SharedUtil {
    private static final String spName = "xyjqbsharedpreferences";
    private static final int spMode = Context.MODE_PRIVATE;

    public static void save(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
//        editor.commit();
        editor.apply();
    }

    //存储String
    public static void save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
//        editor.commit();
        editor.apply();
    }

    //存储int
    public static void save(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
//        editor.commit();
        editor.apply();
    }


    /**
     * @param context 上下文
     * @param key     键
     * @return 默认0
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        return sharedPreferences.getInt(key, 0);
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        return sharedPreferences.getString(key, "");
    }

    /**
     * @param context c
     * @param key     键
     * @return 默认false
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * @param context c
     * @param key     键
     * @return 默认true
     */
    public static boolean getBooleanT(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        return sharedPreferences.getBoolean(key, true);
    }

    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, spMode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
//        editor.commit();
        editor.apply();
    }
}
