package com.fiveoneofly.cgw.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveoneofly.cgw.R;
import com.fiveoneofly.cgw.app.basic.BasicActivity;
import com.fiveoneofly.cgw.app.basic.NavigateBar;
import com.fiveoneofly.cgw.app.manage.UserManage;
import com.fiveoneofly.cgw.constants.NoticeType;
import com.fiveoneofly.cgw.net.ApiRealCall;
import com.fiveoneofly.cgw.net.ServiceCode;
import com.fiveoneofly.cgw.net.api.ApiCallback;
import com.fiveoneofly.cgw.net.entity.bean.LoginInResponse;
import com.fiveoneofly.cgw.net.entity.bean.Notice;
import com.fiveoneofly.cgw.net.entity.bean.NoticeListRequest;
import com.fiveoneofly.cgw.net.entity.bean.NoticeListResponse;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class NoticeListActivity extends BasicActivity {

    @BindView(R.id.notice_list_refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.notice_list_view)
    RecyclerView listView;

    public static void startForType(Context context, String noticeType) {
        Intent intent = new Intent(context, NoticeListActivity.class);
        intent.putExtra("noticeType", noticeType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        noticeType = getIntent().getStringExtra("noticeType");

        noticeNewsAdapter = new NoticeNewsAdapter();
        listView.setLayoutManager(new LinearLayoutManager(this));
        //设置adapter
        listView.setAdapter(noticeNewsAdapter);
        //设置Item增加、移除动画
        listView.setItemAnimator(new DefaultItemAnimator());

        refresh.autoRefresh();// 自动刷新
        refresh.setDisableContentWhenRefresh(true);// 刷新时禁止操作视图
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                currentPage = 1;
                totalCount = 0;
                notices = new ArrayList<>();
                loadNotice(false);
            }
        });
        refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (totalCount > pageCount && (totalCount - pageCount * currentPage) > 0) {
                    currentPage++;
                    loadNotice(true);
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                }
            }
        });
    }

    @Override
    protected View onNavigateBar() {
        return new NavigateBar.Builder(this)
                .setTitle(getString(R.string.notice_msg))
                .setOnNavigateBarListener(new NavigateBar.OnNavigateBarListener() {
                    @Override
                    public void onBack() {
                        NoticeListActivity.this.finish();
                    }
                })
                .getView();
    }

    private NoticeNewsAdapter noticeNewsAdapter;
    private List<Notice> notices = new ArrayList<>();
    private String noticeType;
    private int currentPage = 1;
    private int totalCount = 0;
    private int pageCount = 10;

    private void loadNotice(final boolean loadMore) {
        NoticeListRequest request = new NoticeListRequest();
        request.setCustId(UserManage.get(NoticeListActivity.this).custId());
        request.setSmsType(noticeType);
        request.setPageNo(String.valueOf(currentPage));
        request.setPageSize(String.valueOf(pageCount));
        new ApiRealCall(NoticeListActivity.this, ServiceCode.NOTICE_LIST).request(request, NoticeListResponse.class, new ApiCallback<NoticeListResponse>() {
            @Override
            public void onSuccess(NoticeListResponse response) {
                totalCount = Integer.parseInt(response.getMap().getTotalCnt());

                List<Notice> responseList = response.getMap().getSmsList();
                switch (noticeType) {
                    case NoticeType.MESSAGE:
                        read();
                        break;
                    case NoticeType.NOTICE:
                        break;
                    case NoticeType.INFO:
                        break;
                    default:
                        break;
                }
                notices.addAll(responseList);
                noticeNewsAdapter.notifyDataSetChanged();

                if (loadMore) {
                    refresh.finishLoadMore();
                } else {
                    refresh.finishRefresh();
                }
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                Toast.makeText(NoticeListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                if (loadMore) {
                    refresh.finishLoadMore();
                } else {
                    refresh.finishRefresh();
                }
            }
        });
    }

    private void read() {
        Map<String, String> request = new HashMap<>();
        request.put("custId", UserManage.get(this).custId());
        new ApiRealCall(NoticeListActivity.this, ServiceCode.NOTICE_READ).request(request, String.class, new ApiCallback<String>() {
            @Override
            public void onSuccess(String s) {
                // 更新用户信息缓存
                UserManage userManage = UserManage.get(NoticeListActivity.this);
                LoginInResponse user = userManage.getUser();
                user.setSmsNum("0");
                userManage.loginIn(user);
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
            }
        });
    }

    class NoticeNewsAdapter extends RecyclerView.Adapter<NoticeNewsAdapter.NoticeNewsViewHolder> {


        @NonNull
        @Override
        public NoticeNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NoticeNewsViewHolder(LayoutInflater.from(NoticeListActivity.this).inflate(R.layout.layout_notice_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NoticeNewsViewHolder holder, int position) {
            holder.itemTitle.setText(notices.get(position).getSmsTitle());
            holder.itemDate.setText(notices.get(position).getSmsSendTime());
            holder.itemSummary.setText(notices.get(position).getSmsContent());
        }

        @Override
        public int getItemCount() {
            return notices.size();
        }

        class NoticeNewsViewHolder extends RecyclerView.ViewHolder {
            TextView itemTitle;
            TextView itemDate;
            TextView itemSummary;

            NoticeNewsViewHolder(View view) {
                super(view);
                itemTitle = view.findViewById(R.id.notice_news_title);
                itemDate = view.findViewById(R.id.notice_news_date);
                itemSummary = view.findViewById(R.id.notice_news_summary);
            }
        }
    }

}
