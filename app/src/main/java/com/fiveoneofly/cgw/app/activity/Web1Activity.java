package com.fiveoneofly.cgw.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.NavigateBar;
import com.fiveoneofly.cgw.third.event.RefreshWeb1Event;
import com.fiveoneofly.cgw.web.WebActivity;
import com.fiveoneofly.cgw.utils.StringUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Web1Activity extends WebActivity {

    public static void startWebActivityForUrlByNav(Context context, String url) {
        mShowNav = true;
        startWebActivityForUrl(context, url);
    }

    public static void startWebActivityForUrlByNav(Context context, String url, String navTitle) {
        mNavTitle = navTitle;
        startWebActivityForUrlByNav(context, url);
    }

    public static void startWebActivityForUrl(Context context, String url) {
        Intent intent = new Intent(context, Web1Activity.class);
        intent.putExtra("entryUrl", url);
        context.startActivity(intent);
    }

    private static String mNavTitle;// 是否制定导航栏标题
    private static boolean mShowNav = false;// 是否显示导航栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStatusBarColor(R.color.h5_status);

        entryUrl = getIntent().getStringExtra("entryUrl");
        if (StringUtil.isNotEmpty(entryUrl)) {
            mUrlLoader.loadUrl(entryUrl);
        } else {
            // error
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShowNav = false;
        mNavTitle = null;
    }

    @Override
    protected View onNavigateBar() {
        View view = null;
        if (mShowNav) {
            NavigateBar.Builder builder = new NavigateBar.Builder(this);

            if (null != mNavTitle) {
                builder.setTitle(mNavTitle);
            }

            view = builder
                    .setOnNavigateBarListener(new NavigateBar.OnNavigateBarListener() {
                        @Override
                        public void onBack() {
                            Web1Activity.this.finish();
                        }
                    })
                    .getView();

            if (null == mNavTitle) {
                navTextView = builder.getNavTextView();
                navTextView.setText(R.string.loading);
            }
        }

        return view;
    }

    // 调用 JS 接口
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWeb1(RefreshWeb1Event event) {
        String id = event.getId();
        String code = event.getCode();
        String data = event.getData();
        mUrlLoader.invokeJS(id, code, data);
    }

}
