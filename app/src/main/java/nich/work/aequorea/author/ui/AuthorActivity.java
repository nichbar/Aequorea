package nich.work.aequorea.author.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.author.model.AuthorModel;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.author.presenter.AuthorPresenter;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.ImageHelper;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.common.utils.SnackbarUtils;
import nich.work.aequorea.common.utils.ThemeHelper;

public class AuthorActivity extends BaseActivity implements AuthorView {
    
    private final static String TAG = AuthorActivity.class.getSimpleName();
    
    private AuthorModel mModel;
    private AuthorPresenter mPresenter;
    private AuthorAdapter mAdapter;
    
    private boolean mIsFirstPage = true;
    private boolean mIsAuthorDetailShowing = true;
    
    private static final int ANIMATE_SHOW = 1;
    private static final int ANIMATE_HIDE = 0;
    
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
            super.onScrolled(mRecyclerView, dx, dy);
            if (dy > Constants.AUTO_LOAD_TRIGGER) {
                autoLoad();
            }
        }
    };
    
    private AppBarLayout.OnOffsetChangedListener mOffsetListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset >= -15) {
                showAuthorDetail();
            } else {
                hideAuthorDetail();
            }
        }
    };
    
    @BindView(R.id.status_bar) StatusBarView mStatusBar;
    @BindView(R.id.tv_introduction) TextView mIntroductionTv;
    @BindView(R.id.tv_article_count) TextView mArticleCountTv;
    @BindView(R.id.iv_author) ImageView mAuthorIv;
    @BindView(R.id.container_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.rec) RecyclerView mRecyclerView;
    @BindView(R.id.appbar) AppBarLayout mAppBar;
    @BindView(R.id.loading_progressbar) ProgressBar mProgressBar;
    @BindView(R.id.container_refresh) View mRefreshView;
    
    @OnClick(R.id.container_refresh) void refresh() {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.load();
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        
        initModel();
        initView();
        initPresenter();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
    
    private void initModel() {
        mModel = new AuthorModel();
        
        mModel.setAuthorId((int) getIntent().getLongExtra(Constants.AUTHOR_ID, 0));
    }
    
    private void initView() {
        ButterKnife.bind(this);
    
        mToolbar.setNavigationIcon(R.drawable.icon_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        mCollapsingToolbarLayout.setTitle(" ");
        mCoordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        
        mAdapter = new AuthorAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(mScrollListener);
        mAppBar.addOnOffsetChangedListener(mOffsetListener);
        
        setStatusBarStyle();
    }
    
    private void setStatusBarStyle() {
        if (isInLightTheme()) {
            setStatusBarStyle(true);
            mStatusBar.setLightMask();
        } else {
            setStatusBarStyle(false);
            mStatusBar.setDarkMask();
        }
    }
    
    public AuthorModel getModel() {
        return mModel;
    }
    
    private void initPresenter() {
        mPresenter = new AuthorPresenter();
        mPresenter.attach(this);
        mPresenter.load();
    }
    
    @Override
    public void onDataLoaded(Author a) {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    
        if (mIsFirstPage) {
            mPresenter.findAuthorInfo(a);
            mIsFirstPage = false;
        }
    
        mAdapter.setArticleDataList(a.getData());
        mAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onError(Throwable error) {
        if (mAdapter.getItemCount() == 0) {
            mRefreshView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mRefreshView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
        if (error != null) {
            Log.d(TAG, error.getMessage());
            SnackbarUtils.show(mRecyclerView, error.getMessage());
        }
    }
    
    @Override
    public void onUpdateAuthorInfo(Author author) {
        mCollapsingToolbarLayout.setTitle(author.getName());
    
        ImageHelper.setImage(this, author.getAvatar(), mAuthorIv, true);
    
        String intro = author.getIntroduction();
        if (!TextUtils.isEmpty(intro) && " ".equals(intro)) {
        
            if (intro.contains("。")) {
                int position = intro.indexOf("。");
                intro = intro.substring(0, position);
            }
        
            mIntroductionTv.setText(intro);
        
        } else {
            mIntroductionTv.setText(R.string.default_introduction);
        }
    
        mArticleCountTv.setText(String.format(getString(R.string.article_count), author.getMeta()
            .getTotalCount()));
    }
    
    public void autoLoad() {
        int lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        
        int totalCount = mAdapter.getItemCount();
        if (!mModel.isLoading() && totalCount > 0 && lastVisibleItem >= totalCount - 3 && NetworkUtils.isNetworkAvailable()) {
            mPresenter.load();
        }
    }
    
    private void showAuthorDetail() {
        if (!mIsAuthorDetailShowing) {
            animateAuthorInfo(ANIMATE_SHOW);
            mIsAuthorDetailShowing = true;
        }
    }
    
    private void hideAuthorDetail() {
        if (mIsAuthorDetailShowing) {
            animateAuthorInfo(ANIMATE_HIDE);
            mIsAuthorDetailShowing = false;
        }
    }
    
    private void animateAuthorInfo(int style){
        mAuthorIv.animate()
            .alpha(style)
            .scaleX(style)
            .scaleY(style)
            .setInterpolator(new FastOutSlowInInterpolator())
            .start();
        
        mIntroductionTv.animate()
            .alpha(style)
            .setDuration(100)
            .start();
    
        mArticleCountTv.animate()
            .alpha(style)
            .setDuration(100)
            .start();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }
    
    @Override
    public void onThemeSwitch() {
        setTheme(ThemeHelper.getThemeStyle(Aequorea.getCurrentTheme()));
        currentTheme = Aequorea.getCurrentTheme();
    
        setStatusBarStyle();
        
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
        int rootColor = ThemeHelper.getResourceColor(this, R.attr.root_color);
        int subTitleColor = ThemeHelper.getResourceColor(this, R.attr.subtitle_color);
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
            
        mCoordinatorLayout.setBackgroundColor(primaryColor);
        mIntroductionTv.setTextColor(subTitleColor);
        mArticleCountTv.setTextColor(subTitleColor);
        mCollapsingToolbarLayout.setExpandedTitleColor(titleColor);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(titleColor);
        mCollapsingToolbarLayout.setBackgroundColor(primaryColor);
        mRecyclerView.setBackgroundColor(rootColor);
        
        // reload
        mAdapter = new AuthorAdapter(this, mAdapter.getArticleDataList());
        mRecyclerView.setAdapter(mAdapter);
    }
}
