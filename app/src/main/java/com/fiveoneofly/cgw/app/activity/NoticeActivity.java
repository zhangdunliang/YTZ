package com.fiveoneofly.cgw.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.fiveoneofly.cgw.net.entity.bean.Notice;
import com.fiveoneofly.cgw.net.entity.bean.NoticeTypeRequest;
import com.fiveoneofly.cgw.net.entity.bean.NoticeTypeResponse;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NoticeActivity extends BasicActivity {

    @BindView(R.id.notice_refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.notice_list)
    RecyclerView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        refresh.autoRefresh();// 自动刷新
        refresh.setDisableContentWhenRefresh(true);// 刷新时禁止操作视图
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {

                NoticeTypeRequest request = new NoticeTypeRequest();
                request.setCustId(UserManage.get(NoticeActivity.this).custId());
                new ApiRealCall(NoticeActivity.this, ServiceCode.NOTICE_TYPE).request(request, NoticeTypeResponse.class, new ApiCallback<NoticeTypeResponse>() {
                    @Override
                    public void onSuccess(NoticeTypeResponse response) {

                        List<Notice> notices = response.getMap().getSmsTypeList();

                        NoticeAdapter noticeAdapter = new NoticeAdapter(notices);

                        listView.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));
                        //设置adapter
                        listView.setAdapter(noticeAdapter);
                        //设置Item增加、移除动画
                        listView.setItemAnimator(new DefaultItemAnimator());

                        refresh.finishRefresh();
                    }

                    @Override
                    public void onFailure(@NonNull String errorCode, @NonNull String errorMessage) {
                        Toast.makeText(NoticeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        refresh.finishRefresh();
                    }
                });
            }
        });

    }

    @Override
    protected View onNavigateBar() {
        return new NavigateBar.Builder(this)
                .setTitle(getString(R.string.nav_notice_center))
                .setOnNavigateBarListener(new NavigateBar.OnNavigateBarListener() {
                    @Override
                    public void onBack() {
                        NoticeActivity.this.finish();
                    }
                })
                .getView();
    }

    class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

        List<Notice> lists;

        NoticeAdapter(List<Notice> list) {
            this.lists = list;
        }

        @NonNull
        @Override
        public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NoticeViewHolder(LayoutInflater.from(NoticeActivity.this).inflate(R.layout.layout_notice_item, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {

            final Notice notice = lists.get(position);
            holder.itemDate.setVisibility(View.VISIBLE);
            holder.itemNew.setVisibility(View.VISIBLE);
            switch (notice.getSmsType()) {
                case NoticeType.MESSAGE:
                    holder.itemIcon.setImageResource(R.drawable.ic_notice_news);
                    holder.itemTitle.setText(R.string.notice_msg);
                    holder.itemDate.setText(notice.getSmsSendTime());
                    holder.itemNew.setText(notice.getSmsContent());
                    break;
                case NoticeType.NOTICE:
                    holder.itemIcon.setImageResource(R.drawable.ic_notice_post);
                    holder.itemTitle.setText(R.string.notice_ntc);
                    holder.itemDate.setText(notice.getSmsSendTime());
                    holder.itemNew.setText(notice.getSmsContent());
                    break;
                case NoticeType.INFO:
                    holder.itemIcon.setImageResource(R.drawable.ic_notice_info);
                    holder.itemTitle.setText(R.string.notice_INFO);
                    holder.itemDate.setText(notice.getSmsSendTime());
                    holder.itemNew.setText(notice.getSmsContent());
                    break;
                default:
                    break;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NoticeListActivity.startForType(NoticeActivity.this, notice.getSmsType());
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class NoticeViewHolder extends RecyclerView.ViewHolder {
            ImageView itemIcon;
            TextView itemTitle;
            TextView itemDate;
            TextView itemNew;

            NoticeViewHolder(View view) {
                super(view);
                itemIcon = view.findViewById(R.id.notice_icon);
                itemTitle = view.findViewById(R.id.notice_title);
                itemDate = view.findViewById(R.id.notice_date);
                itemNew = view.findViewById(R.id.notice_new);
            }
        }
    }
}
