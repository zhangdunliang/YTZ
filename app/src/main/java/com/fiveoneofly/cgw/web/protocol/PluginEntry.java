package com.fiveoneofly.cgw.web.protocol;

public class PluginEntry {

    public final String pluginCode;
    public final String pluginClass;
    public final boolean pluginLoad;

    PluginEntry(String pluginCode, String pluginClass, boolean pluginLoad) {
        this.pluginCode = pluginCode;
        this.pluginClass = pluginClass;
        this.pluginLoad = pluginLoad;
    }
}
