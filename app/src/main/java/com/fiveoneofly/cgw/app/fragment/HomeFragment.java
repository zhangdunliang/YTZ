package com.fiveoneofly.cgw.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.activity.Web1Activity;
import com.fiveoneofly.cgw.app.widget.VerticalSwitchTextView;
import com.fiveoneofly.cgw.constants.HtmlUrl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements HomeView {

    @BindView(R.id.home_refresh_Layout)
    SmartRefreshLayout homeRefreshLayout;
    @BindView(R.id.home_notice_view)
    VerticalSwitchTextView noticeView;
    //    @BindView(R.id.home_nc_withdrawal_layout)
//    CardView layoutNcWithdrawal;
//    @BindView(R.id.home_nc_withdrawal_btn)
//    Button btnNcWithdrawal;
    @BindView(R.id.home_credit_layout)
    CardView layoutCredit;
    @BindView(R.id.home_credit_btn)
    Button btnCredit;
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

    private HomePresenter homePresenter;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        homePresenter = new HomePresenter(mActivity, this);
        homePresenter.requestMessage();
        homeRefreshLayout.autoRefresh();// 自动刷新
        homeRefreshLayout.setDisableContentWhenRefresh(true);// 刷新时禁止操作视图
        homeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                homePresenter.requestBusinessStatus();
                homePresenter.requestMessage();
            }
        });
        return view;
    }

    @Override
    public void onMessage(List<String> list) {
        noticeView.setTextContent(list);
        noticeView.setCbInterface(new VerticalSwitchTextView.VerticalSwitchTextViewCbInterface() {
            @Override
            public void showNext(int index) {

            }

            @Override
            public void onItemClick(int index) {
            }
        });
    }

    @Override
    public void custBusinessStatus(boolean isOpenNoCard, boolean isOpenCredit) {
//        btnNcWithdrawal.setText(isOpenNoCard ? "立即取现" : "立即开通");
        btnCredit.setText(isOpenCredit ? "借款/还款" : "立即开通");

//        btnNcWithdrawal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Web1Activity.startWebActivityForUrl(mActivity, HtmlUrl.NO_CARD_URL);
//            }
//        });
        btnCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Web1Activity.startWebActivityForUrl(mActivity, HtmlUrl.CREDIT_URL);
            }
        });
    }


    @Override
    public void finishRefresh() {
        if (homeRefreshLayout.getState() == RefreshState.Refreshing)
            homeRefreshLayout.finishRefresh();
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
    }
}
