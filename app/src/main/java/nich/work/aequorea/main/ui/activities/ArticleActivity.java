package nich.work.aequorea.main.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.rx.RxBus;
import nich.work.aequorea.common.rx.RxEvent;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.fragment.FontDialogFragment;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.ui.widget.SwipeBackCoordinatorLayout;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.FontHelper;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.common.utils.PermissionUtils;
import nich.work.aequorea.common.utils.SnackbarUtils;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.common.utils.ToastUtils;
import nich.work.aequorea.main.model.ArticleModel;
import nich.work.aequorea.main.model.article.Data;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.presenter.ArticlePresenter;
import nich.work.aequorea.main.ui.view.ArticleView;

public class ArticleActivity extends BaseActivity implements ArticleView {
    private static final String TAG = ArticleActivity.class.getSimpleName();
    
    private ArticlePresenter mPresenter;
    private ArticleModel mModel;
    
    
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
    private static final int ANIMATION_DURATION_STATUS_BAR = 200;
    private static final int ANIMATION_DURATION_THEME_SWITCHING = 350;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    
    private int mScrollUpEdge;
    private int mScrollDownEdge;
    
    private boolean mIsStatusBarInLowProfileMode = false;
    private boolean mThemeSwitchIsRunning = false;


    @BindView(R.id.tv_article_content) TextView mContentTv;
    @BindView(R.id.tv_author) TextView mAuthorTv;
    @BindView(R.id.tv_date) TextView mDateTv;
    @BindView(R.id.tv_title) TextView mTitleTv;
    @BindView(R.id.tv_tag) TextView mTagTv;
    @BindView(R.id.tv_article_recommend) TextView mRecommendTitleTv;
    @BindView(R.id.view_divider) View mDividerView;
    @BindView(R.id.loading_progressbar) ProgressBar mProgressBar;
    @BindView(R.id.container_refresh) View mRefreshView;
    @BindView(R.id.layout_swipe_back_article) SwipeBackCoordinatorLayout mSwipeBackLayout;
    @BindView(R.id.layout_container_article) ViewGroup mContainer;
    @BindView(R.id.scrollview) NestedScrollView mScrollView;
    @BindView(R.id.status_bar) StatusBarView mStatusBar;
    @BindView(R.id.container_recommendation) LinearLayout mRecommendationContainer;
    @BindView(R.id.container_recommendation_sub) LinearLayout mRecommendationSubContainer;
    @BindView(R.id.container_option) ViewGroup mOptionContainer;
    @BindView(R.id.iv_theme) ImageView mThemeIv;
    @BindView(R.id.iv_font) ImageView mFontIv;
    @BindView(R.id.iv_screenshot) ImageView mScreenshotIv;
    @BindView(R.id.iv_share) ImageView mShareIv;
    
    @OnClick(R.id.tv_author)
    void gotoAuthorPage() {
        IntentUtils.startAuthorActivity(this, mModel.getData().getAuthors().get(0).getId());
    }
    
    @OnLongClick(R.id.iv_theme)
    boolean showThemeHint() {
        if (isInLightTheme()) {
            ToastUtils.showShortToast(getString(R.string.switch_to_dark_theme));
        } else {
            ToastUtils.showShortToast(getString(R.string.switch_to_light_theme));
        }
        return true;
    }
    
    @OnLongClick(R.id.iv_font)
    boolean showFontHint() {
        ToastUtils.showShortToast(getString(R.string.modify_font));
        return true;
    }
    
    @OnLongClick(R.id.iv_screenshot)
    boolean saveScreenShot() {
        if (mModel.getData() != null) {
            saveArticleToStorageWithPermissionCheck();
        }
        return true;
    }
    
    @OnLongClick(R.id.iv_share)
    boolean showShareHint() {
        ToastUtils.showShortToast(getString(R.string.share));
        return true;
    }
    
