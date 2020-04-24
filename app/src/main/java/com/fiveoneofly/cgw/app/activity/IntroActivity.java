package com.fiveoneofly.cgw.app.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.fiveoneofly.cgw.app.basic.BasicActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import com.fiveoneofly.cgw.R;


public class IntroActivity extends BasicActivity {

    @BindView(R.id.intro_pager)
    ViewPager pager;
    @BindView(R.id.intro_btn)
    Button btn;

    private List<View> mViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //          尺寸             分辨率
        // mdpi     320*480         HVGA
        // hdpi     480*800         WVGA FWVGA QHD
        // xhdpi    720*1280        720P
        // xxhdpi   1080*1920       1080P
        final int[] images = new int[]{R.mipmap.ic_intro_1, R.mipmap.ic_intro_2};
        // 按钮文字
        final String btnText = "开始体验";

        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntroActivity.this.finish();
            }
        });

        initialize(images);
    }

    private void initialize(final int[] images) {

        // 全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // 添加数据
        int len = images.length;
        for (int i = 0; i < len; i++) {
            // 新建ImageView添加资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(images[i]);

            mViewList.add(imageView);
        }

        // 添加适配器，注意：必须在资源准备好之后
        pager.setAdapter(new IntroPagerAdapter());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == mViewList.size() - 1) {
                    btn.setVisibility(View.VISIBLE);
                } else {
                    btn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class IntroPagerAdapter extends PagerAdapter {

        /**
         * @return 页面的个数
         */
        @Override
        public int getCount() {
            if (mViewList != null) {
                return mViewList.size();
            }
            return 0;
        }

        /**
         * 判断对象是否生成界面
         *
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 初始化position位置的界面
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }
}
