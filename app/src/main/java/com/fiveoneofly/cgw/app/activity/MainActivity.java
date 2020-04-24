package com.fiveoneofly.cgw.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveoneofly.cgw.app.basic.ActivityStack;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.app.fragment.HomeFragment;
import com.fiveoneofly.cgw.app.fragment.MineFragment;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.third.getui.PushController;
import com.fiveoneofly.cgw.utils.AndroidUtil;

import butterknife.BindView;

import com.fiveoneofly.cgw.R;

public class MainActivity extends BasicActivity {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.main_notice_layout)
    LinearLayout mainNoticeLayout;
    @BindView(R.id.main_notice_icon)
    ImageView mainNoticeIcon;
    @BindView(R.id.main_notice_no_read)
    ImageView mainNoticeNoRead;

    private long mExitTime;//退出时的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBlueBar();

        final ViewPager viewPager = findViewById(R.id.content_viewpager);
        final BottomNavigationView navigation = findViewById(R.id.bottom_navigation);

        // 默认选中第一项
        navigation.setSelectedItemId(navigation.getMenu().getItem(0).getItemId());
        mainTitle.setText(navigation.getMenu().getItem(0).getTitle());

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mainTitle.setText(item.getTitle());
                if (item.getItemId() == R.id.navigation_home) {
                    noticeStatus();
                } else {
                    mainNoticeLayout.setVisibility(View.INVISIBLE);
                }
                viewPager.setCurrentItem(Fragments.from(item.getItemId()).ordinal());
                return true;
            }
        });

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return Fragments.values().length;
            }

            @Override
            public Fragment getItem(int position) {
                return Fragments.values()[position].fragment();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                navigation.setSelectedItemId(Fragments.values()[position].menuId);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出" + AndroidUtil.getAppName(this), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                try {
                    ActivityStack.AppExit(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noticeStatus();
        PushController.initialize(this);
    }

    @Override
    protected void onDestroy() {
        Fragments.onDestroy();
        super.onDestroy();
    }

    private void noticeStatus() {
        mainNoticeLayout.setVisibility(View.VISIBLE);
        mainNoticeNoRead.setVisibility(View.INVISIBLE);
        if (UserManage.get(this).isLogin()) {// 已登录 && 有未读
            if (Integer.parseInt(UserManage.get(this).smsNum()) > 0)
                mainNoticeNoRead.setVisibility(View.VISIBLE);
            else {
                mainNoticeNoRead.setVisibility(View.GONE);
            }
        }
        mainNoticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
            }
        });
    }

    private enum Fragments {
        home(R.id.navigation_home, HomeFragment.class),
        mine(R.id.navigation_mine, MineFragment.class);

        private Fragment fragment;
        private final int menuId;
        private final Class<? extends Fragment> clazz;

        Fragments(@IdRes int menuId, Class<? extends Fragment> clazz) {
            this.menuId = menuId;
            this.clazz = clazz;
        }

        public Fragment fragment() {
            if (fragment == null) {
                if (clazz.equals(HomeFragment.class)) {
                    fragment = HomeFragment.newInstance("aaa", "bbb");
                } else if (clazz.equals(MineFragment.class)) {
                    fragment = MineFragment.newInstance("ccc", "ddd");
                } else {
                    try {
                        fragment = clazz.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fragment = new Fragment();
                    }
                }
            }
            return fragment;
        }

        public static Fragments from(int itemId) {
            for (Fragments fragment : values()) {
                if (fragment.menuId == itemId) {
                    return fragment;
                }
            }
            return home;
        }

        public static void onDestroy() {
            for (Fragments fragment : values()) {
                fragment.fragment = null;
            }
        }
    }

}
