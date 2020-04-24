package com.fiveoneofly.cgw.app.basic;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.widget.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("Registered")
public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        ActivityStack.addActivity(this);
        initializeRootLayout(R.layout.activity_basic);
    }

    private void initializeRootLayout(@LayoutRes int layoutResID) {
        ViewGroup group = findViewById(android.R.id.content);                   // 得到窗口的根布局
        group.removeAllViews();                                                 // 先移除在根布局上的组件
        LayoutInflater.from(this).inflate(layoutResID, group, true);// group，true==>表示添加上去
    }


//    private FrameLayout navigateLayout; //导航布局
//    private FrameLayout contentLayout;  //内容布局
//
//    private RelativeLayout statusLayout;//状态布局
//    private StatusView statusView;      //状态控件

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // super.setContentView(layoutResID);// 切勿调用父类，会覆盖之前的布局

        FrameLayout navigateLayout = findViewById(R.id.basic_navigate);
        FrameLayout contentLayout = findViewById(R.id.basic_content);

        RelativeLayout statusLayout = findViewById(R.id.basic_status_layout);
//        StatusView statusView = findViewById(R.id.basic_status_view);
        statusLayout.setVisibility(View.GONE);


        //将子类的内容布局添加到基类的内容布局中
        LayoutInflater.from(this).inflate(layoutResID, contentLayout, true);

        //将子类实现的标题，添加到基类的标题布局当中
        if (onNavigateBar() != null) {
            navigateLayout.addView(onNavigateBar());
            navigateLayout.setVisibility(View.VISIBLE);
            setStatusBarColor();
        } else {
            navigateLayout.setVisibility(View.GONE);
        }

        ButterKnife.bind(this);
    }

    protected View onNavigateBar() {
        return null;
    }

    /**
     * 设置状态栏颜色
     * <p>
     * 实现了NavigateBar后默认支持，无需再设置
     */
    protected void setStatusBarColor() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE, true);
    }

    protected void setStatusBlueBar() {
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#2C99FF"), true);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getColor(color), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(color), true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ActivityStack.finishActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
