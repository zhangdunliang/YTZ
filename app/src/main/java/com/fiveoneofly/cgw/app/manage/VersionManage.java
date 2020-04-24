package com.fiveoneofly.cgw.app.manage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.ActivityStack;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.VersionRequest;
import com.fiveoneofly.cgw.net.entity.bean.VersionResponse;
import com.fiveoneofly.cgw.utils.AndroidUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class VersionManage {

    private static final int DOWNLOAD_ING = 1;//下载中
    private static final int DOWNLOAD_FINISH = 2;//下载结束

    private String APP_NAME = "XYJQB";

    private int mProgress;// 记录进度条数量
    private boolean cancelUpdate = false;// 是否取消更新
    private ProgressBar mProgressBar;
    private Dialog mDownloadDialog;
    private Context mContext;
    private String mApkUrl;
    private String mPackageName;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_ING: // 正在下载
                    mProgressBar.setProgress(mProgress); // 设置进度条位置
                    break;
                case DOWNLOAD_FINISH:// 下载完成
                    installApk();// 安装文件
                    break;
                default:
                    break;
            }
        }
    };

    public VersionManage(Context context) {
        this.mContext = context;
    }

    public interface OnCheckVersionCallback {
        void onLatest();// 当前为最新版本

        void onUpdate(boolean isLowVersion, String desc, String url);// 检测到更新，isLowVersion：是否强制更新
    }

    public interface OnDialogCallback {
        void onWait();// 当前为最新版本
    }

    /**
     * 版本检测
     */
    public void checkVersion() {
        checkVersion(null);
    }

    /**
     * 版本检测
     */
    public void checkVersion(final OnCheckVersionCallback callback) {
        checkVersion(true, callback);
    }

    /**
     * 版本检测
     */
    public void checkVersion(final boolean showDialog, final OnCheckVersionCallback callback) {

        VersionRequest request = new VersionRequest();
        request.setAppType("1");
        request.setBundleId(AndroidUtil.getPackageName(mContext));
        new ApiRealCall(mContext, ServiceCode.VERSION_UPDATE).request(request, VersionResponse.class, new ApiCallback<VersionResponse>() {
            @Override
            public void onSuccess(VersionResponse response) {

                String appVersionCode = response.getAppVersionId();// 最新版本ID
                String appLowVersionCode = response.getLowVersionNo();// 最低支持版本ID
                String apkUrl = response.getAppUrl();// 下载地址
                String appDesc = response.getAppDesc();// 更新说明

                // 后台版本大于本地版本
                if (Integer.parseInt(appVersionCode) > AndroidUtil.getVersionCode(mContext)) {
                    // 本地版本低于后台最低版本[强制更新]
                    if (Integer.parseInt(appLowVersionCode) >= AndroidUtil.getVersionCode(mContext)) {
                        if (showDialog)
                            showUpdateDialog(appDesc, apkUrl, true, null);
                        if (callback != null)
                            callback.onUpdate(true, appDesc, apkUrl);
                    } else {
                        if (showDialog)
                            showUpdateDialog(appDesc, apkUrl, false, null);
                        if (callback != null)
                            callback.onUpdate(false, appDesc, apkUrl);
                    }
                } else {
                    if (callback != null)
                        callback.onLatest();
                }

            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();

                // 请求失败，放行
                if (callback != null)
                    callback.onLatest();
            }
        });

    }

    /**
     * 软件更新对话框
     */
    public void showUpdateDialog(String msg, String url, final boolean isLowVersion, final OnDialogCallback callback) {
        this.mApkUrl = url;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        // 更新
        builder.setPositiveButton(R.string.soft_update_update_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog(isLowVersion);
            }
        });
        if (!isLowVersion) {// 如果不低于最低版本
            // 先等等
            builder.setNegativeButton(R.string.soft_update_later_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != callback)
                        callback.onWait();
                    else
                        dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog(final boolean isLowVersion) {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        LayoutInflater inflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.layout_soft_update_progress, null);
        mProgressBar = view.findViewById(R.id.version_update_progress);
        builder.setView(view);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!isLowVersion) {
                    cancelUpdate = true; // 设置取消状态
                } else {
                    cancelUpdate = true;
                    try {
                        ActivityStack.AppExit(mContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        downloadApk(); // 下载文件
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        new downloadApkThread().start(); // 启动新线程下载软件
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    mPackageName = mContext.getPackageName();
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    String savePath = sdpath + mPackageName + "/";
                    URL url = new URL(mApkUrl);
                    // 创建连接
                    int length =0;
                    // 创建输入流
                    InputStream is = null;
                    if(mApkUrl.contains("https:")) {
                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                        TrustManager[] tm = {new NonAuthenticationX509TrustManager()};
                        SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, tm, new java.security.SecureRandom());
                        SSLSocketFactory ssf = sslContext.getSocketFactory();
                        conn.setSSLSocketFactory(ssf);
                        conn.connect();
                        length = conn.getContentLength();
                        // 创建输入流
                        is = conn.getInputStream();
                    }else{
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.connect();
                        // 获取文件大小
                        length = conn.getContentLength();
                        // 创建输入流
                        is = conn.getInputStream();
                    }

                    File file = new File(savePath);
                    if (!file.exists()) { // 判断文件目录是否存在
                        boolean mkdir = file.mkdir();
                    }
                    File apkFile = new File(savePath, APP_NAME);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];// 缓存
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        mProgress = (int) (((float) count / length) * 100);// 计算进度条位置
                        mHandler.sendEmptyMessage(DOWNLOAD_ING); // 更新进度
                        if (numread <= 0) {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH); // 下载完成
                            break;
                        }
                        fos.write(buf, 0, numread); // 写入文件
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            mDownloadDialog.dismiss(); // 取消下载对话框显示
        }
    }

    private void installApk() {
        File apkFile = new File(Environment.getExternalStorageDirectory() + "/" + mPackageName + "/" + APP_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 由于没有在Activity环境下启动Activity,设置下面的标签
        if (Build.VERSION.SDK_INT >= 24) { // 判读版本是否在7.0以上
            // 参数1:上下文
            // 参数2:Provider主机地址和配置文件中保持一致
            // 参数3:共享的文件
            Uri apkUri = FileProvider.getUriForFile(mContext, mPackageName + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }
}
