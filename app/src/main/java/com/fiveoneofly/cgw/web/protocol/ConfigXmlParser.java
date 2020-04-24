package com.fiveoneofly.cgw.web.protocol;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

class ConfigXmlParser {

    private boolean insideFeature = false;

    private String pluginCode = "";
    private String pluginClass = "";
    private boolean pluginLoad = false;

    private ArrayList<PluginEntry> plugins = new ArrayList<>();

    public ArrayList<PluginEntry> getPlugins() {
        return plugins;
    }

    ConfigXmlParser(Context context) throws IOException, XmlPullParserException {
        final String xmlName = "bridge_config";
        final String defType = "xml";
        // 首先检查config.xml的类名称空间
        int id = context.getResources().getIdentifier(xmlName, defType, context.getClass().getPackage().getName());
        if (id == 0) {
            // 如果找不到config.xml，我们将从AndroidManifest.xml中查找名称空间
            id = context.getResources().getIdentifier(xmlName, defType, context.getPackageName());
            if (id == 0) {
                return;
            }
        }

        parse(context.getResources().getXml(id));
    }

    private void parse(XmlPullParser xml) throws IOException, XmlPullParserException {
        int eventType = -1;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                handleStartTag(xml);
            } else if (eventType == XmlPullParser.END_TAG) {
                handleEndTag(xml);
            }
            eventType = xml.next();
        }
    }

    private void handleStartTag(XmlPullParser xml) {

        String strNode = xml.getName();

        if (strNode.equals("feature")) {
            pluginCode = xml.getAttributeValue(null, "name");
            insideFeature = true;
        } else if (insideFeature && strNode.equals("param")) {

            String paramType = xml.getAttributeValue(null, "name");

            if (paramType.equals("android-package")) {
                pluginClass = xml.getAttributeValue(null, "value");
            } else if (paramType.equals("load")) {
                pluginLoad = "true".equals(xml.getAttributeValue(null, "value"));
            }
        }
    }

    private void handleEndTag(XmlPullParser xml) {

        String strNode = xml.getName();

        if (strNode.equals("feature")) {

            plugins.add(new PluginEntry(pluginCode, pluginClass, pluginLoad));

            pluginCode = "";
            pluginClass = "";
            pluginLoad = false;

            insideFeature = false;
        }
    }
}
