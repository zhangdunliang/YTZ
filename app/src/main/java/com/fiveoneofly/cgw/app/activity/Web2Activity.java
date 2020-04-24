package com.fiveoneofly.cgw.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.NavigateBar;
import com.fiveoneofly.cgw.third.event.RefreshWeb1Event;
import com.fiveoneofly.cgw.utils.DialogUtil;
import com.fiveoneofly.cgw.web.WebActivity;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

public class Web2Activity extends WebActivity {

    private static String PARAM_ENTRY_URL = "entryUrl";
    private static String PARAM_ENTRY_HTML = "entryHtml";
    private static String PARAM_BACK_HINT = "backHint";
    private static String PARAM_REFRESH = "refresh";
    private static String PARAM_TYPE = "type";

    private String paramBackHint;
    private boolean paramRefresh;
    private String paramType;

    private final String TYPE_UNIONPAY = "unionPay";


    public static void startWebActivityForUrl(Context context, String url, String backHint, boolean refresh, String type) {
        Intent intent = new Intent(context, Web2Activity.class);
        intent.putExtra(PARAM_ENTRY_URL, url);
        intent.putExtra(PARAM_BACK_HINT, backHint);
        intent.putExtra(PARAM_REFRESH, refresh);
        intent.putExtra(PARAM_TYPE, type);
        context.startActivity(intent);
    }

    public static void startWebActivityForHtml(Context context, String html, String backHint, boolean refresh, String type) {
        Intent intent = new Intent(context, Web2Activity.class);
        intent.putExtra(PARAM_ENTRY_HTML, html);
        intent.putExtra(PARAM_BACK_HINT, backHint);
        intent.putExtra(PARAM_REFRESH, refresh);
        intent.putExtra(PARAM_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        entryUrl = getIntent().getStringExtra(PARAM_ENTRY_URL);
        String entryHtml = getIntent().getStringExtra(PARAM_ENTRY_HTML);
        paramBackHint = getIntent().getStringExtra(PARAM_BACK_HINT);
        paramRefresh = getIntent().getBooleanExtra(PARAM_REFRESH, false);
        paramType = getIntent().getStringExtra(PARAM_TYPE);
        if (StringUtil.isNotEmpty(entryUrl)) {
            mUrlLoader.loadUrl(entryUrl);
        } else if (StringUtil.isNotEmpty(entryHtml)) {
            mUrlLoader.loadDataWithBaseURL(entryHtml);
        } else {
            finish();
        }

//        mAWeb.getAWebView().setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                if (paramType != null) {
//                    if (paramType.equals(TYPE_UNIONPAY)) {//仅在银联认证的时候插入js获取验证码
//                        if (url.contains("mcashier") && url.contains("verify.action") && url.contains("result")) {
//                            startTimer();
//                        }
//                    }
//                }
//                mUrlLoader.invokeJS("", "000", null);
//                super.onPageFinished(view, url);
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        finishWeb2();
    }

    @Override
    protected void onDestroy() {
        if (paramRefresh)
            EventBus.getDefault().post(new RefreshWeb1Event("", "101", null));
//        stopTimer();
        super.onDestroy();
    }

    @Override
    protected View onNavigateBar() {
        NavigateBar.Builder builder = new NavigateBar.Builder(this);
        View view = builder.setOnNavigateBarListener(new NavigateBar.OnNavigateBarListener() {
            @Override
            public void onBack() {
                finishWeb2();
            }
        }).getView();
        navTextView = builder.getNavTextView();
        navTextView.setText(R.string.loading);
        return view;
    }

    private void finishWeb2() {
        if (StringUtil.isNotEmpty(paramBackHint))
            DialogUtil.display(Web2Activity.this,
                    getString(R.string.web2_exit),
                    "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    },
                    "确认",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Web2Activity.this.finish();
                        }
                    });
        else
            finish();

    }

//    private Timer mTimer = null;
//    private TimerTask mTimerTask = null;
//    private int maxCount = 0;
//    private boolean isPause = true;
//
//    private void startTimer() {
//        if (mTimer == null) {
//            mTimer = new Timer();
//        }
//        if (mTimerTask == null) {
//            mTimerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    do {
//                        if (maxCount >= 10) {
//                            stopTimer();
//                            return;
//                        }
//                        if (isPause) {
//                            String verifycode_jsload = "javascript:window.android.verifycode(document.getElementById('pay_success').getElementsByClassName('money')[0].innerHTML)";
//                            mUrlLoader.loadUrl(verifycode_jsload);
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        maxCount++;
//                    } while (isPause);
//                }
//            };
//            if (mTimer != null && mTimerTask != null) {
//                mTimer.schedule(mTimerTask, 2000, 2000);
//            }
//        }
//    }
//
//    private void stopTimer() {
//        if (mTimer != null) {
//            mTimer.cancel();
//            mTimer = null;
//        }
//        if (mTimerTask != null) {
//            mTimerTask.cancel();
//            mTimerTask = null;
//        }
//        if (isPause) {
//            isPause = false;
//        }
//    }
}
