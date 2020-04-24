package com.yxjr.credit.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.yxjr.credit.R;
import com.yxjr.credit.widget.wheel.AbstractWheelTextAdapter;
import com.yxjr.credit.widget.wheel.OnWheelChangedListener;
import com.yxjr.credit.widget.wheel.OnWheelScrollListener;
import com.yxjr.credit.widget.wheel.WheelView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午10:57:58
 * @描述:TODO[日期选择器]
 */
public class WheelDate implements View.OnClickListener {

    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;

    private View wheel;
    private TextView btnSure;
    private TextView btnCancel;

    private ArrayList<String> arry_years = new ArrayList<String>();
    private ArrayList<String> arry_months = new ArrayList<String>();
    private ArrayList<String> arry_days = new ArrayList<String>();
    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDaydapter;

    private int month;
    private int day;

    private int currentYear = getYear();
    private int currentMonth = 1;
    private int currentDay = 1;

    private int maxTextSize = 26;
    private int minTextSize = 18;
    private int visibleItems = 5;

    private boolean isSetdata = false;

    private String selectYear;
    private String selectMonth;
    private String selectDay;

    private OnSelectDateListener onSelectDateListener;
    private LayoutInflater mInflater = null;
    private PopupWindow menuWindow;
    private Context mContext;
    private Activity mActivity;

    public WheelDate(Activity mActivity) {
        this.mContext = mActivity;
        this.mActivity = mActivity;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void show() {
        setPopwindow(setDataPick(), mActivity);
    }

    public void setSelectDateListener(OnSelectDateListener onSelectDateListener) {
        this.onSelectDateListener = onSelectDateListener;
    }

    /**
     * 设置年月日
     *
     * @param year
     * @param month
     * @param day
     */
    public void setDate(int year, int month, int day) {
        selectYear = year + "";
        selectMonth = month + "";
        selectDay = day + "";
        isSetdata = true;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentDay = day;
        if (year == getYear()) {
            this.month = getMonth();
        } else {
            this.month = 12;
        }
        calDays(year, month);
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onSelectDateListener != null) {
                onSelectDateListener.onClick(selectYear, selectMonth, selectDay);
                menuWindow.dismiss();
            }
        } else if (v == btnCancel) {
            menuWindow.dismiss();
        } else if (v == wheel) {
            return;
        } else {
            menuWindow.dismiss();
        }
    }

    public interface OnSelectDateListener {
        public void onClick(String year, String month, String day);
    }

    private void setPopwindow(View view, final Activity mActivity) {
        this.menuWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        menuWindow.setFocusable(true);
        ColorDrawable cd = new ColorDrawable(0x000000);
        menuWindow.setBackgroundDrawable(cd);
        menuWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();// 产生背景变暗效果
        lp.alpha = 0.4f;
        mActivity.getWindow().setAttributes(lp);
        menuWindow.setOutsideTouchable(true);
        menuWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                menuWindow = null;
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });
    }

    private View setDataPick() {
        View view = mInflater.inflate(R.layout.yxjr_credit_wheel_date, null);

        wvYear = (WheelView) view.findViewById(R.id.yx_credit_date_year);
        wvMonth = (WheelView) view.findViewById(R.id.yx_credit_date_month);
        wvDay = (WheelView) view.findViewById(R.id.yx_credit_date_day);

        wheel = view.findViewById(R.id.yx_credit_date_wheel);
        btnSure = (TextView) view.findViewById(R.id.yx_credit_date_sure);
        btnSure.setText("确定");
        btnCancel = (TextView) view.findViewById(R.id.yx_credit_date_cancel);
        btnCancel.setText("取消");

        wheel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (!isSetdata) {
            initData();
        }

        initYears();
        mYearAdapter = new CalendarTextAdapter(mContext, arry_years, setYear(currentYear), maxTextSize, minTextSize);
        wvYear.setVisibleItems(visibleItems);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(setYear(currentYear));

        initMonths(month);
        mMonthAdapter = new CalendarTextAdapter(mContext, arry_months, setMonth(currentMonth), maxTextSize, minTextSize);
        wvMonth.setVisibleItems(visibleItems);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(setMonth(currentMonth));

        initDays(day);
        mDaydapter = new CalendarTextAdapter(mContext, arry_days, currentDay - 1, maxTextSize, minTextSize);
        wvDay.setVisibleItems(visibleItems);
        wvDay.setViewAdapter(mDaydapter);
        wvDay.setCurrentItem(currentDay - 1);

        wvYear.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                selectYear = currentText;
                setTextviewSize(currentText, mYearAdapter);
                currentYear = Integer.parseInt(currentText);
                setYear(currentYear);
                initMonths(month);
                mMonthAdapter = new CalendarTextAdapter(mContext, arry_months, 0, maxTextSize, minTextSize);
                wvMonth.setVisibleItems(visibleItems);
                wvMonth.setViewAdapter(mMonthAdapter);
                wvMonth.setCurrentItem(0);
            }
        });

        wvYear.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mYearAdapter);
            }
        });

        wvMonth.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                selectMonth = currentText;
                setTextviewSize(currentText, mMonthAdapter);
                setMonth(Integer.parseInt(currentText));
                initDays(day);
                mDaydapter = new CalendarTextAdapter(mContext, arry_days, 0, maxTextSize, minTextSize);
                wvDay.setVisibleItems(visibleItems);
                wvDay.setViewAdapter(mDaydapter);
                wvDay.setCurrentItem(0);
            }
        });

        wvMonth.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMonthAdapter);
            }
        });

        wvDay.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
                selectDay = currentText;
            }
        });

        wvDay.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
            }
        });
        return view;

    }

    private void initYears() {
        for (int i = getYear(); i >= 1900; i--) {
            arry_years.add(i + "");
        }
    }

    private void initMonths(int months) {
        arry_months.clear();
        for (int i = 1; i <= months; i++) {
            arry_months.add(String.format("%02d", i));
        }
    }

    private void initDays(int days) {
        arry_days.clear();
        for (int i = 1; i <= days; i++) {
            arry_days.add(String.format("%02d", i));
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.yxjr_credit_wheel_item, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.yx_credit_tv_temp_value);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    private void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

    private int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    private int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    private int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }

    private void initData() {
        setDate(getYear(), getMonth(), getDay());
        this.currentDay = 1;
        this.currentMonth = 1;
    }

    /**
     * 设置年份
     *
     * @param year
     */
    private int setYear(int year) {
        int yearIndex = 0;
        if (year != getYear()) {
            this.month = 12;
        } else {
            this.month = getMonth();
        }
        for (int i = getYear(); i > 1950; i--) {
            if (i == year) {
                return yearIndex;
            }
            yearIndex++;
        }
        return yearIndex;
    }

    /**
     * 设置月份
     *
     * @param month
     * @return
     */
    private int setMonth(int month) {
        int monthIndex = 0;
        calDays(currentYear, month);
        for (int i = 1; i < this.month; i++) {
            if (month == i) {
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }

    /**
     * 计算每月多少天
     *
     * @param year
     * @param month
     */
    private void calDays(int year, int month) {
        boolean leayyear = false;
        if (year % 4 == 0 && year % 100 != 0) {
            leayyear = true;
        } else {
            leayyear = false;
        }
        for (int i = 1; i <= 12; i++) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    this.day = 31;
                    break;
                case 2:
                    if (leayyear) {
                        this.day = 29;
                    } else {
                        this.day = 28;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    this.day = 30;
                    break;
            }
        }
        if (year == getYear() && month == getMonth()) {
            this.day = getDay();
        }
    }

}