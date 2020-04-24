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


public class NavigateBar {

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
    private OnNavigateBarListener onNavigateBarListener;
    private TextView navTitle;

    public View getNavigateBar() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_navigate_bar, null);
        RelativeLayout navBackLayout = view.findViewById(R.id.nav_back_layout);
        ImageView navBackImg = view.findViewById(R.id.nav_back_img);
        navTitle = view.findViewById(R.id.nav_title);

        if (titleSize != DEFAULT_TITLE_SIZE)
            navTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);

        if (!DEFAULT_TITLE_TEXT.equals(title))
            navTitle.setText(title);

        if (backImg != DEFAULT_BACK_IMG)
            navBackImg.setBackgroundResource(backImg);

        navBackLayout.setOnClickListener(leftClickListener);
        return view;
    }

    //不同接口对应的不同事件
    View.OnClickListener leftClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onNavigateBarListener != null) {
                onNavigateBarListener.onBack();
            }
        }
    };

    public interface OnNavigateBarListener {
        void onBack();
    }

    public static class Builder {
        private String bTitle = DEFAULT_TITLE_TEXT;
        private float bTitleSize = DEFAULT_TITLE_SIZE;
        private int bBackImg = DEFAULT_BACK_IMG;
        private Context bContext;
        private OnNavigateBarListener bOnNavigateBarListener;

        public Builder(Context context) {
            this.bContext = context;
        }

        public Builder setTitle(String title) {
            this.bTitle = title;
            return this;
        }

        public Builder setTitleSize(float sp) {
            bTitleSize = sp;
            return this;
        }

        public Builder setBackImg(@LayoutRes int backImg) {
            this.bBackImg = backImg;
            return this;
        }

        public Builder setOnNavigateBarListener(OnNavigateBarListener onNavigateBarListener) {
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
        NavigateBar bavigateBar;

        public View getView() {

            bavigateBar = new NavigateBar();

            bavigateBar.context = this.bContext.getApplicationContext();
            bavigateBar.title = this.bTitle;
            bavigateBar.titleSize = this.bTitleSize;
            bavigateBar.backImg = this.bBackImg;
            bavigateBar.onNavigateBarListener = this.bOnNavigateBarListener;

            return bavigateBar.getNavigateBar();
        }


    }
}
