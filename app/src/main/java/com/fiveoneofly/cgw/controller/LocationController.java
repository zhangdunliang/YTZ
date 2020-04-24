package com.fiveoneofly.cgw.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;

import com.fiveoneofly.cgw.utils.SharedUtil;
import com.yxjr.credit.constants.SharedKey;

import java.math.BigDecimal;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by xiaochangyou on 2018/4/18.
 * <p>
 * Location控制
 */
public class LocationController {

    private Context mContext;
    private LocationManager mLocationManager;
    private static final int GPS_MIN_TIME = 5 * 60 * 1000;// GPS最小刷新时间「5分钟」
    private static final int GPS_MIN_DISTANCE = 0;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 设备位置发生改变时，执行这里的代码；经:location.getLatitude() 纬:location.getLongitude()
            saveLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public LocationController(Context context) {
        this.mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(ACCESS_FINE_LOCATION)
    public void register() {
        Location location = null;
        try {
            location = getLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveLocation(location);
    }

    public void unRegister() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    private void saveLocation(Location location) {
        if (location != null) {
            BigDecimal bLatitude = new BigDecimal(location.getLatitude());
            BigDecimal blongitude = new BigDecimal(location.getLongitude());

            String sLatitude = String.valueOf(bLatitude.setScale(6, BigDecimal.ROUND_HALF_UP));
            String sLongitude = String.valueOf(blongitude.setScale(6, BigDecimal.ROUND_HALF_UP));

//            LogUtil.d("Latitude:" + sLatitude + " Longitude:" + sLongitude);

            SharedUtil.save(mContext, SharedKey.LATITUDE, sLatitude);// 存储当前的经度,并保留小数点后六位
            SharedUtil.save(mContext, SharedKey.LONGITUDE, sLongitude);// 存储当前的纬度,并保留小数点后六位
        } // else("Exception：location is null ! ! !");
//        LogUtil.d("location == null");
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(ACCESS_FINE_LOCATION)
    private Location getLocation() {
        Location location = null;
        String provider = null;
        List<String> providerList = mLocationManager.getProviders(true);// 获取可用的位置提供器

        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {// 优先使用
            provider = LocationManager.NETWORK_PROVIDER; // AGPS
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {// 使用
            provider = LocationManager.GPS_PROVIDER;// GPS
        } // else 没有可用的位置提供器

        if (provider != null) {
            mLocationManager.requestLocationUpdates(
                    provider,
                    GPS_MIN_TIME,// GPS_MIN_TIME 更新下当前位置
                    GPS_MIN_DISTANCE,
                    mLocationListener
            );
            location = mLocationManager.getLastKnownLocation(provider);
            // 未处理异常，交给上级 or 使用者处理
            // SecurityException
        }
        return location;
    }
}
