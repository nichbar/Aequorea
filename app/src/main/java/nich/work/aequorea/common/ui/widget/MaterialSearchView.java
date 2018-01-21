package nich.work.aequorea.common.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.AnimationUtil;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.ui.adapter.InstantSearchAdapter;

public class MaterialSearchView extends FrameLayout implements Filter.FilterListener {
    
    private boolean mIsSearchOpen = false;
    private int mAnimationDuration;
    private boolean mClearingFocus;
    private boolean mSuggestionVisible;
    private String mTheme;
    
    //Views
    private View mSearchLayout;
    private View mDividerView;
    private ListView mSuggestionsListView;
    private EditText mSearchSrcTextView;
    private ImageButton mBackBtn;
    private ImageButton mEmptyBtn;
    private RelativeLayout mSearchTopBar;
    
    private CharSequence mOldQueryText;
    
    private MaterialSearchView.OnQueryTextListener mOnQueryChangeListener;
    private MaterialSearchView.SearchViewListener mSearchViewListener;
    
    private InstantSearchAdapter mAdapter;
    
    private Context mContext;
    
    public MaterialSearchView(Context context) {
        this(context, null);
    }
    
    public MaterialSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public MaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        
        mContext = context;
        
        initiateView();
    }
    
    private void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_search_view, this, true);
        
        mSearchLayout = findViewById(R.id.search_layout);
        mSearchTopBar = mSearchLayout.findViewById(R.id.search_top_bar);
        mSuggestionsListView = mSearchLayout.findViewById(R.id.suggestion_list);
        mSearchSrcTextView = mSearchLayout.findViewById(R.id.searchTextView);
        mBackBtn = mSearchLayout.findViewById(R.id.action_up_btn);
        mEmptyBtn = mSearchLayout.findViewById(R.id.action_empty_btn);
        mDividerView = mSearchLayout.findViewById(R.id.view_divider);
        
        mSearchSrcTextView.setOnClickListener(mOnClickListener);
        mBackBtn.setOnClickListener(mOnClickListener);
        mEmptyBtn.setOnClickListener(mOnClickListener);
    
        applyTheme();
        initSearchView();
        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
    }
    
    private void initSearchView() {
        mSearchSrcTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });
        
        mSearchSrcTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startFilter(s);
                MaterialSearchView.this.onTextChanged(s);
                if (s.length() == 0 && mSuggestionsListView != null) {
                    if (mAdapter != null) {
                        animateSuggestions(mAdapter.getListHeight(), 0);
                    }
                    mAdapter = new InstantSearchAdapter(getContext(), null);
                    mSuggestionsListView.setAdapter(mAdapter);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                
            }
        });
        
        mSearchSrcTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(mSearchSrcTextView);
                    showSuggestions();
                }
            }
        });
    }
    
    private void startFilter(CharSequence s) {
        if (mAdapter != null && mAdapter instanceof Filterable) {
            ((Filterable) mAdapter).getFilter().filter(s, MaterialSearchView.this);
        }
    }
    
    private final OnClickListener mOnClickListener = new OnClickListener() {
        
        public void onClick(View v) {
            if (v == mBackBtn) {
                closeSearch();
            } else if (v == mEmptyBtn) {
                mSearchSrcTextView.setText(null);
            } else if (v == mSearchSrcTextView) {
                showSuggestions();
            }
        }
    };
    
    private void onTextChanged(CharSequence newText) {
        CharSequence text = mSearchSrcTextView.getText();
        boolean hasText = !TextUtils.isEmpty(text);
        if (hasText) {
            mEmptyBtn.setVisibility(VISIBLE);
        } else {
            mEmptyBtn.setVisibility(GONE);
        }
        
        if (mOnQueryChangeListener != null && !TextUtils.equals(newText, mOldQueryText)) {
            mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        mOldQueryText = newText.toString();
    }
    
    private void onSubmitQuery() {
        CharSequence query = mSearchSrcTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryChangeListener == null || !mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
                closeSearch();
                mSearchSrcTextView.setText(null);
            }
        }
    }
    
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    public void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
    
    //Public Attributes
    
    @Override
    public void setBackground(Drawable background) {
        mSearchTopBar.setBackground(background);
    }
    
    @Override
    public void setBackgroundColor(int color) {
        mSearchTopBar.setBackgroundColor(color);
    }
    
    public void setTextColor(int color) {
        mSearchSrcTextView.setTextColor(color);
    }
    
    public void setHint(CharSequence hint) {
        mSearchSrcTextView.setHint(hint);
    }
    
    public void showSuggestions() {
        if (mAdapter == null || mAdapter.getCount() == 0) return;
        
        if (!mSuggestionVisible) {
            animateSuggestions(0, mAdapter.getListHeight());
        } else {
            animateSuggestions(mSuggestionsListView.getHeight(), mAdapter.getListHeight());
        }
    }
    
    public void dismissSuggestions() {
        if (mSuggestionVisible) {
            animateSuggestions(mSuggestionsListView.getHeight(), 0);
        }
    }
    
    private void animateSuggestions(int from, int to) {
        mSuggestionVisible = to > 0;
        
        final ViewGroup.LayoutParams lp = mSuggestionsListView.getLayoutParams();
        if (to == 0 && lp.height == 0) return;
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (int) animation.getAnimatedValue();
                mSuggestionsListView.setLayoutParams(lp);
            }
        });
        animator.start();
    }
    
    /**
     * Set Suggest List OnItemClickListener
     *
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSuggestionsListView.setOnItemClickListener(listener);
    }
    
    /**
     * Set Adapter for suggestions list. Should implement Filterable.
     *
     */
    public void setAdapter(InstantSearchAdapter adapter) {
        mAdapter = adapter;
        mSuggestionsListView.setAdapter(adapter);
        startFilter(mSearchSrcTextView.getText());
    }
    
    public boolean isSearchOpen() {
        return mIsSearchOpen;
    }
    
    /**
     * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
     *
     * @param duration duration of the animation
     */
    public void setAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }
    
    /**
     * Open Search View. This will animate the showing of the view.
     */
    public void showSearch() {
        applyTheme();
        showSearch(true);
    }
    
    /**
     * Open Search View. If animate is true, Animate the showing of the view.
     *
     * @param animate true for animate
     */
    public void showSearch(boolean animate) {
        if (isSearchOpen()) {
            return;
        }
        
        //Request Focus
        mSearchSrcTextView.setText(null);
        mSearchSrcTextView.requestFocus();
        
        if (animate) {
            setVisibleWithAnimation();
        } else {
            mSearchLayout.setVisibility(VISIBLE);
        }
        if (mSearchViewListener != null) {
            mSearchViewListener.onSearchViewShown();
        }
        mIsSearchOpen = true;
    }
    
    private void setVisibleWithAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSearchLayout.setVisibility(View.VISIBLE);
            AnimationUtil.reveal(mSearchTopBar, null, true);
            
        } else {
            AnimationUtil.fadeInView(mSearchLayout, mAnimationDuration, null);
        }
    }
    
    /**
     * Close search view.
     */
    public void closeSearch() {
        if (!isSearchOpen()) {
            return;
        }
        
        mSearchSrcTextView.setText(null);
        dismissSuggestions();
        clearFocus();
    
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }
        
            @Override
            public boolean onAnimationEnd(View view) {
                mSearchLayout.setVisibility(GONE);
                if (mSearchViewListener != null) {
                    mSearchViewListener.onSearchViewClosed();
                }
                return false;
            }
        
            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };
    
        AnimationUtil.reveal(mSearchTopBar, animationListener, false);

        mIsSearchOpen = false;
    }
    
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryChangeListener = listener;
    }
    
    public void setOnSearchViewListener(SearchViewListener listener) {
        mSearchViewListener = listener;
    }
    
    public String getSearchEditText() {
        return String.valueOf(mSearchSrcTextView.getText());
    }
    
    @Override
    public void onFilterComplete(int count) {
        if (count > 0) {
            showSuggestions();
        } else {
            dismissSuggestions();
        }
    }
    
    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept focus if in the middle of clearing focus
        if (mClearingFocus) return false;
        // Check if SearchView is focusable.
        return isFocusable() && mSearchSrcTextView.requestFocus(direction, previouslyFocusedRect);
    }
    
    @Override
    public void clearFocus() {
        mClearingFocus = true;
        hideKeyboard(this);
        super.clearFocus();
        mSearchSrcTextView.clearFocus();
        mClearingFocus = false;
    }
    
    private void applyTheme() {
        if (!Aequorea.getCurrentTheme().equals(mTheme)) {
            
            mTheme = Aequorea.getCurrentTheme();
    
            int lineColor = ThemeHelper.getResourceColor(mContext, R.attr.line_color);
            int rootColor = ThemeHelper.getResourceColor(mContext, R.attr.root_color);
            int textColor = ThemeHelper.getResourceColor(mContext, R.attr.title_color);
            int hintColor = ThemeHelper.getResourceColor(mContext, R.attr.subtitle_color);
    
            mSearchTopBar.setBackgroundColor(rootColor);
            mSuggestionsListView.setBackgroundColor(rootColor);
            mDividerView.setBackgroundColor(lineColor);
            mSearchSrcTextView.setTextColor(textColor);
            mSearchSrcTextView.setHintTextColor(hintColor);
    
            if (Aequorea.isLightTheme()) {
                mBackBtn.setImageResource(R.drawable.ic_action_navigation_arrow_back);
                mEmptyBtn.setImageResource(R.drawable.ic_action_navigation_close);
            } else {
                mBackBtn.setImageResource(R.drawable.ic_action_navigation_arrow_back_inverted);
                mEmptyBtn.setImageResource(R.drawable.ic_action_navigation_close_inverted);
            }
        }
    }
    
    public interface OnQueryTextListener {
        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);
    }
    
    public interface SearchViewListener {
        void onSearchViewShown();
        
        void onSearchViewClosed();
    }
}