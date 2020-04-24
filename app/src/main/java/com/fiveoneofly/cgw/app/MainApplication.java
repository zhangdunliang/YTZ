package com.fiveoneofly.cgw.app;

import android.app.Application;
import android.os.Build;
import android.os.StatFs;

import com.fiveoneofly.cgw.third.picasso.PicassoSslSocketFactory;
import com.fiveoneofly.cgw.third.tongdun.Tongdun;
import com.fiveoneofly.cgw.third.v5kf.V5kf;
import com.fiveoneofly.cgw.third.xinge.Xinge;
import com.moxie.client.manager.MoxieSDK;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;

import javax.net.ssl.SSLContext;

import cn.tongdun.android.shell.exception.FMException;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

public class MainApplication extends Application {

    /**
     * AES密钥、token
     * 1、进入APP时默认空
     * 2、请求token接口成功后复制
     * 3、为空时必须先获得token值后才能继续下一个接口请求
     */
    public static String token;

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashHandler.getInstance().init(this);
        leadCanary();//内存泄露分析工具
        picasso();//图片加载框架初始化
        V5kf.initialize(this);//智能客服
        Xinge.initialize(this);//信鸽推送初始化
        umengInit();//友盟初始化
    }

    private void leadCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        // 同盾初始化
        try {
            Tongdun.getInstance().initialize(this);
        } catch (FMException e) {
            e.printStackTrace();
        }

        //初始化MoxieSDK
        MoxieSDK.init(this);
    }

    private void picasso() {
        // android5.0之前https无法获取图片
        // 原因Picasso没有设置证书问题
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso picasso = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
            int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

            // 缓存目录[来源 com.squareup.picasso.Utils ]
            File cache = new File(this.getApplicationContext().getCacheDir(), "picasso-cache");
            if (!cache.exists()) {
                //noinspection ResultOfMethodCallIgnored
                cache.mkdirs();
            }

            // 缓存大小 [来源 com.squareup.picasso.Utils ]
            long size = MIN_DISK_CACHE_SIZE;
            try {
                StatFs statFs = new StatFs(cache.getAbsolutePath());
                //noinspection deprecation
                long blockCount = SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockCount() : statFs.getBlockCountLong();
                //noinspection deprecation
                long blockSize = SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockSize() : statFs.getBlockSizeLong();
                long available = blockCount * blockSize;
                // Target 2% of the total space.
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }
            // Bound inside min/max size for disk cache.
            long maxSize = Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);

            OkHttpClient client = null;
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, null, null);
                client = new OkHttpClient().newBuilder()
                        .sslSocketFactory(new PicassoSslSocketFactory(sc.getSocketFactory()))
                        .cache(new Cache(cache, maxSize))
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (client != null) {
                builder.downloader(new OkHttp3Downloader(client));
                picasso = builder.build();
            }
        } else {
            builder.downloader(new OkHttp3Downloader(this));
            picasso = builder.build();

        }
        if (null != picasso) {
            picasso.setIndicatorsEnabled(true);// 标识图片来源
            Picasso.setSingletonInstance(picasso);
        }
    }

    private void umengInit(){
        //友盟初始化操作
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        //场景类型设置接口 一般选用普通统计场景
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //友盟日志加密
        UMConfigure.setEncryptEnabled(true);
    }

}
