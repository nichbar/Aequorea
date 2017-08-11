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
    public static final float LIGHT_MASK_ALPHA = 0.06f;
    public static final float DARK_INIT_MASK_ALPHA = 0.2f;
    public static final float DARKER_MASK_ALPHA = 0.2f;
    
    private boolean isPlaceHolder;
    private boolean isInitState;
    private ObjectAnimator objectAnimator;
    
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
        this.isPlaceHolder = a.getBoolean(R.styleable.StatusBarView_placeholder, false);
        a.recycle();
        
        if (!isPlaceHolder) {
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
    
    public void setDarkMask() {
        setInitState(false);
        changeAlpha(DARKER_MASK_ALPHA);
    }
    
    public void setLightMask() {
        setInitState(true);
        changeAlpha(LIGHT_INIT_MASK_ALPHA);
    }
    
    // only available in activity that does not need to change alpha
    public void setLight2ndMask() {
        setInitState(true);
        changeAlpha(LIGHT_MASK_ALPHA);
    }
    
    private void changeAlpha(float alphaTo) {
        cancelAnimator();
        if (getAlpha() != alphaTo) {
            objectAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), alphaTo)
                .setDuration(150);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
        }
    }
    
    private void cancelAnimator() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }
    
    public void setInitState(boolean isInitState) {
        this.isInitState = isInitState;
    }
    
    public boolean isInitState() {
        return isInitState;
    }
}
