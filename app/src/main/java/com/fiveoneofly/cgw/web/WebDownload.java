package com.fiveoneofly.cgw.web;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.widget.Toast;

import java.util.HashMap;

/**
 * WebView下载监听
 */
public class WebDownload implements DownloadListener {

    public static final int DOWNLOAD_SYSTEM = 0;
    public static final int DOWNLOAD_BROWS = 1;

    private Context mContext;
    private int mDownloadMode = DOWNLOAD_SYSTEM;

    public WebDownload(Context context, int downloadMode) {
        this.mContext = context;
        this.mDownloadMode = downloadMode;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        if (mDownloadMode == DOWNLOAD_SYSTEM) {
            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
            systemDownload(mContext, url);
        } else if (mDownloadMode == DOWNLOAD_BROWS)
            browsDownload(mContext, url);
    }

    //跳转浏览器下载
    private void browsDownload(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    //使用系统下载服务下载
    private void systemDownload(Context context, String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));// 指定下载地址
        request.allowScanningByMediaScanner();// 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// 设置通知的显示类型，下载进行时和完成后显示通知

//        request.setTitle("This is title"); // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setDescription("This is description");// 设置通知栏的描述

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            request.setAllowedOverMetered(false);// 允许在计费流量下下载
        }
        request.setVisibleInDownloadsUi(false);// 允许该记录在下载管理界面可见//在通知栏显示
        request.setAllowedOverRoaming(true);// 允许漫游时下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);// 允许下载的网路类型
        // 设置下载文件保存的路径和文件名
//        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);//输出目录
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);//加入下载队列执行

        fileNames.put(downloadId, fileName);
        // 下载完成
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiver, intentFilter);
    }

    private static final HashMap<Long, String> fileNames = new HashMap<>();

    //系统下载服务完成通知
    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);

                    Intent downloadIntent = new Intent().setAction(Intent.ACTION_VIEW);
                    downloadIntent.setDataAndType(uri, getMimeType(fileNames.get(downloadId)));
                    if (Build.VERSION.SDK_INT >= 24) {
                        downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (false) {
                            downloadIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                    }

                    if (!(context instanceof Activity)) {
                        downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(downloadIntent);

                    Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
//                    Toast.makeText(context, "点击通知", Toast.LENGTH_SHORT).show();
                } else if (DownloadManager.ACTION_VIEW_DOWNLOADS.equals(intent.getAction())) {
//                    Toast.makeText(context, "不晓得的", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private String getMimeType(String fileName) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();

        String mimeType;
        if (end.equals("pdf")) {

            mimeType = "application/pdf";
        } else if (end.equals("m4a") ||
                end.equals("mp3") ||
                end.equals("mid") ||
                end.equals("xmf") ||
                end.equals("ogg") ||
                end.equals("wav")) {

            mimeType = "audio/*";
        } else if (end.equals("3gp") ||
                end.equals("mp4")) {

            mimeType = "video/*";
        } else if (end.equals("jpg") ||
                end.equals("gif") ||
                end.equals("png") ||
                end.equals("jpeg") ||
                end.equals("bmp")) {

            mimeType = "image/*";
        } else if (end.equals("apk")) {

            mimeType = "application/vnd.android.package-archive";
        } else if (end.equals("pptx") ||
                end.equals("ppt")) {

            mimeType = "application/vnd.ms-powerpoint";
        } else if (end.equals("docx") ||
                end.equals("doc")) {

            mimeType = "application/vnd.ms-word";
        } else if (end.equals("xlsx") ||
                end.equals("xls")) {

            mimeType = "application/vnd.ms-excel";
        } else {

            mimeType = "*/*";
        }
        return mimeType;
    }
}
