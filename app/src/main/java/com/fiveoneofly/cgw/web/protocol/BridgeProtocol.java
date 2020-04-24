package com.fiveoneofly.cgw.web.protocol;

import android.content.Context;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class BridgeProtocol {

    PluginManager pluginManager;

    public BridgeProtocol(Context context) throws IOException, XmlPullParserException {
        ConfigXmlParser configXmlParser = new ConfigXmlParser(context);
        ArrayList<PluginEntry> plugins = configXmlParser.getPlugins();
        pluginManager = new PluginManager(context, plugins);
    }

    public void registerPlugin(String pluginCode, Plugin plugin) {
        pluginManager.registerPlugin(pluginCode, plugin);
    }

    public void execute(String code, String id, String data, ICallback callback) throws Exception {
        pluginManager.realExecute(id, code, data, callback);
    }
}
