package nich.work.aequorea.ui.activitiy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.OnClick;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activity.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.common.utils.SnackbarUtils;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.model.SimpleArticleListModel;
import nich.work.aequorea.model.entity.Author;
import nich.work.aequorea.model.entity.Data;
import nich.work.aequorea.presenter.SimpleArticleListPresenter;
import nich.work.aequorea.ui.adapter.SimpleArticleListAdapter;
import nich.work.aequorea.ui.view.SimpleArticleListView;

public abstract class SimpleArticleListActivity extends BaseActivity implements SimpleArticleListView {
    
    private final static String TAG = SimpleArticleListActivity.class.getSimpleName();
    
    protected SimpleArticleListModel mModel;
    protected SimpleArticleListPresenter mPresenter;
    protected SimpleArticleListAdapter mAdapter;
    
    protected boolean mIsFirstPage = true;
    
    @BindView(R.id.status_bar)
    protected StatusBarView mStatusBar;
    @BindView(R.id.main_content)
    protected CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.rec)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.loading_progressbar)
    protected ProgressBar mProgressBar;
    @BindView(R.id.container_refresh)
    protected View mRefreshView;
    @BindView(R.id.container_no_data)
    protected View mNoDateView;
    
    protected RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
            super.onScrolled(mRecyclerView, dx, dy);
            if (dy > Constants.AUTO_LOAD_TRIGGER) {
                autoLoad();
            }
        }
    };
    
    @OnClick(R.id.container_refresh)
    protected void refresh() {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.load();
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
    
    protected void setStatusBarStyle() {
        if (isInLightTheme()) {
            setStatusBarStyle(true);
            mStatusBar.setLightMask();
        } else {
            setStatusBarStyle(false);
            mStatusBar.setDarkMask();
        }
    }
    
    public SimpleArticleListModel getModel() {
        return mModel;
    }
    
    @Override
    public void onDataLoaded(Data a) {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        
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
            error.printStackTrace();
        }
    }
    
    @Override
    public void onUpdateAuthorInfo(Author author) {
        // do nothing
    }
    
    @Override
    public void onNoData() {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mNoDateView.setVisibility(View.VISIBLE);
    }
    
    public void autoLoad() {
        int lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        
        int totalCount = mAdapter.getItemCount();
        if (!mModel.isLoading() && totalCount > 0 && lastVisibleItem >= totalCount - 3 && NetworkUtils
            .isNetworkAvailable()) {
            mPresenter.load();
        }
    }
    
    @Override
    public void onThemeSwitch() {
        setTheme(ThemeHelper.getThemeStyle(Aequorea.getCurrentTheme()));
        currentTheme = Aequorea.getCurrentTheme();
        
        setStatusBarStyle();
        
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
        int rootColor = ThemeHelper.getResourceColor(this, R.attr.root_color);
        
        mCoordinatorLayout.setBackgroundColor(primaryColor);
        mRecyclerView.setBackgroundColor(rootColor);
        
        // reload
        mAdapter = new SimpleArticleListAdapter(this, mAdapter.getArticleDataList());
        mRecyclerView.setAdapter(mAdapter);
    }
}
