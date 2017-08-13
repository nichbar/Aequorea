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

import com.bumptech.glide.Glide;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.R;
import nich.work.aequorea.author.model.AuthorModel;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.author.model.entities.Datum;
import nich.work.aequorea.author.presenter.AuthorPresenter;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.ui.widget.glide.CircleTransformation;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.SnackBarUtils;

public class AuthorActivity extends BaseActivity {
    
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
            autoLoad(dy);
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
        mPresenter.load(mModel.getAuthorId());
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
    
        setStatusBarStyle(true);
        mStatusBar.setLightMask();
        
        mAppBar.addOnOffsetChangedListener(mOffsetListener);
    }
    
    public AuthorModel getModel() {
        return mModel;
    }
    
    private void initPresenter() {
        mPresenter = new AuthorPresenter(this);
        mPresenter.load(mModel.getAuthorId());
    }
    
    public void onUpdate(Author a) {
    
        // filter the content that can not display at this very moment
        // TODO support this kind of things
        a.setData(filter(a.getData()));
        
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        
        List<Datum> articleList = mAdapter.getArticleDataList();
        
        if (mIsFirstPage) {
            mPresenter.findAuthorInfo(a);
            mIsFirstPage = false;
        }
        
        if (mAdapter.getArticleDataList() == null) {
            // TODO filter article that only substituted can read
            mAdapter.setArticleDataList(a.getData());
        } else {
            for (Datum d : a.getData()) {
                if (!articleList.contains(d)) {
                    articleList.add(d);
                }
            }
            mAdapter.setArticleDataList(articleList);
        }
        mAdapter.notifyDataSetChanged();
    }
    
    private List<Datum> filter(List<Datum> data) {
        Iterator<Datum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Datum d = iterator.next();
            if (d.getArticleType().equals(Constants.ARTICLE_TYPE_THEME) || d.getArticleType()
                .equals(Constants.ARTICLE_TYPE_MAGAZINE) || d.getArticleType()
                .equals(Constants.ARTICLE_TYPE_MAGAZINE_V2)) {
                iterator.remove();
            }
        }
        return data;
    }
    
    public void onError(Throwable error) {
        mRefreshView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        if (error != null) {
            Log.d(TAG, error.getMessage());
        }
        SnackBarUtils.show(mRecyclerView, getString(R.string.network_error));
    }
    
    public void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        
        int totalCount = mAdapter.getItemCount();
        if (!mModel.isLoading() && totalCount > 0 && dy > 0 && lastVisibleItem >= totalCount - 3) {
            mModel.setLoading(true);
            mPresenter.load(mModel.getAuthorId());
        }
    }
    
    public void updateAuthorInfo(Author author) {
        mCollapsingToolbarLayout.setTitle(author.getName());
        
        Glide.with(this)
            .load(author.getAvatar())
            .transform(new CircleTransformation(this))
            .into(mAuthorIv);
        
        String intro = author.getIntroduction();
        if (!TextUtils.isEmpty(intro) && !intro.equals(" ")) {
            
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
}
