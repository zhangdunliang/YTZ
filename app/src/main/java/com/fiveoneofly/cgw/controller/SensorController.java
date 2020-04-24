package com.fiveoneofly.cgw.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.fiveoneofly.cgw.utils.LogUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.yxjr.credit.constants.SharedKey;

import java.text.DecimalFormat;


/**
 * Created by xiaochangyou on 2018/4/18.
 * <p>
 * Sensor管理
 */
public class SensorController {

    private SensorManager mSensorManager;
    private Context mContext;
    private boolean isRefresh = false;

    private SensorController(Context context) {
        this.mContext = context;
        this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @SuppressLint("StaticFieldLeak")
    private static SensorController mInstance = null;

    public static SensorController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SensorController.class) {
                if (mInstance == null) {
                    mInstance = new SensorController(context);
                }
            }
        }
        return mInstance;
    }

    public void refresh() {
        SharedUtil.save(mContext, SharedKey.SENSOR_ORIENTATION, "");
        SharedUtil.save(mContext, SharedKey.SENSOR_ACCELEROMETER, "");
        SharedUtil.save(mContext, SharedKey.SENSOR_GRAVITY, "");
        SharedUtil.save(mContext, SharedKey.SENSOR_GYROSCOPES, "");
        isRefresh = true;
        register();
    }

    private void register() {
        Sensor orientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);// 方向传感器(水平传感器)
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 加速度传感器
        Sensor gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);// 重力传感器
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);// 陀螺仪传感器
        if (orientationSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } //else 设备不支持加速度传感器、方向传感器

        if (accelerometerSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } //else 设备不支持加速度传感器

        if (gravitySensor != null) {
            mSensorManager.registerListener(mSensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } //else 设备不支持重力传感器

        if (gyroscopeSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } //else 设备不支持加速度传感器、陀螺仪传感器
    }

    private void unRegister() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);// 解除传感器注册
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        private String tempOrientation = null;
        private String tempAccelerometer = null;
        private String tempGravity = null;
        private String tempGyroscopes = null;
        private DecimalFormat sDecimalFormat = new DecimalFormat("0.000000");// 小数不足2位,以0补足.

        private String formatSensor(float[] values) {
            LogUtil.d("formatSensor===>", sDecimalFormat.format(values[0]) + "|" + sDecimalFormat.format(values[1]) + "|" + sDecimalFormat.format(values[2]));

            return sDecimalFormat.format(values[0]) + "|"
                    + sDecimalFormat.format(values[1]) + "|"
                    + sDecimalFormat.format(values[2]);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == null || !isRefresh) {
                return;
            }
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    tempOrientation = formatSensor(event.values);
                case Sensor.TYPE_ACCELEROMETER:
                    tempAccelerometer = formatSensor(event.values);
                case Sensor.TYPE_GRAVITY:
                    tempGravity = formatSensor(event.values);
                case Sensor.TYPE_GYROSCOPE:
                    tempGyroscopes = formatSensor(event.values);
                default:
                    break;
            }
            if (tempOrientation != null) {
                SharedUtil.save(mContext, SharedKey.SENSOR_ORIENTATION, tempOrientation);
                tempOrientation = null;
            }
            if (tempAccelerometer != null) {
                SharedUtil.save(mContext, SharedKey.SENSOR_ACCELEROMETER, tempAccelerometer);
                tempAccelerometer = null;
            }
            if (tempGravity != null) {
                SharedUtil.save(mContext, SharedKey.SENSOR_GRAVITY, tempGravity);
                tempGravity = null;
            }
            if (tempGyroscopes != null) {
                SharedUtil.save(mContext, SharedKey.SENSOR_GYROSCOPES, tempGyroscopes);
                tempGyroscopes = null;
            }
            isRefresh = false;
            unRegister();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

}
