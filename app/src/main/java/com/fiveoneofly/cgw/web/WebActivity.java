package com.fiveoneofly.cgw.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.bridge.BridgeService;
import com.fiveoneofly.cgw.third.event.InvokeJsEvent;
import com.fiveoneofly.cgw.third.event.OpenCameraEvent;
import com.fiveoneofly.cgw.third.event.OpenFileManageEvent;
import com.fiveoneofly.cgw.third.event.OpenGalleryEvent;
import com.fiveoneofly.cgw.utils.MediaUtility;
import com.fiveoneofly.cgw.web.protocol.ICallback;
import com.fiveoneofly.cgw.web.protocol.OnBridgeListener;
import com.fiveoneofly.cgw.web.protocol.Plugin;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;

@SuppressLint("Registered")
public class WebActivity extends BasicActivity implements OnBridgeListener {

    // webveiw打开系统相机
    final int REQUEST_OPEN_SYS_CRMERA = 6001;
    // webview打开系统相册
    final int REQUEST_OPEN_SYS_GALLERY = 6002;
    // webview打开系统文件
    final int REQUEST_OPEN_SYS_FILE = 6003;

    private List<WebActivity> activityList = new ArrayList<>();

    private String mPicPath;

    protected UrlLoader mUrlLoader;
    protected String entryUrl;
    protected TextView navTextView;

    protected AWeb mAWeb;

    @BindView(R.id.web_content)
    ViewGroup mContentView;


    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        activityList.add(this);

        mAWeb = new AWeb.Builder(this).build();
        mUrlLoader = mAWeb.getUrlLoader();
        BridgeService bridgeService = new BridgeService(this);
        bridgeService.registerPlugin("103", new Plugin() {
            @Override
            public void handle(String id, JSONObject data, ICallback responseCallback) {
                mUrlLoader.loadUrl(entryUrl);
            }
        });
        mAWeb.getAWebView().addJavascriptInterface(bridgeService, "android");

        mAWeb.getAWebView().setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (StringUtil.isEmpty(title)) {
                    title = view.getUrl();
                }
                if (navTextView != null)
                    navTextView.setText(title.length() > 10 ? title.substring(0, 10) + "..." : title);// 设置标题
            }
        });

        mAWeb.getAWebView().setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mUrlLoader.invokeJS("", "000", null);
                super.onPageFinished(view, url);
            }
        });
        WebLayout webLayout = mAWeb.getWebLayout();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        webLayout.setLayoutParams(layoutParams);

        mContentView.addView(webLayout);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        if (mAWeb.getAWebView() != null && mAWeb.getAWebView().getUrl() != null) {
            if (mAWeb.getAWebView().getUrl().contains("http://www-1.fuiou.com:18670/mobile_pay/")
                    || mAWeb.getAWebView().getUrl().contains("https://mpay.fuiou.com:16128/")) {

                mUrlLoader.invokeJS("goback");// 富友支付页
            } else {// H5页面内
                mUrlLoader.invokeJS("backHistory");
            }
        } else {
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_OPEN_SYS_CRMERA) {// 打开系统相机
            if (null == mPicPath) {
                mAWeb.getFileChooser().returnFilePath("");
            } else {
                mAWeb.getFileChooser().returnFilePath(mPicPath);
            }
        }
        if (requestCode == REQUEST_OPEN_SYS_GALLERY
                || requestCode == REQUEST_OPEN_SYS_FILE) {// 打开图库||文件管理器
            if (null == data) {
                mAWeb.getFileChooser().returnFilePath("");
            } else {
                String path = MediaUtility.getPath(this, data.getData());
                mAWeb.getFileChooser().returnFilePath(path);
            }
        }
        if (null != mPlugin) {
            mPlugin.onActivityResult(requestCode, resultCode, data);
            mPlugin = null;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mUrlLoader.stopLoading();
        if (!isFinishing()) {
            for (int i = 0; i < activityList.size(); i++) {
                if (((WebActivity) activityList.get(i)).getClass().getName().equals(getClass().getName())) {
                    Activity activity = activityList.get(i);
                    if ((activity != null) && (!activity.isFinishing())) {
                        (activityList.get(i)).finish();
                    }
                }
            }
        }
        super.onDestroy();
    }

    // 调用 JS 接口
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void invokeJS(InvokeJsEvent event) {
        String id = event.getId();
        String code = event.getCode();
        String data = event.getData();
        mUrlLoader.invokeJS(id, code, data);
    }

    // 打开相机
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openCamera(OpenCameraEvent event) {
        File file = new File(
                Environment.getExternalStorageDirectory(),
                "img/" + String.valueOf(System.currentTimeMillis()) + ".jpg"
        );
        if (file.exists()) {
            boolean delete = file.delete();
        } else {
            boolean mkdirs = file.getParentFile().mkdirs();// /storage/sdcard1/1517975553331.jpg
        }
        mPicPath = file.getPath();
        Uri cameraUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)   //判读版本是否在7.0「24」以上
            cameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        else
            cameraUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, REQUEST_OPEN_SYS_CRMERA);
    }

    // 打开图库
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openGallery(OpenGalleryEvent event) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_OPEN_SYS_GALLERY);
    }

    // 打开文件管理器
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openFileManage(OpenFileManageEvent event) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_OPEN_SYS_FILE);
    }

    private Plugin mPlugin;

    @Override
    public void startActivityForResult(Plugin plugin, Intent intent, int requestCode) {
        setActivityResultCallback(plugin);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void setActivityResultCallback(Plugin plugin) {
        mPlugin = plugin;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public ExecutorService getThreadPool() {
        return null;
    }

    @Override
    public void loadUrl(String url) {
        mUrlLoader.loadUrl(url);
    }
}
