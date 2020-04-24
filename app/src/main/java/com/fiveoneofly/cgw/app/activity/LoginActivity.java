package com.fiveoneofly.cgw.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.app.basic.NavigateBarBlue;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.app.widget.EditText;
import com.fiveoneofly.cgw.constants.CacheKey;
import com.fiveoneofly.cgw.constants.HtmlUrl;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.LoginInRequest;
import com.fiveoneofly.cgw.net.entity.bean.LoginInResponse;
import com.fiveoneofly.cgw.net.entity.bean.LoginSmsRequest;
import com.fiveoneofly.cgw.net.entity.bean.LoginSmsResponse;
import com.fiveoneofly.cgw.third.getui.PushController;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.utils.SharedUtil;
import com.fiveoneofly.cgw.utils.StringUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BasicActivity {
    @BindView(R.id.login_slogan)
    ImageView slogan;//标语
    @BindView(R.id.login_edit_phone)
    EditText phoneEdit;//手机号码
    @BindView(R.id.login_edit_verifiy_code)
    EditText verifiyCodeEdit;//短信验证码
    @BindView(R.id.login_clear)
    ImageView phoneClear;//清除手机号码
    @BindView(R.id.login_get_verifiy_code)
    TextView verifiyCodeGet;//获取短信验证
    @BindView(R.id.login_btn)
    Button loginBtn;//登录/注册
    @BindView(R.id.login_agreement_layout)
    LinearLayout agreementLayout;//协议区域
    @BindView(R.id.login_agreement)
    TextView agreement;//协议

    private SmsCountDownTimer mSmsCountDownTimer;
    private String seq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarColor(R.color.colorPrimary);

        SpannableString msp = new SpannableString(getString(R.string.login_agreement_register));
        msp.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreement.setText(msp);
        phoneEdit.setText(SharedUtil.getString(this, CacheKey.NOW_PHONE));
        phoneEdit.setOnClearListener(new EditText.OnClearListener() {
            @Override
            public void onClearVisibility(boolean visibility) {
                if (visibility) {
                    if (phoneClear.getVisibility() != View.VISIBLE)
                        phoneClear.setVisibility(View.VISIBLE);
                } else {
                    if (phoneClear.getVisibility() == View.VISIBLE)
                        phoneClear.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        PushController.initialize(this);
    }

    @Override
    protected View onNavigateBar() {
        setStatusBarColor();
        return new NavigateBarBlue.Builder(this)
                .setTitle(getString(R.string.login_or_register))
                .setTitleSize(16)
                .getView();
    }

    @OnClick({
            R.id.login_clear,
            R.id.login_get_verifiy_code,
            R.id.login_btn,
            R.id.login_agreement
    })
    public void onViweClick(View view) {
        switch (view.getId()) {
            case R.id.login_clear:// 清除手机号栏位
                phoneEdit.setText("");
                break;
            case R.id.login_get_verifiy_code:// 获取验证码
                if (StringUtil.isEmpty(phoneEdit.getText())) {
                    Toast.makeText(this, R.string.login_enter_phone, Toast.LENGTH_SHORT).show();
                } else if (phoneEdit.getText().length() < 11 || !phoneEdit.getText().toString().startsWith("1")) {
                    Toast.makeText(this, R.string.login_enter_phone_error, Toast.LENGTH_SHORT).show();
                } else {
                    LoginSmsRequest smsRequest = new LoginSmsRequest();
                    smsRequest.setPhoneNo(phoneEdit.getText().toString());
                    new ApiRealCall(this, ServiceCode.LOGIN_SMS).request(smsRequest, LoginSmsResponse.class, new ApiCallback<LoginSmsResponse>() {
                        @Override
                        public void onSuccess(LoginSmsResponse response) {
                            seq = response.getSeq();
                            mSmsCountDownTimer = new SmsCountDownTimer(60000, 1000);
                            mSmsCountDownTimer.start();
                            Toast.makeText(LoginActivity.this, R.string.v_code_get_success, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.login_btn:// 登录

                if (StringUtil.isEmpty(phoneEdit.getText())) {
                    Toast.makeText(this, R.string.login_enter_phone, Toast.LENGTH_SHORT).show();
                } else if (phoneEdit.getText().length() < 11 || !phoneEdit.getText().toString().startsWith("1")) {
                    Toast.makeText(this, R.string.login_enter_phone_error, Toast.LENGTH_SHORT).show();
                } else if (StringUtil.isEmpty(verifiyCodeEdit.getText())) {
                    Toast.makeText(this, R.string.login_verifiy_code, Toast.LENGTH_SHORT).show();
                } else if (StringUtil.isEmpty(seq)) {
                    Toast.makeText(this, R.string.v_code_get_fail, Toast.LENGTH_SHORT).show();
                } else {
                    LoginInRequest loginInRequest = new LoginInRequest();
                    loginInRequest.setPhoneNo(phoneEdit.getText().toString());
                    loginInRequest.setVerifyCode(verifiyCodeEdit.getText().toString());
                    loginInRequest.setSourceName(AndroidUtil.getAppMetaDataChannel(LoginActivity.this,"RELEASE_CHANNEL",""));
                    String cid = SharedUtil.getString(LoginActivity.this, CacheKey.GT_CID);
                    LoginInRequest.setCid(cid);
                    loginInRequest.setSeq(seq);
                    new ApiRealCall(this, ServiceCode.LOGIN_IN).request(loginInRequest, LoginInResponse.class, new ApiCallback<LoginInResponse>() {
                        @Override
                        public void onSuccess(LoginInResponse response) {
                            UserManage.get(LoginActivity.this).loginIn(response);
                            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                            if (UserManage.get(LoginActivity.this).identityStatus()) {
                                startMainActivity();
                            } else {// 未实名
                                startIdCardVerifyActivity();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.login_agreement:// 协议
                Web1Activity.startWebActivityForUrlByNav(this, HtmlUrl.AGREEMENT_USER_LOGIN);

                break;
            default:
                break;
        }
    }

    private void startIdCardVerifyActivity() {
        startActivity(new Intent(this, IdCardVerifyActivity.class));
        this.finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSmsCountDownTimer != null)
            mSmsCountDownTimer.cancel();
    }

    class SmsCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以「 毫秒 」为单位倒计时的总数
         *                          例如 millisInFuture = 1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick()
         *                          例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         */
        SmsCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            verifiyCodeGet.setClickable(true);
            verifiyCodeGet.setText(R.string.v_code_get_again);
        }

        public void onTick(long millisUntilFinished) {
            verifiyCodeGet.setClickable(false);
            String millis = millisUntilFinished / 1000 + " S";
            verifiyCodeGet.setText(millis);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击完返回键，执行的动作
            moveTaskToBack(false);
            return true;
        }
        return true;
    }
}
