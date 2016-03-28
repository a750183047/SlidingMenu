package com.yan.slidingmenu;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 *
 * Created by a7501 on 2016/3/27.
 */
public class DragLayout extends FrameLayout {

    private ViewGroup mLiftContent;
    private ViewGroup mMainContent;

    private ViewDragHelper mDragHelper;
    private int mWidth;
    private int mHight;
    private int mRange;

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
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //为了兼容低版本，每次修改值之后  进行重绘
            invalidate();
        }
    };
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
        if (getChildCount() <2){
            throw new IllegalStateException("布局中至少要有两个子布局");  //异常状态
        }
        if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
            throw  new IllegalArgumentException("子布局必须是ViewGroup的子类");  //异常参数
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

        mRange = (int) (0.6 *mWidth);
    }
}
