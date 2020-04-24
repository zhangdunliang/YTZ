package com.fiveoneofly.cgw.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.activity.LoginActivity;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.app.manage.VersionManage;
import com.fiveoneofly.cgw.app.widget.BadgeView;
import com.fiveoneofly.cgw.constants.CacheKey;
import com.fiveoneofly.cgw.third.event.KfxxEvent;
import com.fiveoneofly.cgw.third.getui.PushController;
import com.fiveoneofly.cgw.third.v5kf.V5kf;
import com.fiveoneofly.cgw.third.xinge.XingeReceiver;
import com.fiveoneofly.cgw.utils.AndroidUtil;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.LoginOutRequest;
import com.fiveoneofly.cgw.net.entity.bean.LoginOutResponse;
import com.fiveoneofly.cgw.utils.SharedUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MineFragment extends Fragment {

    /*
     * 禁止使用 getActivity、getContext
     * 防止 空指针 异常
     *
     * 虽然这种办法有导致内存泄露的风险「onDetach后，仍持有Activity的引用」
     * 但是总比空指针闪退好
     */
    private Activity mActivity;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.layout_lxkf)
    LinearLayout layoutLxkf;
    @BindView(R.id.layout_dqbb)
    RelativeLayout layoutDqbb;
    @BindView(R.id.lxkf_group)
    ImageView lxkfGroup;
    @BindView(R.id.lxkf_msg_num)
    BadgeView numView;
    @BindView(R.id.version_now)
    TextView versionNow;
    @BindView(R.id.switch_notice)
    Switch switchNotice;
    @BindView(R.id.btn_login_out)
    Button btnLoginOut;

    public MineFragment() {
    }

    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // 调用 JS 接口
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updNum(KfxxEvent event) {
        XingeReceiver.NUM = event.getNum();
        if (event.getNum() <= 0) {
            lxkfGroup.setVisibility(View.VISIBLE);
            numView.setVisibility(View.GONE);
        } else {
            lxkfGroup.setVisibility(View.GONE);
            numView.setVisibility(View.VISIBLE);
            numView.setBadgeCount(XingeReceiver.NUM);//设置提示消息数量
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);

        String version = "V" + AndroidUtil.getVersionName(mActivity);
        versionNow.setText(version);
        EventBus.getDefault().post(new KfxxEvent(XingeReceiver.NUM = 0));
        layoutLxkf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new KfxxEvent(XingeReceiver.NUM = 0));
                V5kf.config(mActivity);
                V5kf.start(mActivity);
            }
        });
        layoutDqbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new VersionManage(mActivity).checkVersion(new VersionManage.OnCheckVersionCallback() {
                    @Override
                    public void onLatest() {
                        Toast.makeText(mActivity, R.string.soft_update_latest_version, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUpdate(boolean isLowVersion, String desc, String url) {

                    }
                });
            }
        });
        switchNotice.setChecked(SharedUtil.getBooleanT(mActivity, CacheKey.SKEY_PUSH));
        switchNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedUtil.save(mActivity, CacheKey.SKEY_AUTO_UPDATE, b);
                if (b) {
                    PushController.startPush(mActivity);
                } else {
                    PushController.stopPush(mActivity);
                }
            }
        });

        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.login_out_confirm)
                        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LoginOutRequest request = new LoginOutRequest();
                                request.setCustId(UserManage.get(getActivity()).custId());
                                new ApiRealCall(mActivity, ServiceCode.LOGIN_OUT).request(request, LoginOutResponse.class, new ApiCallback<LoginOutResponse>() {
                                    @Override
                                    public void onSuccess(LoginOutResponse response) {

                                        UserManage.get(mActivity).loginOut();
                                        btnLoginOut.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), R.string.login_out_success, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }

        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        btnLoginOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
        EventBus.getDefault().unregister(this);
    }

}
