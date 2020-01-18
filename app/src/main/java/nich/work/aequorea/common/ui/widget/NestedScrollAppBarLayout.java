package nich.work.aequorea.common.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

@CoordinatorLayout.DefaultBehavior(NestedScrollAppBarLayout.Behaviour.class)
public class NestedScrollAppBarLayout extends AppBarLayout implements NestedScrollingChild {

    private int mAppLayoutHeight;

    private OnNestedScrollListener mListener;

    private ScrollAnimator mAnimator;

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
    }

    public void setOnNestedListener(OnNestedScrollListener listener) {
        mListener = listener;
    }

    public interface OnNestedScrollListener {
        void onNestedScrolling();
        void onStopNestedScrolling();
    }

    public static class Behaviour extends AppBarLayout.Behavior {

        private NestedScrollAppBarLayout mAppBarLayout;
        private float mOriginTop;

        private void bindChild(AppBarLayout appBarLayout) {
            if (mAppBarLayout == null) {
                mAppBarLayout = (NestedScrollAppBarLayout) appBarLayout;
            }
        }
    
        @Override
        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
            if (super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)) {
                bindChild(child);
                mOriginTop = child.getY();
                mAppBarLayout.stopScrollAnimation();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            bindChild(child);
            if (mAppBarLayout.mListener != null) {
                mAppBarLayout.mListener.onNestedScrolling();
            }
        }

        @Override
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
            bindChild(abl);

            float currentTop = abl.getTop();
            mAppBarLayout.mAppLayoutHeight = abl.getMeasuredHeight();

            if (currentTop > mOriginTop) {
                mAppBarLayout.scrollAndShowAppBar();
            } else if (currentTop < mOriginTop) {
                mAppBarLayout.scrollAndHideAppBar();
            }

            if (mAppBarLayout.mListener != null) {
                mAppBarLayout.mListener.onStopNestedScrolling();
            }
            super.onStopNestedScroll(coordinatorLayout, abl, target, type);
        }
    }

    private void scrollAndShowAppBar() {
        scrollToY(0);
    }

    private void scrollAndHideAppBar() {
        scrollToY(-mAppLayoutHeight);
    }

    private void scrollToY(int y) {
        if (getY() != y){
            mAnimator = new ScrollAnimator(y);
            mAnimator.start();
        }
    }

    private void stopScrollAnimation(){
        if (mAnimator != null)
            mAnimator.cancel();
    }

    @SuppressWarnings("unchecked")
    private class ScrollAnimator extends ValueAnimator {
        private CoordinatorLayout.Behavior behavior = null;
        private int lastY;

        ScrollAnimator(final int toY) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) getLayoutParams();
            behavior = lp.getBehavior();

            int fromY = (int) getY();
            lastY = fromY;

            setIntValues(fromY, toY);
            setDuration((long) (150.0 + 150.0 * Math.abs(toY - fromY) / getMeasuredHeight()));
            setInterpolator(new DecelerateInterpolator());
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentY = (int) animation.getAnimatedValue();
                    int[] total = new int[]{0, lastY - currentY};
                    int[] consumed = new int[]{0, 0};
                    behavior.onNestedPreScroll((CoordinatorLayout) getParent(), NestedScrollAppBarLayout.this, NestedScrollAppBarLayout.this, total[0], total[1], consumed, ViewCompat.TYPE_NON_TOUCH);
                    behavior.onNestedScroll((CoordinatorLayout) getParent(), NestedScrollAppBarLayout.this, NestedScrollAppBarLayout.this, consumed[0], consumed[1],
                            total[0] - consumed[0], total[1] - consumed[1], ViewCompat.TYPE_NON_TOUCH);

                    lastY = currentY;
                }
            });
        }
    }
}
