package nich.work.aequorea.common.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import nich.work.aequorea.R;

public class ParallaxImageView extends android.support.v7.widget.AppCompatImageView {
    
    public static final String RECYCLER_VIEW_TAG = "RECYCLER_VIEW_TAG";
    
    private final int MAX_PARALLAX_OFFSET = (int) getContext().getResources()
        .getDimension(R.dimen.parallax_image_offset);
    
    private boolean mAttached = false;
    
    private int mRecyclerViewHeight = -1;
    private int[] mRecyclerViewLocation = {-1, -1};
    
    public ParallaxImageView(Context context) {
        super(context);
    }
    
    public ParallaxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ParallaxImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec + MAX_PARALLAX_OFFSET);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        mAttached = true;
        
        View view = getRootView().findViewWithTag(RECYCLER_VIEW_TAG);
        if (view instanceof RecyclerView) {
            ((RecyclerView) view).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    
                    if (!mAttached) {
                        recyclerView.removeOnScrollListener(this);
                        return;
                    }
                    
                    if (mRecyclerViewHeight == -1) {
                        mRecyclerViewHeight = recyclerView.getHeight();
                        recyclerView.getLocationOnScreen(mRecyclerViewLocation);
                    }
                    
                    setParallaxTranslation();
                }
            });
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        mAttached = false;
    }
    
    private void setParallaxTranslation() {
        if (mRecyclerViewHeight == -1) {
            return;
        }
        
        int[] location = new int[2];
        getLocationOnScreen(location);
        
        boolean visible = location[1] + getHeight() > mRecyclerViewLocation[1] || location[1] < mRecyclerViewLocation[1] + mRecyclerViewHeight;
        
        if (!visible) return;
        
        float dy = (location[1] - mRecyclerViewLocation[1]);
        
        float translationY = MAX_PARALLAX_OFFSET * dy / mRecyclerViewHeight;
        
        setTranslationY(-translationY);
    }
}
