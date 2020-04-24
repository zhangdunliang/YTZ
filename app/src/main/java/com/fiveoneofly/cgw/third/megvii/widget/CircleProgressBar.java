package com.fiveoneofly.cgw.third.megvii.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 画倒计时圆通过传递 Progress和max来画
 */
public class CircleProgressBar extends View {

	@SuppressWarnings("unused")
	private final TextPaint mTextPaint;
	private int mProgress = 100;
	private int mMax = 100;
	private Paint mPaint;
	private RectF mOval;
	private int mWidth = 20;
	private int mRadius = 75;
	//	private Bitmap mBitmap;

	SweepGradient sweepGradient = null;

	public int getMax() {
		return mMax;
	}

	public void setMax(int max) {
		this.mMax = max;
	}

	public int getProgress() {
		return mProgress;
	}

	public void setProgress(int progress) {
		this.mProgress = progress;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int use = width > height ? height : width;
		int sum = mWidth + mRadius;
		mWidth = use / 2 * mWidth / sum;
		mRadius = use / 2 * mRadius / sum;
		setMeasuredDimension(use, use);
	}

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mOval = new RectF();
		mTextPaint = new TextPaint();
		//		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
		sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, new int[] { 0xfffe9a8e, 0xff3fd1e4, 0xffdc968e }, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//        oval.set(0,0,getWidth(), getHeight());

		//        canvas.drawBitmap(bit,null, oval, paint);
		mPaint.setAntiAlias(true);
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		//		mPaint.setColor(0xff000000);
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setStrokeWidth(mWidth);// 设置画笔宽度
		mPaint.setStyle(Paint.Style.STROKE);// 设置中空的样式
		canvas.drawCircle(mRadius + mWidth, mRadius + mWidth, mRadius, mPaint);// 在中心为（100,100）的地方画个半径为55的圆，宽度为setStrokeWidth：10，也就是灰色的底边

		//        paint.setShader(sweepGradient);
		//		mPaint.setColor(0xff3fd1e4);// 设置画笔为绿色
		mPaint.setColor(Color.parseColor("#FBB91A"));// 设置画笔为绿色
		mOval.set(mWidth, mWidth, mRadius * 2 + mWidth, (mRadius * 2 + mWidth));// 设置类似于左上角坐标（45,45），右下角坐标（155,155），这样也就保证了半径为55
		canvas.drawArc(mOval, -90, ((float) mProgress / mMax) * 360, false, mPaint);// 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
		mPaint.reset();
		//        textPaint.setStrokeWidth(3);
		//        textPaint.setTextSize(getMeasuredWidth() / 3);// 设置文字的大小
		//        textPaint.setColor(0xff318deb);// 设置画笔颜色
		//        textPaint.setTextAlign(Paint.Align.CENTER);
		//        FontMetrics fontMetrics= textPaint.getFontMetrics();
		//        float baseX = Radius + Width;
		//        float baseY = Radius + Width;
		//        float textHeight = fontMetrics.descent - fontMetrics.ascent;
		//        if (progress == max) {
		//            // canvas.drawText("完成", baseX, baseY - fontMetrics.descent
		//            // + (textHeight) / 2, paint);
		//            if (bit!=null)
		//            canvas.drawBitmap(bit, Radius + Width - bit.getHeight() / 2,
		//                    Radius + Width - bit.getHeight() / 2, paint);
		//        } else {
		//            canvas.drawText(progress * 100 / max + "%", baseX, baseY - fontMetrics.descent
		//                    + (textHeight) / 2, textPaint);
		//
		//        }
	}
}
