package com.fiveoneofly.cgw.calm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


import com.fiveoneofly.cgw.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class ItemAppList {

    public JSONArray getData(Context context) {
        JSONArray appList = null;
        try {//SM-A8000 app.publicSourceDir为null，暂无设备，try carch掉整个逻辑避免闪退
            PackageManager pm = context.getPackageManager();
            // 查询所有已经安装的应用程序
            List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序
            appList = new JSONArray();
            for (ApplicationInfo app : listAppcations) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {//第三方APP
                    CharSequence appName = app.loadLabel(pm);//名字
                    String appPkgName = app.packageName;//包名
                    //SM-A8000 app.publicSourceDir为null，暂无设备，try carch掉整个逻辑避免闪退
                    @SuppressLint("SimpleDateFormat")
                    String installTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new File(app.publicSourceDir).lastModified());//安装时间
                    JSONObject apps = new JSONObject();
                    apps.put("appName", appName);
                    apps.put("appPkgName", appPkgName);
                    apps.put("installTime", installTime);
                    appList.put(apps);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Calm---AppList", " query end ====> AppList");
        return appList;
    }

}
