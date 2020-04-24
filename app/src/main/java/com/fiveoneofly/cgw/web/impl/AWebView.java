package com.fiveoneofly.cgw.web.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveoneofly.cgw.utils.DensityUtil;
import com.fiveoneofly.cgw.utils.FileUtil;
import com.fiveoneofly.cgw.utils.ImgUtil;
import com.fiveoneofly.cgw.web.indicator.IndicatorController;
import com.fiveoneofly.cgw.web.indicator.LineIndicator;
import com.fiveoneofly.cgw.web.indicator.WebIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by xiaochangyou on 2018/6/13.
 */

public class AWebView extends WebView {

    public static final String WEB_ERROR_URL = "file:///android_asset/yxjr_credit_load_error.html";

    private int mTouchX, mTouchY;

    public AWebView(Context context) {
        super(context);
        settings();
        setClick();
    }

    /*
     * 如下错误方式，导致WebView无法弹出软键盘；
     * 主要原因 defStyleAttr 不能传 0 ；
     * https://yq.aliyun.com/ziliao/154840
     */
//    public AWebView(Context context) {
//        this(context, null, 0);
//    }
//
//    public AWebView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public AWebView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        settings();
//    }


    @Override
    public void setWebViewClient(WebViewClient client) {
        AWebViewClient aWebViewClient;
        if (client instanceof AWebViewClient) {
            aWebViewClient = (AWebViewClient) client;
        } else {
            aWebViewClient = new AWebViewClient();
            aWebViewClient.setWebViewClientProxy(client);
        }

        super.setWebViewClient(aWebViewClient);
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {

        WebIndicator webIndicator = new LineIndicator(this.getContext());
        WebIndicator.Controller webIndicatorController = new IndicatorController(webIndicator);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) webIndicator.layoutParams();
        layoutParams.gravity = Gravity.TOP;

        this.addView((View) webIndicator, layoutParams);

        AWebChromeClient aWebChromeClient;
        if (client instanceof AWebChromeClient) {
            aWebChromeClient = (AWebChromeClient) client;
        } else {
            aWebChromeClient = new AWebChromeClient();
            aWebChromeClient.setWebChromeClientProxy(client);
        }
        aWebChromeClient.setIndicator(webIndicatorController);

        super.setWebChromeClient(aWebChromeClient);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void settings() {

        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不使用缓存||LOAD_CACHE_ELSE_NETWORK优先使用缓存
        settings.setAllowFileAccess(true); // 设置可以访问文件
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        settings.setSaveFormData(false);
        settings.setSupportZoom(false); // 不支持缩放
        settings.setDomStorageEnabled(true);//配合前端使用Dom Storage（Web Storage）存储机制
        settings.setUseWideViewPort(true);//viewport

        setHorizontalScrollBarEnabled(false);// 水平不显示
        setVerticalScrollBarEnabled(false); // 垂直不显示

    }

    private void setClick() {

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = (int) event.getRawX();
                mTouchY = (int) event.getRawY();
                return false;
            }
        });

        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                HitTestResult result = ((AWebView) v).getHitTestResult();
                if (null != result && result.getType() == HitTestResult.IMAGE_TYPE) {
                    /**
                     * web_long_click 使用java实现
                     */
                    ViewGroup view = new RelativeLayout(AWebView.this.getContext());
                    TextView txt_save_img = new TextView(AWebView.this.getContext());
                    txt_save_img.setText("保存图片");
                    txt_save_img.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    txt_save_img.setTextColor(Color.WHITE);
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    view.addView(txt_save_img, rlp);

                    // 处理长按图片的菜单项
                    final PopupWindow popup = new PopupWindow(AWebView.this.getContext());
                    popup.setWidth(DensityUtil.dp2Px(AWebView.this.getContext(), 100));
                    popup.setHeight(DensityUtil.dp2Px(AWebView.this.getContext(), 40));
                    popup.setContentView(view);
                    popup.setOutsideTouchable(true);
                    popup.setFocusable(true);
                    popup.showAtLocation(v, Gravity.TOP | Gravity.START, mTouchX, mTouchY);

                    final String img = result.getExtra();
                    txt_save_img.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (img.startsWith("http://") || img.startsWith("https://")) {

                                // Android 4.0以后要使用线程来访问网络
                                @SuppressLint("StaticFieldLeak")
                                AsyncTask<String, Void, String> saveTask = new AsyncTask<String, Void, String>() {

                                    @Override
                                    protected String doInBackground(String... strings) {
                                        String result;
                                        try {
                                            File file = new File(FileUtil.IMG_DIRECTORY);
                                            if (!file.exists())
                                                file.mkdirs();
                                            String type = img.substring(img.lastIndexOf("."));// 图片类型
                                            file = new File(FileUtil.IMG_DIRECTORY + new Date().getTime() + type);
                                            InputStream inputStream = null;
                                            HttpURLConnection conn = (HttpURLConnection) new URL(img).openConnection();
                                            conn.setRequestMethod("GET");
                                            conn.setConnectTimeout(20000);
                                            if (conn.getResponseCode() == 200)
                                                inputStream = conn.getInputStream();
                                            byte[] buffer = new byte[4096];
                                            int len;
                                            FileOutputStream outStream = new FileOutputStream(file);
                                            while ((len = inputStream.read(buffer)) != -1) {
                                                outStream.write(buffer, 0, len);
                                            }
                                            outStream.close();
                                            result = "已保存至：" + file.getAbsolutePath();
                                        } catch (Exception e) {
                                            result = "保存失败:" + e.getLocalizedMessage();
                                        }
                                        return result;
                                    }

                                    @Override
                                    protected void onPostExecute(String result) {
                                        Toast.makeText(AWebView.this.getContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                };
                                saveTask.execute();
                            } else if (img.contains("base64")) {
                                int indexOf = img.indexOf("base64,");
                                String imgBase64 = img.substring(indexOf + 7);
                                Bitmap bitmap = ImgUtil.base64ToBitmap(imgBase64);
                                String savePath = ImgUtil.saveBitmap(bitmap, String.valueOf(new Date().getTime()));
                                Toast.makeText(
                                        AWebView.this.getContext(),
                                        savePath == null ? "保存失败" : "已保存至:" + savePath,
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(AWebView.this.getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                            popup.dismiss();
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }
}
