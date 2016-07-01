package com.wuxufanhua.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ScrollView;

/**
 * Created by wuxufanhua on 2016/7/1.
 */
public class PullScrollView extends ScrollView{
    public PullScrollView(Context context) {
        this(context,null);
    }

    public PullScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public interface OnScroll {
        public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    private OnScroll onScroll;

    public OnScroll getOnScroll() {
        return onScroll;
    }

    public void setOnScroll(OnScroll onScroll) {
        this.onScroll = onScroll;
    }

    /**
     * 阻尼系数,越小阻力就越大.
     */
    private static final float SCROLL_RATIO = 0.5f;

    /**
     * 头部view.
     */
    private View mHeader;

    /**
     * ScrollView的content view.
     */
    private View mContentView;

    /**
     * 首次点击的Y坐标.
     */
    private float mTouchDownY;

    /**
     * 是否关闭ScrollView的滑动.
     */
    private boolean mEnableTouch = false;

    /**
     * 是否开始移动.
     */
    private boolean isMoving = false;

    /**
     * 是否移动到顶部位置.
     */
    private boolean isTop = false;

    private boolean enableMove = true;

    public void setEnableMove(boolean enableMove) {
        this.enableMove = enableMove;
    }

    private enum State {
        /**
         * 顶部
         */
        UP,
        /**
         * 底部
         */
        DOWN,
        /**
         * 正常
         */
        NORMAL
    }

    /**
     * 状态.
     */
    private State mState = State.NORMAL;


    private void init() {
        // set scroll mode
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            ViewGroup childAt = (ViewGroup) getChildAt(0);
            mContentView = childAt.getChildAt(1);
            mHeader = childAt.getChildAt(0);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (onScroll != null) {
            onScroll.onScrollChanged(this, l, t, oldl, oldt);
        }

        if (getScrollY() == 0) {
            isTop = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownY = ev.getY();
            mEnableTouch = false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mContentView != null && enableMove) {
            doTouchEvent(ev);
        }

        // 禁止控件本身的滑动.
        return mEnableTouch || super.onTouchEvent(ev);
    }

    /**
     * 触摸事件处理
     *
     * @param event
     */
    private void doTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                doActionMove(event);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 回滚动画
                if (isNeedAnimation()) {
                    rollBackAnimation();
                }

                if (getScrollY() == 0) {
                    mState = State.NORMAL;
                    isTop = true;
                }
                isMoving = false;
                mEnableTouch = false;
                break;

            default:
                break;
        }
    }

    /**
     * 执行移动动画
     *
     * @param event
     */
    private void doActionMove(MotionEvent event) {

        // if (getScrollY() == 0) {
        // mState = State.NORMAL;

        // 滑动经过顶部初始位置时，修正Touch down的坐标为当前Touch点的坐标
        // if (isTop) {
        // isTop = false;
        // mTouchDownY = event.getY();
        // }
        // }

        float deltaY = event.getY() - mTouchDownY;

        // 对于首次Touch操作要判断方位：UP OR DOWN
        if (deltaY < 0 && mState == State.NORMAL) {
            mState = State.UP;
        } else if (deltaY > 0 && mState == State.NORMAL) {
            mState = State.DOWN;
        }

        if (mState == State.UP) {
            deltaY = deltaY < 0 ? deltaY : 0;

            isMoving = false;
            mEnableTouch = false;

        } else if (mState == State.DOWN) {
            if (getScrollY() <= deltaY) {
                mEnableTouch = true;
                isMoving = true;
            }
            deltaY = deltaY < 0 ? 0 : deltaY;
        }

        if (isMoving) {

            // 计算header移动距离(手势移动的距离*阻尼系数*0.5)
            float headerMoveHeight = deltaY * 0.5f * SCROLL_RATIO;

            // 计算content移动距离(手势移动的距离*阻尼系数)
            float contentMoveHeight = deltaY * SCROLL_RATIO;

            mHeader.setTranslationY(headerMoveHeight);
            mContentView.setTranslationY(contentMoveHeight);
            float scale = deltaY / mHeader.getHeight() / 2.5f + 1;
            mHeader.setScaleX(scale);
            mHeader.setScaleY(scale);
            // }
        }
    }

    private void rollBackAnimation() {

        mHeader.animate().translationY(0).setDuration(300)
                .setInterpolator(new AccelerateInterpolator());
        mContentView.animate().translationY(0).setDuration(300)
                .setInterpolator(new AccelerateInterpolator());
        mHeader.animate().scaleX(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
        mHeader.animate().scaleY(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
    }

    /**
     * 是否需要开启动画
     */
    private boolean isNeedAnimation() {
        return isMoving;
    }


}
