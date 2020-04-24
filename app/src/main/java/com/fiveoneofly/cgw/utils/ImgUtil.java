package com.fiveoneofly.cgw.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xiaochangyou on 2018/6/31.
 */
public class ImgUtil {

    /**
     * Base64 转 Bitmap
     *
     * @param base64
     * @return
     */
    public static Bitmap base64ToBitmap(String base64) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

//    public static File bitmap2File80(Bitmap bmp, String fileName) {
//        return bitmap2File(bmp, "", fileName, 80);
//    }
//
//    private static File bitmap2File(Bitmap bmp, String parent, String child, int quality) {
//        if (bmp == null || bmp.isRecycled())
//            return null;
//        File file = new File(parent, child);
//        try {
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (file.exists())
//            return file;
//        return null;
//    }

    /**
     * 保存 Bitmap 至文件[JPG]
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String fileName) {
        FileOutputStream out = null;
        try {
            File file = new File(FileUtil.IMG_DIRECTORY);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(FileUtil.IMG_DIRECTORY + fileName + ".jpg");
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