    @OnClick(R.id.iv_share)
    void shareArticle() {
        if (mModel.getData() != null) {
            String title = mModel.getData().getTitle();
            String url = mModel.getData().getShareUrl();
            
            IntentUtils.shareText(this, title, "[" + title + "]" + url, getString(R.string.share_to));
        }
    }
    
    @OnClick(R.id.iv_screenshot)
    void showScreenshotHint() {
        ToastUtils.showShortToast(getString(R.string.long_click_to_save_this_article));
    }
    
    @OnClick(R.id.iv_font)
    void modifyFont() {
        FontDialogFragment dialogFragment = new FontDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "");
    }
    
    @OnClick(R.id.iv_theme)
    void switchTheme() {
        if (mThemeSwitchIsRunning) {
            return;
        }
        
        if (isInLightTheme()) {
            mTheme = Constants.THEME_DARK;
        } else {
            mTheme = Constants.THEME_LIGHT;
        }
        ThemeHelper.setTheme(mTheme);
        setTheme(ThemeHelper.getThemeStyle(mTheme));
        startThemeSwitchingAnimation();
        
        RxBus.getInstance().post(RxEvent.EVENT_TYPE_CHANGE_THEME, null);
    }
    
    @OnClick(R.id.container_refresh) void refresh() {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.load(mModel.getId());
    }
    
    private SwipeBackCoordinatorLayout.OnSwipeListener mSwipeBackListener = new SwipeBackCoordinatorLayout.OnSwipeListener() {
        @Override
        public boolean canSwipeBack(int dir) {
            return true;
        }

        @Override
        public void onSwipeProcess(float percent) {
            mContainer.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
        }

        @Override
        public void onSwipeFinish(int dir) {
            finish();
            switch (dir) {
                case SwipeBackCoordinatorLayout.UP_DIR:
                    overridePendingTransition(0, R.anim.activity_slide_out_top);
                    break;

                case SwipeBackCoordinatorLayout.DOWN_DIR:
                    overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                    break;
            }
        }
    };
    
    private NestedScrollView.OnScrollChangeListener mScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY - oldScrollY > mScrollDownEdge) {
                setLowProfileMode();
            } else if (scrollY - oldScrollY < -mScrollUpEdge) {
                setStandardMode();
            }
        }
    };
    
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float oldY;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    oldY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (event.getY() - oldY  == 0) {
                        toggleShowStatus();
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        
        initModel();
        initView();
        initPresenter();
    }

    private void initModel() {
        mModel = new ArticleModel();
        mModel.setId(getIntent().getLongExtra(Constants.ARTICLE_ID, 0));
    }

    private void initView() {
        ButterKnife.bind(this);
    
        setStatusBarStyle();
        setStatusBarMask();

        mSwipeBackLayout.setOnSwipeListener(mSwipeBackListener);
        
        mContentTv.setOnTouchListener(mTouchListener);
        
        mScrollView.setOnScrollChangeListener(mScrollChangeListener);
        
        mScrollDownEdge = dp2px(5);
        mScrollUpEdge = dp2px(10);
    }
    
    private void initPresenter() {
        mPresenter = new ArticlePresenter();
        mPresenter.attach(this);
        mPresenter.load(mModel.getId());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        setStandardMode();
        // restyle content
        setContentFontSize(FontHelper.getFontSize());
        setContentLineSpacing(FontHelper.getFontSpacing());
        setContentFontFamily(FontHelper.getFontFamily());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }
    
    public void setStandardMode() {
        if (mIsStatusBarInLowProfileMode) {
            setStatusBarStyle();
            mStatusBar.animate().scaleY(1).setDuration(ANIMATION_DURATION_STATUS_BAR);
            mIsStatusBarInLowProfileMode = false;
            showOptions();
        }
    }
    
    public void setLowProfileMode() {
        if (!mIsStatusBarInLowProfileMode) {
            setStatusBarInLowProfileMode(isInLightTheme());
            mStatusBar.animate().scaleY(0).setDuration(ANIMATION_DURATION_STATUS_BAR);
            mIsStatusBarInLowProfileMode = true;
            hideOptions();
        }
    }
    
    private void showOptions() {
        mOptionContainer.setVisibility(View.VISIBLE);
        mOptionContainer.animate()
            .alpha(1);
    }
    
    private void hideOptions(){
        mOptionContainer.animate()
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
            
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mIsStatusBarInLowProfileMode) {
                        mOptionContainer.setVisibility(View.GONE);
                    }
                }
            
                @Override
                public void onAnimationCancel(Animator animation) {}
            
                @Override
                public void onAnimationRepeat(Animator animation) {}
            })
            .alpha(0);
    }
    
    private void toggleShowStatus() {
        if (mIsStatusBarInLowProfileMode) {
            setStandardMode();
        } else {
            setLowProfileMode();
        }
    }
    
    @Override
    public void onArticleLoaded(Data article) {
        mModel.setData(article);
    
        mProgressBar.setVisibility(View.GONE);
        mRefreshView.setVisibility(View.GONE);
    
        mTitleTv.setText(article.getTitle());
    
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        String targetDateString = null;
        try {
            Date date = sourceDateFormat.parse(article.getDisplayTime());
            targetDateString = targetDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
        mDateTv.setText(targetDateString);
    
        if (article.getAuthors().size() != 0 && article.getAuthors().get(0) != null) {
            mAuthorTv.setText(article.getAuthors().get(0).getName());
        } else {
            mAuthorTv.setText(R.string.default_author);
        }
    
        if (article.getTopics() != null && article.getTopics().size() != 0 && article.getTopics().get(0) != null) {
            mTagTv.setText(String.format("# %s", article.getTopics().get(0).getName()));
        } else {
            mTagTv.setVisibility(View.GONE);
        }
    
        RichText.from(article.getContent()).into(mContentTv);
    
        // load recommendation after rendering the context
        mPresenter.loadRecommendedArticles(mModel.getId());
    }
    
    @Override
    public void onArticleError(Throwable t) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshView.setVisibility(View.VISIBLE);
    }
    
    public void onRecommendationLoaded(List<Datum> data) {
        mModel.setRecommendDataList(data);
        mRecommendationContainer.setVisibility(View.VISIBLE);
        for (Datum d : data) {
            mRecommendationSubContainer.addView(createRecommendArticle(d));
        }
    }
    
    @SuppressLint("InflateParams")
    private View createRecommendArticle(final Datum datum) {
        View view = View.inflate(this, R.layout.piece_recommend_article, null);
        
        TextView title = (TextView) view.findViewById(R.id.tv_article_recommend_title);
        title.setText(datum.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startArticleActivity(ArticleActivity.this, datum.getId());
            }
        });
        
        return view;
    }
    
    public void onRecommendationError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }
    
    private void startThemeSwitchingAnimation() {
        int location[] = new int[2];
        mThemeIv.getLocationOnScreen(location);
    
        Animator animator = ViewAnimationUtils.createCircularReveal(mScrollView, location[0] + mThemeIv.getMeasuredWidth() / 2, location[1], 0f, getResources()
            .getDisplayMetrics().heightPixels);
    
        animator.setDuration(ANIMATION_DURATION_THEME_SWITCHING);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mThemeSwitchIsRunning = true;
            }
    
            @Override
            public void onAnimationEnd(Animator animator) {
                mSwipeBackLayout.setBackgroundColor(ThemeHelper.getResourceColor(ArticleActivity.this, R.attr.root_color));
                mThemeSwitchIsRunning = false;
            }
    
            @Override
            public void onAnimationCancel(Animator animator) {}
    
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
    
    @Override
    public void onThemeSwitch() {
        setTheme(ThemeHelper.getThemeStyle(Aequorea.getCurrentTheme()));
        mTheme = Aequorea.getCurrentTheme();
        
        setStatusBarStyle();
        setStatusBarMask();
        
        int lineColor = ThemeHelper.getResourceColor(this, R.attr.line_color);
        int rootColor = ThemeHelper.getResourceColor(this, R.attr.root_color);
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
        int accentColor = ThemeHelper.getResourceColor(this, R.attr.colorAccent);
        int subtitleColor = ThemeHelper.getResourceColor(this, R.attr.subtitle_color);
        int contentColor = ThemeHelper.getResourceColor(this, R.attr.content_color);
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
    
        int iconShareId = ThemeHelper.getResourceId(this, R.attr.icon_share);
        int iconFontId = ThemeHelper.getResourceId(this, R.attr.icon_font);
        int iconThemeId = ThemeHelper.getResourceId(this, R.attr.icon_theme);
        int iconScreenshotId = ThemeHelper.getResourceId(this, R.attr.icon_screenshot);
    
        mScrollView.setBackgroundColor(rootColor);
        mTagTv.setTextColor(accentColor);
        mTitleTv.setTextColor(titleColor);
        mAuthorTv.setTextColor(subtitleColor);
        mDateTv.setTextColor(subtitleColor);
        mContentTv.setTextColor(contentColor);
        mDividerView.setBackgroundColor(lineColor);
        mRecommendTitleTv.setTextColor(titleColor);
    
        mShareIv.setImageResource(iconShareId);
        mFontIv.setImageResource(iconFontId);
        mThemeIv.setImageResource(iconThemeId);
        mScreenshotIv.setImageResource(iconScreenshotId);
    
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.TRANSPARENT, primaryColor});
        mOptionContainer.setBackground(drawable);
        
        // recreate related articles
        mRecommendationSubContainer.removeAllViews();
        if (mModel.getRecommendDataList() != null) {
            onRecommendationLoaded(mModel.getRecommendDataList());
        }
    }
    
    private void setStatusBarStyle() {
        if (isInLightTheme()) {
            setStatusBarStyle(true);
        } else {
            setStatusBarStyle(false);
        }
    }
    
    private void setStatusBarMask() {
        if (isInLightTheme()) {
            mStatusBar.setLightMask();
        } else {
            mStatusBar.setDarkMask();
        }
    }
    
    public void setContentFontSize(int i) {
        mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, i);
    }
    
    public void setContentLineSpacing(int i) {
        mContentTv.setLineSpacing(dp2px(i), 1);
    }
    
    public void setContentFontFamily(String fontFamily) {
        switch (fontFamily) {
            case FontHelper.SANS_SERIF:
                mContentTv.setTypeface(Typeface.SANS_SERIF);
                break;
            case FontHelper.SERIF:
                mContentTv.setTypeface(Typeface.SERIF);
                break;
            case FontHelper.MONOSPACE:
                mContentTv.setTypeface(Typeface.MONOSPACE);
                break;
        }
    }
    
    private void saveArticleToStorageWithPermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        
        saveArticleToStorage();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    saveArticleToStorage();
                } else {
                    ToastUtils.showShortToast(getString(R.string.require_write_stroage_permission));
                }
        }
    }
    
    private void saveArticleToStorage() {
        int rootColor = ThemeHelper.getResourceColor(this, R.attr.root_color);
        Bitmap bitmap = DisplayUtils.shotNestedScrollView(mScrollView, rootColor);
        mPresenter.startSaveArticleToStorage(bitmap, mModel.getData().getTitle());
    }
    
    @Override
    public void onArticleSavedAsPictureSucceeded(final String path) {
        hideOptions();
        
        Snackbar snackbar = SnackbarUtils.getSnackbar(mSwipeBackLayout, getString(R.string.screenshot_saved_to_stroage), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.open_image, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.openImageFromStorage(ArticleActivity.this, path);
            }
        });
        snackbar.show();
    }
    
    @Override
    public void onArticleSavedAsPictureFailed(Throwable t) {
        ToastUtils.showShortToast(getString(R.string.save_failed) + t.toString());
    }
}
