package com.yan.slidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 00
 * Created by a7501 on 2016/3/27.
 */
public class DragLayout extends FrameLayout {

    private ViewGroup mLiftContent;
    private ViewGroup mMainContent;

    private ViewDragHelper mDragHelper;
    private int mWidth;
    private int mHight;
    private int mRange;
    private OnDragStatusChangeListener mListener;
    private Status mStatus = Status.Close;

    private boolean once = true;

    /***
     * 状态枚举
     */
    public static enum Status{
        Close,Open,Draging;
    }

    public interface OnDragStatusChangeListener{
        void onClose();
        void onOpen();
        void onDraging(float percent);
    }

    public void setDragStatusListener(OnDragStatusChangeListener mListener){
        this.mListener = mListener;
    }
    public Status getStatus(){
        return mStatus;
    }
    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        //1 初始化 通过静态方法
        mDragHelper = ViewDragHelper.create(this, mCallback);


    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        // 3. 重写事件

        // 1. 根据返回结果决定当前child是否可以拖拽
        // child 当前被拖拽的View
        // pointerId 区分多点触摸的id
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            //当 capturedChild被捕获时 调用
            super.onViewCaptured(capturedChild, activePointerId);


        }

        @Override
        public int getViewHorizontalDragRange(View child) {

            //返回拖拽的范围 不对拖拽进行真正的限制 仅仅决定了动画执行速度
            return mRange;
        }

        //2 根据建议值修正要移动（横向）的位置
        //此时没有发生真正的移动
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // child  当前拖拽的 View
            //left  新的位置的建议值  dx 位置变化量

            if (child == mMainContent) {
                left = fixLeft(left);
            }
            return left;
        }


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }


        // 3 当 View 位置改变的时候 处理要做的事情 （更新状态 伴随动画 重绘界面等）
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

//            if (changedView == mMainContent) {
//                if (once)
//                    mMainContent.layout(0, 0, mWidth, mHight);
//                once = false;
//            }

            int newLeft = left;
            if (changedView == mLiftContent) {
                //把当前值变化量 传递给 mMainContent
                newLeft = mMainContent.getLeft() + dx;
            }
            //进行修正
            newLeft = fixLeft(newLeft);

            if (changedView == mLiftContent) {
                //当 左面板移动之后 在强制放回去
                mLiftContent.layout(0, 0, mWidth, mHight);
                mMainContent.layout(newLeft, 0, newLeft + mWidth, mHight);
            }

            dispatchDragEvent(newLeft);
            //为了兼容低版本，每次修改值之后  进行重绘
            // invalidate();
        }


        /**
         *  //当View 被释放的时候 处理的事情 （执行动画）
         * @param releasedChild     被释放的子View
         * @param xvel              水平方向的速度  释放前拖动的速度
         * @param yvel              竖直方向的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            super.onViewReleased(releasedChild, xvel, yvel);

            //判断速度来选择关闭或者打开
            //先判断所有打开的情况
            if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }
    };


    /***
     * 伴随动画
     *
     * @param newLeft
     */
    private void dispatchDragEvent(int newLeft) {
        float percent = newLeft * 1.0f / mRange;

        if (mListener != null){
            mListener.onDraging(percent);
        }
        //更新状态  执行回调
        Status preStatus  = mStatus;
        mStatus = updateStatus(percent);
        if (mStatus != preStatus){
            //状态发生变化
            if (mStatus == Status.Close){
                //当前状态为关闭
                if (mListener != null){
                    mListener.onClose();
                }
            }else if (mStatus == Status.Open){
                //当前状态为开
                if (mListener != null){
                    mListener.onOpen();
                }
            }
        }

        /**
         * 伴随动画
         */
        animViews(percent);


    }

    /**
     * 更新状态
     * @param percent
     * @return
     */
    private Status updateStatus(float percent) {
        if (percent == 0f){
            return Status.Close;
        }else if (percent == 1.0f) {
            return Status.Open;
        }
        return Status.Draging;
    }

    /***
     * 伴随动画
     * @param percent
     */
    private void animViews(float percent) {
        //左面板
        //缩放动画
        mLiftContent.setScaleX(evaluate(percent, 0.5f, 1.0f));
        mLiftContent.setScaleY(0.5f + 0.5f * percent);
        //平移动画
        mLiftContent.setTranslationX(evaluate(percent, -mWidth / 2.0f, 0));
        //透明度
        mLiftContent.setAlpha(evaluate(percent, 0.5f, 1.0f));
        //主面板
        //缩放动画
        mMainContent.setScaleX(evaluate(percent, 1.0f, 0.8f));
        mMainContent.setScaleY(evaluate(percent, 1.0f, 0.8f));
        //背景动画  颜色渐变
        getBackground().setColorFilter((Integer) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT),
                PorterDuff.Mode.SRC_OVER);
    }

    /***
     * 关闭侧边栏
     */
    public void close() {
        int finalLeft = 0;
        //触发一个平滑动画
        if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHight);
        }

    }

    /***
     * 打开侧边栏
     */
    public void open() {
        int finalLeft = mRange;
        if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHight);
        }
    }
    //2 传递触摸事件

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;  //返回true 持续接受事件
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //容错性检查
        if (getChildCount() < 2) {
            throw new IllegalStateException("布局中至少要有两个子布局");  //异常状态
        }
        if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("子布局必须是ViewGroup的子类");  //异常参数
        }
        mLiftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //当尺寸有变化的时候调用

        mHight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        mRange = (int) (0.6 * mWidth);
    }

    /**
     * 根据范围修正左边的值
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > mRange) {
            return mRange;
        }
        return left;
    }

    @Override
    public void computeScroll() {
        //持续平滑动画
        if (mDragHelper.continueSettling(true)) {
            //如果返回 true 动画还需要继续执行
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    /**
     * 估值器
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * 颜色变化过度
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                (int) ((startB + (int) (fraction * (endB - startB))));
    }
}
