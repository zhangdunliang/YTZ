package com.fiveoneofly.cgw.web.protocol;

import android.content.Context;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;

public class PluginManager {

    private static final String TAG = PluginManager.class.getSimpleName();
    private static final int SLOW_EXEC_WARNING_THRESHOLD = Debug.isDebuggerConnected() ? 60 : 16;// 执行超时警告界限

    private final Context mContext;
    private final OnBridgeListener mOnBridgeListener;

    private final LinkedHashMap<String, Plugin> mPluginMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, PluginEntry> mPluginEntryMap = new LinkedHashMap<>();

    PluginManager(Context context, Collection<PluginEntry> pluginEntries) {

        if (context instanceof OnBridgeListener) {
            this.mOnBridgeListener = (OnBridgeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBridgeListener");
        }

        this.mContext = context;

        if (mPluginMap.size() > 0) {
            mPluginMap.clear();
        }
        if (mPluginEntryMap.size() > 0) {
            mPluginEntryMap.clear();
        }

        for (PluginEntry entry : pluginEntries) {
            this.mPluginEntryMap.put(entry.pluginCode, entry);
        }

        for (PluginEntry entry : mPluginEntryMap.values()) {
            if (entry.pluginLoad) {
                getPlugin(entry.pluginCode);
            } else {
                mPluginMap.put(entry.pluginCode, null);
            }
        }

    }

    public void registerPlugin(String pluginCode, Plugin plugin) {
        mPluginMap.put(pluginCode, plugin);
    }

    public void realExecute(String id, final String pluginCode, final String data, ICallback responseCallback) throws Exception {
        Plugin plugin = getPlugin(pluginCode);
        if (plugin == null) {
            Log.i(TAG, "exec() call to unknown plugin: " + pluginCode);
            return;
        }

        long pluginStartTime = System.currentTimeMillis();

        plugin.handle(
                TextUtils.isEmpty(id) ? "123456789" : id,
                TextUtils.isEmpty(data) ? new JSONObject() : new JSONObject(data),
                responseCallback
        );

        long duration = System.currentTimeMillis() - pluginStartTime;

        if (duration > SLOW_EXEC_WARNING_THRESHOLD) {
            Log.w(TAG, "THREAD WARNING: exec() call to " + pluginCode + "." + data + " blocked the main thread for " + duration + "ms. Plugin should use CordovaInterface.getThreadPool().");
        }

    }


    private Plugin getPlugin(String pluginCode) {
        Plugin plugin = mPluginMap.get(pluginCode);
        if (plugin == null) {
            PluginEntry pluginEntry = mPluginEntryMap.get(pluginCode);
            if (pluginEntry == null) {
                return null;
            }
            plugin = instancePlugin(pluginEntry.pluginClass);
            if (plugin != null) {
                plugin.initialize(pluginCode, mContext, mOnBridgeListener);
                mPluginMap.put(pluginCode, plugin);
            }
        }
        return plugin;
    }

    private Plugin instancePlugin(String className) {
        Plugin plugin = null;
        try {
            Class<?> clazz = null;
            if ((className != null) && !("".equals(className))) {
                clazz = Class.forName(className);
            }
//            isAssignableFrom说明
//
//            a对象.isAssignableFrom(b对象)
//            a对象所对应类信息是b对象所对应的类信息的父类或者是父接口，简单理解即a是b的父类或接口
//            a对象所对应类信息与b对象所对应的类信息相同，简单理解即a和b为同一个类或同一个接口

//            //说明：Protocol是接口，DubboProtocol是Protocol的实现
//            Class protocolClass = Protocol.class ;
//            Class dubboProtocolClass = DubboProtocol.class ;
//
//
//            protocolClass.isAssignableFrom(dubboProtocolClass )) ;   //返回true
//            protocolClass.isAssignableFrom(protocolClass )) ;        //返回true
//            dubboProtocolClass.isAssignableFrom(protocolClass )) ;   //返回false
            if (clazz != null && Plugin.class.isAssignableFrom(clazz)) {
                plugin = (Plugin) clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plugin;
    }
}
