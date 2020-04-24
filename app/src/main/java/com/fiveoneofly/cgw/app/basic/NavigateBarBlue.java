package com.fiveoneofly.cgw.app.basic;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiveoneofly.cgw.R;

/**
 * Created by zhangdunliang on 2018/7/9.
 */

public class NavigateBarBlue {

    //            +-------------------------------+
    //            | 〈 标题                        |
    //            +-------------------------------+

    private static final float DEFAULT_TITLE_SIZE = 20;
    private static final String DEFAULT_TITLE_TEXT = "默认";
    private static final int DEFAULT_BACK_IMG = -1;

    private Context context;
    private int backImg;
    private String title;
    private float titleSize;
    private NavigateBar.OnNavigateBarListener onNavigateBarListener;
    private TextView navTitle;

    private View getNavigateBar(Builder builder) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_navigate_blue_bar, null);
        RelativeLayout navBackLayout = view.findViewById(R.id.nav_back_layout);
        ImageView navBackImg = view.findViewById(R.id.nav_back_img);
        navTitle = view.findViewById(R.id.nav_title);

        if (titleSize != DEFAULT_TITLE_SIZE)
            navTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);

        if (!DEFAULT_TITLE_TEXT.equals(title))
            navTitle.setText(title);

        if (backImg != DEFAULT_BACK_IMG)
            navBackImg.setBackgroundResource(backImg);

        if (null != onNavigateBarListener) {
            navBackLayout.setVisibility(View.VISIBLE);
            navBackLayout.setOnClickListener(leftClickListener);
        }
        return view;
    }

    // 不同接口对应的不同事件
    private View.OnClickListener leftClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null != onNavigateBarListener) {
                onNavigateBarListener.onBack();
            }
        }
    };

    public static class Builder {
        private String bTitle = DEFAULT_TITLE_TEXT;
        private float bTitleSize = DEFAULT_TITLE_SIZE;
        private int bBackImg = DEFAULT_BACK_IMG;
        private Context bContext;
        private NavigateBar.OnNavigateBarListener bOnNavigateBarListener;

        private Builder() {
        }

        public Builder(Context context) {
            this.bContext = context;
        }

        public NavigateBarBlue.Builder setTitle(String title) {
            this.bTitle = title;
            return this;
        }

        public NavigateBarBlue.Builder setTitleSize(float sp) {
            bTitleSize = sp;
            return this;
        }

        public NavigateBarBlue.Builder setBackImg(@LayoutRes int backImg) {
            this.bBackImg = backImg;
            return this;
        }

        public NavigateBarBlue.Builder setOnNavigateBarListener(NavigateBar.OnNavigateBarListener onNavigateBarListener) {
            this.bOnNavigateBarListener = onNavigateBarListener;
            return this;
        }

        // 临时适配WebView更新title
        public TextView getNavTextView() {
            if (bavigateBar != null)
                return bavigateBar.navTitle;
            return null;
        }

        // 临时适配WebView更新title
        NavigateBarBlue bavigateBar;

        public View getView() {

            bavigateBar = new NavigateBarBlue();

            //防止内存泄漏
            bavigateBar.context = this.bContext.getApplicationContext();
            bavigateBar.title = this.bTitle;
            bavigateBar.titleSize = this.bTitleSize;
            bavigateBar.backImg = this.bBackImg;
            bavigateBar.onNavigateBarListener = this.bOnNavigateBarListener;

            return bavigateBar.getNavigateBar(this);
        }

    }
}
