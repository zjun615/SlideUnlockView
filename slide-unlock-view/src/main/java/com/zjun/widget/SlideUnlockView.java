package com.zjun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * SlideUnlockView
 * 滑动解锁控件
 *
 * @author Ralap
 * @description
 * @date 2018/7/13
 */
public class SlideUnlockView extends View {


    private int mStrokeColor = Color.parseColor("#FF0000");
    private int mBgColor = Color.parseColor("#EEEEEE");
    private int mTextColor = Color.parseColor("#888888");
    private int mSlidedBgColor = Color.parseColor("#FFFDE4D8");

    private int mStokeWidth = dp2px(2);
    private int mTextSize = dp2px(14);
    private int mGap = dp2px(2);
    private int mArrowLineWidth = dp2px(2);
    private int mArrowSize = dp2px(12);

    private float mCurrentPosition = 50;

    private int paddingStart, paddingTop, paddingEnd, paddingBottom;
    private int drawWidth, drawHeight, halfDrawHeight;
    private int slideWidth, slideHeight, halfSlideHeight;


    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;

    private OnUnlockListener mOnUnlockListener;

    public interface OnUnlockListener{
        void onUnlocked();
    }

    public SlideUnlockView(Context context) {
        this(context, null);
    }

    public SlideUnlockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideUnlockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideUnlockView);

        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        logD("onMeasure>>>width: mode=0x%X, size=%d; height: mode=0x%X, size=%d"
                , widthMode, widthSize, heightMode, heightSize);

        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            paddingStart = getPaddingStart();
            paddingEnd = getPaddingEnd();
        } else {
            paddingStart = getPaddingLeft();
            paddingEnd = getPaddingRight();
        }

        /*
        确定控件的具体宽和高
            若是wrap_content，则宽用200dp，高用40dp
         */
        final int defaultWidth = dp2px(200);
        final int defaultHeight = dp2px(40);
        int desireWidth, desireHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            desireWidth = widthSize;
        } else {
            desireWidth = defaultWidth + paddingStart + paddingEnd;
            if (widthMode == MeasureSpec.AT_MOST) {
                desireWidth = Math.min(desireWidth, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            desireHeight = heightSize;
        } else {
            desireHeight = defaultHeight + paddingTop + paddingBottom;
            if (heightMode == MeasureSpec.AT_MOST) {
                desireHeight = Math.min(desireHeight, heightSize);
            }
        }

        drawWidth = desireWidth - paddingStart - paddingEnd;
        drawHeight = desireHeight - paddingTop - paddingBottom;
        halfDrawHeight = drawHeight >> 1;
        slideWidth = drawWidth - (mGap << 1);
        slideHeight = drawHeight - (mGap << 1);
        halfSlideHeight = slideHeight >> 1;
        logD("onMeasure>>>desireWidth=%d, desireHeight=%d, drawWidth=%d, drawHeight=%d",
                desireWidth, desireHeight, drawWidth, drawHeight);
        setMeasuredDimension(desireWidth, desireHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private float mDownX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                return isInSwitch(mDownX, event.getY());
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float deltaX = moveX - mDownX;
                mDownX = moveX;
                mCurrentPosition += deltaX;
                mCurrentPosition = Math.max(mCurrentPosition, 0);
                mCurrentPosition = Math.min(mCurrentPosition, slideWidth - slideHeight);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if (mOnUnlockListener != null) {
                    if (mCurrentPosition >= slideWidth - slideHeight) {
                        mOnUnlockListener.onUnlocked();
                    } else {
                        mCurrentPosition = 0;
                        invalidate();
                    }
                }
                return true;
            default: break;
        }
        return false;
    }

    private boolean isInSwitch(float mDownX, float mDownY) {
        float centerX = paddingStart  + mGap + mCurrentPosition;
        float centerY = paddingTop + mGap + halfSlideHeight;
        return Math.pow(mDownX - centerX, 2) + Math.pow(mDownY - centerY, 2) <= Math.pow(halfSlideHeight, 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(paddingStart, paddingTop);

        // 1 绘制背景、边框、文字
        drawBg(canvas);

        // 2 绘制划过的颜色、开关
        drawSlided(canvas);

        canvas.restore();
    }

    private void drawBg(Canvas canvas) {
        // 背景
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);

        mPath.reset();
        mPath.moveTo(halfDrawHeight, 0);
        mPath.rLineTo(drawWidth - drawHeight, 0);
        mRectF.set(drawWidth - drawHeight, 0, drawWidth, drawHeight);
        mPath.addArc(mRectF, -90, 180);
        mRectF.set(0, 0, drawHeight, drawHeight);
        mPath.arcTo(mRectF, 90, 180, false);

        canvas.drawPath(mPath, mPaint);

        // 边框
        mPaint.setStrokeWidth(mStokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mStrokeColor);
        canvas.drawPath(mPath, mPaint);

        // 文字
        final String text = "向右滑动滑块";
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);
        final float w = mPaint.measureText(text);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        final float baseline = halfDrawHeight + (fm.descent - fm.ascent)/2 - fm.descent;
        canvas.drawText(text, (drawWidth - w) * .5f, baseline, mPaint);
    }

    private void drawSlided(Canvas canvas) {
        canvas.save();
        canvas.translate(mGap, mGap);

        // 滑过的背景色
        mPaint.setColor(mSlidedBgColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.reset();
        mPath.moveTo(halfSlideHeight, 0);
        mPath.rLineTo(mCurrentPosition, 0);
        mPath.rLineTo(0, slideHeight);
        mPath.rLineTo(-mCurrentPosition, 0);
        mRectF.set(0, 0, slideHeight, slideHeight);
        mPath.addArc(mRectF, 90, 180);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        // 按钮
        mPaint.setColor(mStrokeColor);
        mRectF.set(mCurrentPosition, 0, mCurrentPosition + slideHeight, slideHeight);
        canvas.drawArc(mRectF, 0, 360, true, mPaint);

        // 箭头
        final int halfSize = mArrowSize >> 1;
        mPath.reset();
        mPath.moveTo(halfSlideHeight + mCurrentPosition - halfSize, halfSlideHeight);
        mPath.rLineTo(mArrowSize, 0);
        mPath.moveTo( halfSlideHeight + mCurrentPosition, halfSlideHeight - halfSize);
        mPath.rLineTo(halfSize, halfSize);
        mPath.rLineTo(-halfSize, halfSize);
        mPaint.setColor(mBgColor);
        mPaint.setStrokeWidth(mArrowLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }


    public void setOnUnlockListener(OnUnlockListener listener) {
        this.mOnUnlockListener = listener;
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void logD(String format, Object... args) {
        Log.d("SlideLockView", String.format(format, args));
    }
}
