package com.zjun.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * SlideUnlockView
 * 滑动解锁控件
 *
 * @author Ralap
 * @description
 * @date 2018/7/13
 */
public class SlideUnlockView extends View {

    /**
     * 背景颜色
     */
    private int mBgColor;
    /**
     * 边框颜色
     */
    private int mStrokeColor;
    /**
     * 边框宽度
     */
    private int mStrokeWidth;
    /**
     * 滑过的背景颜色
     */
    private int mSlideBgColor;
    /**
     * 滑动按钮与外边的间距
     */
    private int mSlideGap;
    /**
     * 滑动到终点的误差值。在误差值范围内都可解锁
     */
    private int mSlideDeviation;
    /**
     * 滑动按钮颜色
     */
    private int mBtnColor;
    /**
     * 被按下时，按钮的光环大小
     */
    private int mBtnRingSize;
    /**
     * 被按下时，按钮的光环颜色
     */
    private int mBtnRingColor;
    /**
     * 滑动按钮里箭头大小
     */
    private int mArrowSize;
    /**
     * 滑动按钮里箭头线条宽度
     */
    private int mArrowLineWidth;
    /**
     * 滑动按钮里箭头颜色
     */
    private int mArrowColor;
    /**
     * 提示文字内容
     */
    private String mTips;
    /**
     * 提示文字字体大小
     */
    private int mTipsSize;
    /**
     * 提示文字字体颜色
     */
    private int mTipsColor;
    /**
     * 提示文字是否粗体
     */
    private boolean mTipsBold;
    /**
     * 是否需要返回起点的动画
     */
    private boolean mBackAnimEnable;
    /**
     * 返回起点的总时长
     */
    private int mBackFullDuration;

    /**
     * 内边距
     */
    private int paddingStart, paddingTop, paddingEnd, paddingBottom;
    /**
     * 可绘制区域，即背景的绘制区域
     */
    private int drawWidth, drawHeight, halfDrawHeight;
    /**
     * 滑动的区域
     *
     * 其中，halfSlideHeight也是滑块按钮的半径
     */
    private int slideWidth, slideHeight, halfSlideHeight;
    /**
     * 终点
     */
    private int terminalPoint;
    /**
     * 当前位置。如果要xml布局文件中查看滑块在中间的效果，可赋值>0
     */
    private float mCurrentPosition = 0;
    /**
     * 正在执行动画
     */
    private boolean mIsAnimatorRunning;
    /**
     * 是否触摸在滑动按钮上
     */
    private boolean mIsTouchOnButton;

