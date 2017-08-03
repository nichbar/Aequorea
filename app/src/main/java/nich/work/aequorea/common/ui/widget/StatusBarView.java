package nich.work.aequorea.common.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.DisplayUtils;


public class StatusBarView extends View {

    public static final float LIGHT_INIT_MASK_ALPHA = 0.03f;
    public static final float DARK_INIT_MASK_ALPHA = 0.2f;
    public static final float DARKER_MASK_ALPHA = 0.2f;

    private boolean mIsPlaceHolder;
    private boolean mIsOriginalStyle;
    private ObjectAnimator mObjectAnimator;

    public StatusBarView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView, defStyleAttr, defStyleRes);
        mIsPlaceHolder = a.getBoolean(R.styleable.StatusBarView_placeholder, false);
        a.recycle();

        if (!mIsPlaceHolder) {
            setBackgroundResource(android.R.color.black);
            setAlpha(getAlphaFromDeviceApi());
        }
    }

    private float getAlphaFromDeviceApi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return LIGHT_INIT_MASK_ALPHA;
        } else {
            return DARKER_MASK_ALPHA;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), DisplayUtils.getStatusBarHeight(getResources()));
    }

    public void setDarkMask(){
        setOriginalStyle(false);
        changeAlpha(DARKER_MASK_ALPHA);
    }

    public void setLightMask(){
        setOriginalStyle(true);
        changeAlpha(LIGHT_INIT_MASK_ALPHA);
    }

    private void changeAlpha(float alphaTo) {
        cancelAnimator();
        if (getAlpha() != alphaTo) {
            mObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), alphaTo)
                    .setDuration(150);
            mObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mObjectAnimator.start();
        }
    }

    private void cancelAnimator() {
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
    }

    public void setOriginalStyle(boolean originalStyle){
        mIsOriginalStyle = originalStyle;
    }

    public boolean isOriginalStyle(){
        return mIsOriginalStyle;
    }
}
