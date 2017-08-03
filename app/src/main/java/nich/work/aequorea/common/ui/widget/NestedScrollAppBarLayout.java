package nich.work.aequorea.common.ui.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

@CoordinatorLayout.DefaultBehavior(NestedScrollAppBarLayout.Behaviour.class)
public class NestedScrollAppBarLayout extends AppBarLayout implements NestedScrollingChild {

    private float mTouchSlop;
    private OnNestedScrollListener mListener;

    public NestedScrollAppBarLayout(Context context) {
        super(context);
        initialize();
    }

    public NestedScrollAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private float getTouchSlop() {
        return mTouchSlop;
    }

    public void setOnNestedListener(OnNestedScrollListener listener){
        mListener = listener;
    }

    public interface OnNestedScrollListener{
        void onNestedScrolling();
    }

    public static class Behaviour extends AppBarLayout.Behavior {

        private NestedScrollAppBarLayout mAppBarLayout;
        private float mOldY;
        private boolean mIsBeingDragged;

        public Behaviour() {
            super();
        }

        public Behaviour(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            bindChild(child);
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mAppBarLayout.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                    mOldY = ev.getY();
                    mIsBeingDragged = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!mIsBeingDragged) {
                        if (Math.abs(ev.getY() - mOldY) > mAppBarLayout.getTouchSlop()) {
                            mIsBeingDragged = true;
                        }
                    }
                    mOldY = ev.getY();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mAppBarLayout.stopNestedScroll();
                    break;
            }
            return super.onTouchEvent(parent, child, ev);
        }

        private void bindChild(AppBarLayout appBarLayout) {
            if (mAppBarLayout == null) {
                mAppBarLayout = (NestedScrollAppBarLayout) appBarLayout;
            }
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            bindChild(child);
            if (mAppBarLayout.mListener != null){
                mAppBarLayout.mListener.onNestedScrolling();
            }
        }
    }
}
