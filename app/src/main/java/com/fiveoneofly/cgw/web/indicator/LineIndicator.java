package com.fiveoneofly.cgw.web.indicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * 顶部进度条
 */
public class LineIndicator extends FrameLayout implements WebIndicator {

    /* 进度条颜色 */
    private int mColor;
    /* 进度条的画笔 */
    private Paint mPaint;
    /* 进度条动画 */
    private Animator mAnimator;
    /* 控件的宽度 */
    private int mTargetWidth = 0;

    /* 默认匀速动画最大的时长 */
    public static final int MAX_UNIFORM_SPEED_DURATION = 8 * 1000;
    /* 默认加速后减速动画最大时长 */
    public static final int MAX_DECELERATE_SPEED_DURATION = 450;
    /* 结束动画时长 Fade out */
    public static final int DO_END_ANIMATION_DURATION = 600;

    /* 当前匀速动画最大的时长 */
    private static int CURRENT_MAX_UNIFORM_SPEED_DURATION = MAX_UNIFORM_SPEED_DURATION;
    /* 当前加速后减速动画最大时长 */
    private static int CURRENT_MAX_DECELERATE_SPEED_DURATION = MAX_DECELERATE_SPEED_DURATION;

    /* 标志当前进度条的状态 */
    private int TAG = 0;
    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;

    private float mCurrentProgress = 0F;

    /* 默认的高度*/
    private final int DEFAULT_HEIGHT = 5;
    private int mHeight;

    public LineIndicator(Context context) {
        this(context, null);
    }

    public LineIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        mPaint = new Paint();
        mColor = Color.parseColor("#1aad19");
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);

        mTargetWidth = context.getResources().getDisplayMetrics().widthPixels;
        mHeight = dp2px(context, DEFAULT_HEIGHT);
    }

    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.AT_MOST) {
            w = w <= getContext().getResources().getDisplayMetrics().widthPixels ? w : getContext().getResources().getDisplayMetrics().widthPixels;
        }
        if (hMode == MeasureSpec.AT_MOST) {
            h = mHeight;
        }
        this.setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, mCurrentProgress / 100 * Float.valueOf(this.getWidth()), this.getHeight(), mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mTargetWidth = getMeasuredWidth();
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        if (mTargetWidth >= screenWidth) {
            CURRENT_MAX_DECELERATE_SPEED_DURATION = MAX_DECELERATE_SPEED_DURATION;
            CURRENT_MAX_UNIFORM_SPEED_DURATION = MAX_UNIFORM_SPEED_DURATION;
        } else {
            //取比值
            float rate = this.mTargetWidth / Float.valueOf(screenWidth);
            CURRENT_MAX_UNIFORM_SPEED_DURATION = (int) (MAX_UNIFORM_SPEED_DURATION * rate);
            CURRENT_MAX_DECELERATE_SPEED_DURATION = (int) (MAX_DECELERATE_SPEED_DURATION * rate);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //animator cause leak , if not cancel;
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    public void setColor(String color) {
        this.setColor(Color.parseColor(color));
    }

    private void startAnim(boolean isFinished) {

        float v = isFinished ? 100 : 95;

        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        mCurrentProgress = mCurrentProgress == 0f ? 0.00000001f : mCurrentProgress;

        if (!isFinished) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentProgress, v);
            float residue = 1f - mCurrentProgress / 100 - 0.05f;
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration((long) (residue * CURRENT_MAX_UNIFORM_SPEED_DURATION));
            valueAnimator.addUpdateListener(mAnimatorUpdateListener);
            valueAnimator.start();
            this.mAnimator = valueAnimator;
        } else {

            ValueAnimator segment95Animator = null;
            if (mCurrentProgress < 95f) {
                segment95Animator = ValueAnimator.ofFloat(mCurrentProgress, 95);
                float residue = 1f - mCurrentProgress / 100f - 0.05f;
                segment95Animator.setInterpolator(new LinearInterpolator());
                segment95Animator.setDuration((long) (residue * CURRENT_MAX_DECELERATE_SPEED_DURATION));
                segment95Animator.setInterpolator(new DecelerateInterpolator());
                segment95Animator.addUpdateListener(mAnimatorUpdateListener);
            }

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
            objectAnimator.setDuration(DO_END_ANIMATION_DURATION);
            ValueAnimator valueAnimatorEnd = ValueAnimator.ofFloat(95f, 100f);
            valueAnimatorEnd.setDuration(DO_END_ANIMATION_DURATION);
            valueAnimatorEnd.addUpdateListener(mAnimatorUpdateListener);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(objectAnimator, valueAnimatorEnd);

            if (segment95Animator != null) {
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.play(animatorSet).after(segment95Animator);
                animatorSet = animatorSet1;
            }
            animatorSet.addListener(mAnimatorListenerAdapter);
            animatorSet.start();
            mAnimator = animatorSet;
        }
        TAG = STARTED;
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float t = (float) animation.getAnimatedValue();
            LineIndicator.this.mCurrentProgress = t;
            LineIndicator.this.invalidate();
        }
    };

    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            doEnd();
        }
    };

    private void doEnd() {
        if (TAG == FINISH && mCurrentProgress == 100f) {
            setVisibility(GONE);
            mCurrentProgress = 0f;
            this.setAlpha(1f);
        }
        TAG = UN_START;
    }

    @Override
    public void reset() {
        mCurrentProgress = 0;
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    @Override
    public void setProgress(int newProgress) {

        float progress = Float.valueOf(newProgress);
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }
        if (progress < 95f) {
            return;
        }
        if (TAG != FINISH) {
            startAnim(true);
        }
    }

    @Override
    public void show() {

        if (getVisibility() == GONE) {
            this.setVisibility(VISIBLE);
            mCurrentProgress = 0f;
            startAnim(false);
        }
    }

    @Override
    public void hide() {
        TAG = FINISH;
    }

    @Override
    public ViewGroup.LayoutParams layoutParams() {
        return new LayoutParams(-1, mHeight);
    }
}
