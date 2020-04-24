package com.fiveoneofly.cgw.third.megvii.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;

import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by binghezhouke on 15-8-12.
 */
public class Util {

    /**
     * @return File
     * @作者:xiaochangyou
     * @创建时间:2016-8-1 下午3:12:30
     * @描述:TODO[获取保存图片的目录]
     */
    private static File getAlbumDir() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "temp");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * @param path void
     * @作者:xiaochangyou
     * @创建时间:2016-8-1 下午3:12:10
     * @描述:TODO[根据路径删除图片]
     */
    public static void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Camera.Size getNearestRatioSize(Camera.Parameters para, final int screenWidth, final int screenHeight) {
        List<Camera.Size> supportedSize = para.getSupportedPreviewSizes();
        for (Camera.Size tmp : supportedSize) {
            if (tmp.width == 1280 && tmp.height == 720) {
                return tmp;
            }
        }
        Collections.sort(supportedSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int diff1 = (((int) ((1000 * (Math.abs(lhs.width / (float) lhs.height - screenWidth / (float) screenHeight))))) << 16) - lhs.width;
                int diff2 = (((int) (1000 * (Math.abs(rhs.width / (float) rhs.height - screenWidth / (float) screenHeight)))) << 16) - rhs.width;

                return diff1 - diff2;
            }
        });

        return supportedSize.get(0);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return simpleDateFormat.format(new Date());
    }

    public static void closeStreamSilently(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {

            }
        }
    }

    // bitmap转btye
    public static byte[] bmp2byteArr(Bitmap bmp) {
        if (bmp == null || bmp.isRecycled())
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        Util.closeStreamSilently(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // byte转Fil类型
    public static File byte2File(byte[] buf, String fileName) {
        if (buf == null)
            return null;

        Bitmap bitmap = null;
        if (buf.length != 0) {
            bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
        } else {
            return null;
        }
        return bmp2File(bitmap, fileName);
    }

    public static String saveJPGFile(Context mContext, byte[] data, String jpgFileName) {
        if (data == null)
            return null;
        File mediaStorageDir = mContext.getExternalFilesDir("livenessDemo_image");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mediaStorageDir + "/" + jpgFileName);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
            return mediaStorageDir.getAbsolutePath() + "/" + jpgFileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据byte数组生成文件
     * bytes生成文件用到的byte数组
     */
    public static File createFileWithByte(byte[] bytes, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);// 创建File对象，其中包含文件所在的目录以及文件的命名
        FileOutputStream outputStream = null;// 创建FileOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;// 创建BufferedOutputStream对象
        try {
            if (file.exists()) {// 如果文件存在则删除
                file.delete();
            }
            file.createNewFile();// 在文件系统中根据路径创建一个新的空文件
            outputStream = new FileOutputStream(file);// 获取FileOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream); // 获取BufferedOutputStream对象
            bufferedOutputStream.write(bytes);// 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.flush();// 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
        } catch (Exception e) {
            e.printStackTrace();// 打印异常信息
        } finally {
            if (outputStream != null) { // 关闭创建的流对象
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * =====================
     * ===> 压缩比率 20% <===
     * =====================
     * <p>
     * Bitmap.compress说明
     * ==> 如quality=80
     * ==> 保留原有80%品质，即压缩20%
     * ==> 但实际Bitmap大小未改变仍会有内存危险，实际为文件存储大小压缩
     *
     * @param bmp
     * @param fileName
     * @return
     */
    public static File bmp2File(Bitmap bmp, String fileName) {
        if (bmp == null || bmp.isRecycled())
            return null;
        File file = new File(getAlbumDir(), fileName);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists())
            return file;
        return null;
    }

    public static File bmp2File100(Bitmap bmp, String fileName) {
        if (bmp == null || bmp.isRecycled())
            return null;
        File file = new File(getAlbumDir(), fileName);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists())
            return file;
        return null;
    }

    // 身份中OCR 错误类型转换
    @SuppressWarnings("incomplete-switch")
    public static String errorType2HumanStr(IDCardQualityResult.IDCardFailedType type, IDCardAttr.IDCardSide side) {
        String result = null;
        switch (type) {
            case QUALITY_FAILED_TYPE_NOIDCARD:
                result = "未检测到身份证";
                break;
            case QUALITY_FAILED_TYPE_BLUR:
                result = "模糊";
                break;
            case QUALITY_FAILED_TYPE_BRIGHTNESSTOOHIGH:
                result = "太亮";
                break;
            case QUALITY_FAILED_TYPE_BRIGHTNESSTOOLOW:
                result = "太暗";
                break;
            case QUALITY_FAILED_TYPE_OUTSIDETHEROI:
                result = "请将身份证摆到提示框内";
                // result = "请将身份证与提示框对齐";
                break;
            case QUALITY_FAILED_TYPE_SIZETOOLARGE:
                result = "请离远些拍摄";
                break;
            case QUALITY_FAILED_TYPE_SIZETOOSMALL:
                result = "请靠近些拍摄";
                break;
            case QUALITY_FAILED_TYPE_SPECULARHIGHLIGHT:
                result = "请调整拍摄位置，以去除光斑";
                break;
            case QUALITY_FAILED_TYPE_TILT:
                result = "请将身份证摆正";
                break;
            case QUALITY_FAILED_TYPE_SHADOW:
                result = "有阴影";
                break;
            case QUALITY_FAILED_TYPE_WRONGSIDE:
                if (side == IDCardAttr.IDCardSide.IDCARD_SIDE_BACK)
                    result = "请翻到反面";
                else {
                    result = "请翻到正面";
                }
                break;
        }
        return result;
    }

}
