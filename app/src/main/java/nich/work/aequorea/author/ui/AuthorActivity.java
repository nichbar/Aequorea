package nich.work.aequorea.author.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    
    private AuthorModel mModel;
    private AuthorPresenter mPresenter;
    private AuthorAdapter mAdapter;
    
    private boolean mIsFirstPage = true;
    
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
            super.onScrolled(mRecyclerView, dx, dy);
            autoLoad(dy);
        }
    };

    @BindView(R.id.status_bar) StatusBarView mStatusBar;
    @BindView(R.id.tv_introduction) TextView mIntroductionTv;
    @BindView(R.id.tv_article_count) TextView mArticleCount;
    @BindView(R.id.iv_author) ImageView mAuthorIv;
    @BindView(R.id.container_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.rec) RecyclerView mRecyclerView;
    
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
        
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        mCollapsingToolbarLayout.setTitle(" ");
        mCoordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        
        mAdapter = new AuthorAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(mScrollListener);
    
        setStatusBarStyle(true);
        mStatusBar.setLightMask();
    }
    
    public AuthorModel getModel() {
        return mModel;
    }
    
    private void initPresenter() {
        mPresenter = new AuthorPresenter();
        mPresenter.attach(this);
        
        mPresenter.load(mModel.getAuthorId());
    }
    
    public void onUpdate(Author a) {
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
    
    public void onError(Throwable mThrowable) {
        SnackBarUtils.show(mRecyclerView, getString(R.string.network_error) + mThrowable.getMessage());
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
        
        mArticleCount.setText(String.format(getString(R.string.article_count), author.getMeta()
            .getTotalCount()));
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }
}
