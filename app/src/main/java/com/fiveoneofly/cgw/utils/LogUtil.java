package com.fiveoneofly.cgw.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Created by xiaochangyou on 2018/7/2.
 */
public class LogUtil {

    /**
     * 自定义策略，临时解决 studio 3.1.3 打印问题
     */
    static class DynamicTagStrategy implements LogStrategy {

        private String[] prefix = {". ", " ."};
        private int index = 0;

        @Override
        public void log(int priority, @Nullable String tag, @NonNull String message) {
            index = index ^ 1;
            Log.println(priority, prefix[index] + tag, message);
        }
    }

    static {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true[是否选择显示线程信息，默认为true]
//                .methodCount(0)         // (Optional) How many method line to show. Default 2[方法数显示多少行，默认2行]
                .methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5[隐藏方法内部调用到偏移量，默认5]
                .logStrategy(new DynamicTagStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat[打印日志的策略，默认LogCat]
                .tag("CC_Log")   // (Optional) Global tag for every log. Default PRETTY_LOGGER[自定义TAG全部标签，默认PRETTY_LOGGER]
                .build();
//        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    public static void d(@Nullable Object object) {
        Logger.d(object);
    }

    public static void d(@Nullable String tag, @Nullable Object object) {
        Logger.t(tag).d(object);
    }

    public static void json(@Nullable String json) {
        Logger.json(json);
    }

    public static void json(@Nullable String tag, @Nullable String json) {
        Logger.t(tag).json(json);
    }

    public static void e(@Nullable Throwable throwable) {
        e(throwable, "", "message");
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        Logger.e(throwable, message, args);
    }

}