    /**
     * 绘制时的基本工具
     */
    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;
    /**
     * 前一次Down/Move的横坐标
     * {@link #onTouchEvent(MotionEvent)}
     */
    private float mLastX;

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
        mBgColor = ta.getColor(R.styleable.SlideUnlockView_suv_bgColor, Color.parseColor("#FFEEEEEE"));
        mStrokeColor = ta.getColor(R.styleable.SlideUnlockView_suv_strokeColor, Color.parseColor("#FFFF0000"));
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_strokeWidth, dp2px(2));
        mSlideBgColor = ta.getColor(R.styleable.SlideUnlockView_suv_slideBgColor, Color.parseColor("#FFFDE4D8"));
        mSlideGap = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_slideGap, dp2px(2));
        mSlideDeviation = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_slideDeviation, dp2px(5));
        mBtnColor = ta.getColor(R.styleable.SlideUnlockView_suv_btnColor, mStrokeColor);
        mBtnRingSize = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_btnRingSize, mSlideGap);
        mBtnRingColor = ta.getColor(R.styleable.SlideUnlockView_suv_btnRingColor, Color.parseColor("#A0ABABAB"));
        // 不设置的情况下，使用-1，onMeasure()中，根据实际高度自动计算
        mArrowSize = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_arrowSize, -1);
        mArrowLineWidth = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_arrowLineWidth, dp2px(2));
        mArrowColor = ta.getColor(R.styleable.SlideUnlockView_suv_arrowColor, mBgColor);
        mTips = ta.getString(R.styleable.SlideUnlockView_suv_tips);
        mTipsSize = ta.getDimensionPixelSize(R.styleable.SlideUnlockView_suv_tipsSize, dp2px(14));
        mTipsColor = ta.getColor(R.styleable.SlideUnlockView_suv_tipsColor, Color.parseColor("#FF888888"));
        mTipsBold = ta.getBoolean(R.styleable.SlideUnlockView_suv_tipsBold, false);
        mBackAnimEnable = ta.getBoolean(R.styleable.SlideUnlockView_suv_backAnimatorEnable, false);
        mBackFullDuration = ta.getInt(R.styleable.SlideUnlockView_suv_backFullDuration, 500);
        ta.recycle();

        // 初始化
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
        slideWidth = drawWidth - (mSlideGap << 1);
        slideHeight = drawHeight - (mSlideGap << 1);
        terminalPoint = slideWidth - slideHeight;
        halfSlideHeight = slideHeight >> 1;
        if (mArrowSize == -1) {
            mArrowSize = halfSlideHeight;
        }
        logD("onMeasure>>>desireWidth=%d, desireHeight=%d, drawWidth=%d, drawHeight=%d",
                desireWidth, desireHeight, drawWidth, drawHeight);
        setMeasuredDimension(desireWidth, desireHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsAnimatorRunning) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mIsTouchOnButton = isInSlideButton(mLastX, event.getY());
                return mIsTouchOnButton;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float deltaX = moveX - mLastX;
                mLastX = moveX;
                mCurrentPosition += deltaX;
                // 限定范围
                mCurrentPosition = Math.min(Math.max(mCurrentPosition, 0), terminalPoint);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                mIsTouchOnButton = false;
                if (mOnUnlockListener != null) {
                    if (mCurrentPosition >= terminalPoint - mSlideDeviation) {
                        mCurrentPosition = terminalPoint;
                        postInvalidate();
                        mOnUnlockListener.onUnlocked();
                    } else {
                        if (mBackAnimEnable) {
                            startBackAnimator();
                        }
                        mCurrentPosition = 0;
                        invalidate();
                    }
                }
                return true;
            default: break;
        }
        return false;
    }

    /**
     * 开始执行返回动画
     */
    private void startBackAnimator() {
        if (mBackFullDuration < 0) {
            return;
        }
        mIsAnimatorRunning = true;
        ValueAnimator animator = ValueAnimator.ofFloat(mCurrentPosition, 0);
        // 插值器：先加速后减速
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentPosition = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimatorRunning = false;
            }
        });
        animator.setDuration((long) (mBackFullDuration * mCurrentPosition / (terminalPoint)));
        animator.start();
    }

    /**
     * 判断按下点，是否在圆形滑动按钮里
     * @return true-在滑动按钮里；false-不在
     */
    private boolean isInSlideButton(float downX, float downY) {
        // 当前按钮的中心点坐标
        float centerX = paddingStart  + mSlideGap + halfSlideHeight + mCurrentPosition;
        float centerY = paddingTop + mSlideGap + halfSlideHeight;
        return Math.pow(downX - centerX, 2) + Math.pow(downY - centerY, 2) <= Math.pow(halfSlideHeight, 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        // 移动Canvas绘制坐标系
        canvas.translate(paddingStart, paddingTop);

        // 1 绘制背景、边框、文字
        drawBg(canvas);

        // 2 绘制划过的颜色、开关
        drawSlided(canvas);

        // 恢复到修改前的坐标系。当然这里已经绘制完成了，包括一开始的save()没什么卵用，只为了保持一种撸码习惯，仅此而已
        canvas.restore();
    }

    /**
     * 绘制背景、边框、文字
     * @param canvas
     */
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
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mStrokeColor);
        canvas.drawPath(mPath, mPaint);

        // 文字
        if (mTips != null && !mTips.isEmpty()) {
            mPaint.setColor(mTipsColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(mTipsSize);
            mPaint.setFakeBoldText(mTipsBold);
            final float w = mPaint.measureText(mTips);
            // 为了垂直对齐，详细原理可参考：https://blog.csdn.net/a10615/article/details/52658927
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            final float baseline = halfDrawHeight + (fm.descent - fm.ascent)/2 - fm.descent;
            canvas.drawText(mTips, (drawWidth - w) * .5f, baseline, mPaint);
        }

    }

    /**
     * 绘制划过的颜色、滑动开关
     */
    private void drawSlided(Canvas canvas) {
        canvas.save();
        canvas.translate(mSlideGap, mSlideGap);

        // 滑过的背景色
        mPaint.setColor(mSlideBgColor);
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
        mPaint.setColor(mBtnColor);
        mRectF.set(mCurrentPosition, 0, mCurrentPosition + slideHeight, slideHeight);
        canvas.drawArc(mRectF, 0, 360, true, mPaint);

        // 光环
        if (mIsTouchOnButton && mBtnRingSize > 0) {
            mPaint.setColor(mBtnRingColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBtnRingSize);
            canvas.drawCircle(halfSlideHeight + mCurrentPosition, halfSlideHeight, halfSlideHeight + (mSlideGap >> 1), mPaint);
        }

        // 箭头
        final int halfSize = mArrowSize >> 1;
        mPath.reset();
        mPath.moveTo(halfSlideHeight + mCurrentPosition - halfSize, halfSlideHeight);
        mPath.rLineTo(mArrowSize, 0);
        mPath.moveTo( halfSlideHeight + mCurrentPosition, halfSlideHeight - halfSize);
        mPath.rLineTo(halfSize, halfSize);
        mPath.rLineTo(-halfSize, halfSize);
        mPaint.setColor(mArrowColor);
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

    /**
     * 获取是否为粗体
     */
    public boolean isBold() {
        return mTipsBold;
    }

    public void setBold(boolean isBold) {
        this.mTipsBold = isBold;
        postInvalidate();
    }

    public boolean isBackAnimEnable() {
        return mBackAnimEnable;
    }

    public void setBackAnimEnable(boolean enable) {
        this.mBackAnimEnable = enable;
    }

    public void setBackAnimDuration(int duraiton) {
        this.mBackFullDuration = duraiton;
    }
}
