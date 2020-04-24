package com.yxjr.credit.fragment.deprecated;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

//import com.yxjr.credit.utils.ToastUtil;

import java.io.File;
import java.util.UUID;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-15 下午5:23:32
 * @描述: 打开相机及相关
 */
@Deprecated
public class YxBmpFactory {

    private Activity mActivity = null;
    private File tempCameraFile;

    public YxBmpFactory(Activity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    /**
     * @param requestCode
     * @作者:xiaochangyou
     * @创建时间:2016-7-15 下午5:23:05
     * @描述: 打开相机，并保存图片至图片的存储目录下，已temp_随机数.jpg命名
     */
    public void openCamera(int requestCode) {
        try {
            tempCameraFile = new File(YxPictureUtil.getAlbumDir(), "temp_" + UUID.randomUUID().toString() + ".jpg");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("camerasensortype", 2);// 调用前置摄像头
            intent.putExtra("autofocus", true);// 自动对焦
            intent.putExtra("fullScreen", false);// 全屏
            intent.putExtra("showActionIcons", false);
            //			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile));// 指定调用相机拍照后照片的储存路径
            ContentValues contentValues = new ContentValues(1);//Android7.0 crash- FileUriExposedException解决方案，7.0文件系统权限的变化导致
            contentValues.put(MediaStore.Images.Media.DATA, tempCameraFile.getAbsolutePath());
            Uri uri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);// 指定调用相机拍照后照片的储存路径
            mActivity.startActivityForResult(intent, requestCode);
        } catch (SecurityException se) {
//			YxLog.e("without permission android.permission.CAMERA");
//            ToastUtil.showToast(mActivity, "请打开相机权限!");
        } catch (Exception e) {
            e.printStackTrace();
//			YxLog.e("CAMERA Exception:" + e);
//            ToastUtil.showToast(mActivity, "相机异常!");
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @return String 存储路径
     * @作者:xiaochangyou
     * @创建时间:2016-7-15 下午5:23:56
     * @描述:返回临时文件的路径
     */
    public String getCameraFilePath(int requestCode, int resultCode, Intent data) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return tempCameraFile.getAbsolutePath();
        } else {
//            YxLog.e("no SD card,无法存储照片！");
//            ToastUtil.showToast(mActivity, "未找到存储卡,无法存储照片！");
        }
        return null;
    }

}
