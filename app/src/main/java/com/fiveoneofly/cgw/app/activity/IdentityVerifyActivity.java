package com.fiveoneofly.cgw.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.app.basic.NavigateBar;
import com.fiveoneofly.cgw.third.megvii.AuthUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdentityVerifyActivity extends BasicActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verify);
        ButterKnife.bind(this);
    }

    @Override
    protected View onNavigateBar() {
        return new NavigateBar.Builder(this)
                .setTitle("身份信息认证")
                .setOnNavigateBarListener(new NavigateBar.OnNavigateBarListener() {
                    @Override
                    public void onBack() {
                        IdentityVerifyActivity.this.finish();
                    }
                })
                .getView();
    }


    @OnClick(R.id.begin_face)
    public void onViewClicked() {
        AuthUtil.netFaceAuth(this, new AuthUtil.FaceAuthListener() {
            @Override
            public void authSucces() {
                startActivity(new Intent(IdentityVerifyActivity.this, MegviiFace2Activity.class));
            }
        });
    }
}
