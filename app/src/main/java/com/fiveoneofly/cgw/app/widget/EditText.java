package com.fiveoneofly.cgw.app.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

public class EditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private boolean hasFoucs; // 控件是否有焦点

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle); //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }


    /**
     * 当 EditText 焦点发生变化的时候，根据长度显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    /**
     * 文本改变时
     *
     * @param s     文本改变之后的内容
     * @param start 文本开始改变时的起点位置，从0开始计算
     * @param count 要被改变的文本字数，即已经被替代的选中文本字数
     * @param after 改变后添加的文本字数，即替代选中文本后的文本字数
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
        if (mOnWatcherListener != null)
            mOnWatcherListener.onChanged(s);
    }

    /**
     * 文本改变之前
     *
     * @param s     文本改变之前的内容
     * @param start 文本开始改变时的起点位置，从0开始计算
     * @param count 要被改变的文本字数，即将要被替代的选中文本字数
     * @param after 改变后添加的文本字数，即替代选中文本后的文本字数
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * 文本改变结束后
     *
     * @param s 改变后的最终文本
     */
    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 设置清除图标的显示与隐藏
     *
     * @param visible 是否可见
     */
    private void setClearIconVisible(boolean visible) {
        if (mOnClearListener != null)
            mOnClearListener.onClearVisibility(visible);
    }

    private OnClearListener mOnClearListener;

    public void setOnClearListener(OnClearListener onClearListener) {
        this.mOnClearListener = onClearListener;
    }

    public interface OnClearListener {
        /**
         * 清除按钮是否显示
         *
         * @param visibility 是否可见
         */
        void onClearVisibility(boolean visibility);
    }

    private OnWatcherListener mOnWatcherListener;

    public void setOnWatcherListener(OnWatcherListener onWatcherListener) {
        this.mOnWatcherListener = onWatcherListener;
    }

    public interface OnWatcherListener {
        /**
         * 改变后
         *
         * @param s 改变后的字符串
         */
        void onChanged(CharSequence s);
    }
}