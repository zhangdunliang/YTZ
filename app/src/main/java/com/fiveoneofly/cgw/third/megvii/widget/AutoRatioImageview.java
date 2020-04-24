package com.fiveoneofly.cgw.third.megvii.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by binghezhouke on 14-1-2.
 */
public class AutoRatioImageview extends ImageView {
	@SuppressWarnings("unused")
	private float mRatio = -1;
	@SuppressWarnings("unused")
	private int mPrefer = 0;

	public AutoRatioImageview(Context context) {
		super(context);
	}

	@SuppressLint("Recycle")
	public AutoRatioImageview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TypedArray typedArray = context.obtainStyledAttributes(attrs,
		// R.styleable.AutoRatioImageView);
		//
		// if (typedArray != null) {
		// mRatio = typedArray.getFloat(R.styleable.AutoRatioImageView_ratio,
		// -1.0f);
		// mPrefer = typedArray.getInt(R.styleable.AutoRatioImageView_prefer,
		// 0);
		// }
	}

	public AutoRatioImageview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(viewWidth, viewHeight);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		// int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		//
		// if (mRatio < 0) {
		// //this case means the ration is auto ratio
		// if (getDrawable() == null) {
		// //no image settled, invoke super onMeasure
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// } else {
		//
		// int drawableWidth = getDrawable().getIntrinsicWidth();
		// int drawableHeight = getDrawable().getIntrinsicHeight();
		// if (mPrefer == 0) {
		// // consider width
		//
		// setMeasuredDimension(viewWidth, viewWidth * drawableHeight /
		// drawableWidth);
		// } else {
		// setMeasuredDimension(viewHeight * drawableWidth / drawableHeight,
		// viewHeight);
		// }
		// }
		// } else {
		// // this view is fixed ratio
		// if (mPrefer == 0) {
		// // consider view width
		// setMeasuredDimension(viewWidth, (int) (viewWidth * mRatio));
		// } else {
		// setMeasuredDimension((int) (viewHeight / mRatio), viewWidth);
		// }
		// }

	}
}
