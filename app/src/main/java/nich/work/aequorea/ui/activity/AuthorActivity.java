package nich.work.aequorea.ui.activity;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.ImageHelper;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.data.SimpleArticleListModel;
import nich.work.aequorea.data.entity.Author;
import nich.work.aequorea.data.entity.Data;
import nich.work.aequorea.presenter.AuthorPresenter;
import nich.work.aequorea.ui.adapter.SimpleArticleListAdapter;

public class AuthorActivity extends SimpleArticleListActivity {
    
    private boolean mIsAuthorDetailShowing = true;
    
    private static final int ANIMATE_SHOW = 1;
    private static final int ANIMATE_HIDE = 0;
    
    private Author mAuthor;
    
    @BindView(R.id.tv_introduction)
    protected TextView mIntroductionTv;
    @BindView(R.id.tv_article_count)
    protected TextView mArticleCountTv;
    @BindView(R.id.iv_author)
    protected ImageView mAuthorIv;
    @BindView(R.id.container_collapsing_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appbar)
    protected AppBarLayout mAppBar;
    
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
    
    @Override
    protected int getContentViewId() {
        return R.layout.activity_author;
    }
    
    @Override
    protected void initModel() {
        mModel = new SimpleArticleListModel();
        
        mModel.setId((int) getIntent().getLongExtra(Constants.AUTHOR_ID, 0));
        
        if (getIntent().getExtras() != null) {
            mAuthor = (Author) getIntent().getExtras().get(Constants.AUTHOR);
        }
    }
    
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        
        mToolbar.setNavigationIcon(R.drawable.icon_ab_back_material);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mCollapsingToolbarLayout.setTitle(" ");
        mCoordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        
        mAdapter = new SimpleArticleListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(mScrollListener);
        mAppBar.addOnOffsetChangedListener(mOffsetListener);
        
        if (mAuthor != null) {
            updateAuthorImg(mAuthor);
        }
        
        setStatusBarStyle();
    }
    
    @Override
    protected void initPresenter() {
        mPresenter = new AuthorPresenter();
        mPresenter.attach(this);
        mPresenter.load();
    }
    
    @Override
    public void onDataLoaded(Data a) {
        super.onDataLoaded(a);
        
        if (mIsFirstPage) {
            ((AuthorPresenter) mPresenter).findAuthorInfo(a);
            mIsFirstPage = false;
        }
    }
    
    private void updateAuthorImg(Author author) {
        mCollapsingToolbarLayout.setTitle(author.getName());
    
        ImageHelper.loadImage(this, author.getAvatar(), mAuthorIv, true);
    }
    
    @Override
    public void onUpdateAuthorInfo(Author author) {
        if (mAuthor == null) {
            updateAuthorImg(author);
        }
        
        String intro = author.getIntroduction();
        if (!TextUtils.isEmpty(intro) && !" ".equals(intro)) {
            if (intro.contains("。")) {
                int position = intro.indexOf("。");
                intro = intro.substring(0, position);
            }
            mIntroductionTv.setText(intro);
        } else {
            mIntroductionTv.setText(R.string.default_introduction);
        }
        
        mArticleCountTv.setText(String.format(getString(R.string.article_count), author.getMeta().getTotalCount()));
    }
    
    @Override
    public void onThemeSwitch() {
        super.onThemeSwitch();
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
        int subTitleColor = ThemeHelper.getResourceColor(this, R.attr.subtitle_color);
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
        
        mIntroductionTv.setTextColor(subTitleColor);
        mArticleCountTv.setTextColor(subTitleColor);
        mCollapsingToolbarLayout.setExpandedTitleColor(titleColor);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(titleColor);
        mCollapsingToolbarLayout.setBackgroundColor(primaryColor);
    
        Drawable drawable = getDrawable(R.drawable.icon_ab_back_material);
        if (drawable != null) {
            drawable.setTint(ThemeHelper.getResourceColor(this, R.attr.colorControlNormal));
        }
        mToolbar.setNavigationIcon(drawable);
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
    
    private void animateAuthorInfo(int style) {
        mAuthorIv.animate()
            .alpha(style)
            .scaleX(style)
            .scaleY(style)
            .setInterpolator(new FastOutSlowInInterpolator())
            .start();
        
        mIntroductionTv.animate().alpha(style).setDuration(100).start();
        
        mArticleCountTv.animate().alpha(style).setDuration(100).start();
    }
}
