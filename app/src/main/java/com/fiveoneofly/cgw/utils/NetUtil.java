package com.fiveoneofly.cgw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * Created by xiaochangyou on 2018/6/13.
 */

public class NetUtil {

    /**
     * @param context 上下文
     * @return boolean true:已联网;false:未联网
     */
    public static boolean networked(Context context) {
        boolean isAvailable = false;
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = null;
            if (manager != null) {
                info = manager.getActiveNetworkInfo();
            }
            if (info != null) {
                isAvailable = info.isAvailable();
            }
        }
        return isAvailable;
    }


    /**
     * @param context 上下文
     * @return wifi环境 int 「1：wifi; 2:no wifi」
     */
    public static boolean isWifi(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        return info != null && info.isAvailable();
    }

    /**
     * @return IP
     */
    public static String getIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return wifi mac 移动网络下有为null
     */
    public static String getMac() {
        String mac = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String str = "";
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mac;
    }
}
