package nich.work.aequorea.ui.activitiy;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.service.CacheService;
import nich.work.aequorea.common.ui.activity.BaseActivity;
import nich.work.aequorea.common.ui.widget.MaterialSearchView;
import nich.work.aequorea.common.ui.widget.NestedScrollAppBarLayout;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.common.utils.SPUtils;
import nich.work.aequorea.common.utils.SnackbarUtils;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.data.MainPageModel;
import nich.work.aequorea.data.entity.Datum;
import nich.work.aequorea.data.entity.search.Content;
import nich.work.aequorea.data.entity.search.SearchDatum;
import nich.work.aequorea.presenter.MainPresenter;
import nich.work.aequorea.ui.adapter.InstantSearchAdapter;
import nich.work.aequorea.ui.view.HomeView;

public class MainActivity extends BaseActivity implements HomeView, NestedScrollAppBarLayout.OnNestedScrollListener, View.OnClickListener {
    
    private static final String TAG = MainActivity.class.getSimpleName();
    
    private MainPresenter mPresenter;
//    private MainArticleAdapter mAdapter;
    private MainPageModel mModel;
    private LinearLayoutManager mLinearLayoutManager;
    private MenuItem mSearchMenuItem;
    
    private long mClickTime;
    
    @BindView(R.id.rec)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.main_content)
    protected CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.appbar)
    protected NestedScrollAppBarLayout mAppBarLayout;
    @BindView(R.id.status_bar)
    protected StatusBarView mStatusBar;
    @BindView(R.id.layout_swipe_refresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.loading_progressbar)
    protected ProgressBar mProgressBar;
    @BindView(R.id.container_refresh)
    protected View mRefreshView;
    @BindView(R.id.search_view)
    protected MaterialSearchView mSearchView;
    @BindView(R.id.search_mask)
    protected View mSearchMask;
    
    @OnClick(R.id.container_refresh)
    protected void refresh() {
        hideRefreshLayout();
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.refresh();
    }
    
    @OnClick (R.id.search_mask)
    protected void hideMask() {
        if (mSearchMask.getVisibility() == View.VISIBLE) {
            mSearchView.closeSearch();
            mSearchMask.setVisibility(View.GONE);
        }
    }
    
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
            super.onScrolled(mRecyclerView, dx, dy);
            if (dy > Constants.AUTO_LOAD_TRIGGER) {
                autoLoad();
            }
        }
    };
    
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mModel.setRefreshing(true);
            mPresenter.refresh();
        }
    };
    
    private MaterialSearchView.SearchViewListener mSearchViewListener = new MaterialSearchView.SearchViewListener() {
        @Override
        public void onSearchViewShown() {
            mSearchMask.setVisibility(View.VISIBLE);
        }
    
        @Override
        public void onSearchViewClosed() {
            mSearchMask.setVisibility(View.GONE);
        }
    };
    
    private MaterialSearchView.OnQueryTextListener mQueryTestListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            mSearchView.closeSearch();
            IntentUtils.startSearchActivity(MainActivity.this, query);
            return false;
        }
    
        @Override
        public boolean onQueryTextChange(String newText) {
            mPresenter.getSearchList(newText);
            return false;
        }
    };
    
    private AdapterView.OnItemClickListener mOnSearchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            InstantSearchAdapter.InstantSearchBean bean = (InstantSearchAdapter.InstantSearchBean) view.getTag(R.string.app_name);
            if (bean != null) {
                IntentUtils.startArticleActivity(MainActivity.this, bean.id);
                mSearchView.closeSearch();
            }
        }
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
    
    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }
    
    @Override
    protected void initModel() {
        mModel = new MainPageModel();
    }
    
    @Override
    protected void initPresenter() {
        if (mPresenter == null) {
            mPresenter = new MainPresenter();
        }
        mPresenter.attach(this);
        
        // refresh
        mSwipeRefreshLayout.setRefreshing(true);
        mModel.setRefreshing(true);
        mPresenter.refresh();
    }
    
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        
        if (isInLightTheme()) {
            setStatusBarStyle(true);
            mStatusBar.setLightMask();
        } else {
            setStatusBarStyle(false);
            mStatusBar.setDarkMask();
        }
        
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        
//        mAdapter = new MainArticleAdapter(this, null);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mScrollListener);
        
        mAppBarLayout.setOnNestedListener(this);
        
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        
        mSearchView.setHint(getString(R.string.search_article));
        mSearchView.setOnQueryTextListener(mQueryTestListener);
        mSearchView.setOnItemClickListener(mOnSearchItemClickListener);
        mSearchView.setOnSearchViewListener(mSearchViewListener);
    }
    
    @Override
    public void onDataLoaded(List<Datum> data, boolean isRefresh) {
        hideRefreshLayout();
        
//        mAdapter.setArticleList(data, isRefresh);
        
        if (isRefresh) startOfflineCaching();
    }
    
    private void startOfflineCaching() {
        if (SPUtils.getBoolean(Constants.SP_OFFLINE_CACHE)) {
            if (NetworkUtils.isWiFiNetwork()) {
                Intent intent = new Intent(this, CacheService.class);
                startService(intent);
            }
        }
    }
    
    @Override
    public void onError(Throwable error) {
//        if (mAdapter.getItemCount() == 0) {
//            showRefreshLayout();
//        } else {
//            hideRefreshLayout();
//        }
        
        if (error != null) {
            SnackbarUtils.show(mRecyclerView, error.getMessage());
        }
    }
    
    @Override
    public void setRefreshing(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }
    
    @Override
    public void onNestedScrolling() {
        changeStatusBarStyle();
    }
    
    @Override
    public void onStopNestedScrolling() {
        changeStatusBarStyle();
    }
    
    private void changeStatusBarStyle() {
        // light status bar only show in light theme
        if (isInLightTheme()) {
            if (mAppBarLayout.getY() <= -mAppBarLayout.getMeasuredHeight()) {
                if (mStatusBar.isInitState()) {
                    setStatusBarStyle(false);
                    mStatusBar.setDarkMask();
                }
            } else {
                if (!mStatusBar.isInitState()) {
                    setStatusBarStyle(true);
                    mStatusBar.setLightMask();
                }
            }
        }
    }
    
    @Override
    public MainPageModel getModel() {
        return mModel;
    }
    
    private void autoLoad() {
        int lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        
//        int totalCount = mAdapter.getItemCount();
//        if (!mModel.isLoading() && !mModel.isRefreshing() && totalCount > 0 && lastVisibleItem >= totalCount - 6) {
//            mPresenter.loadData();
//        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                if (System.currentTimeMillis() - mClickTime < 200) {
                    int position = mLinearLayoutManager.findFirstVisibleItemPosition();
                    scrollToTop(position);
                }
                mClickTime = System.currentTimeMillis();
                break;
            default:
                break;
        }
    }
    
    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
            return;
        }
    
        int position = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (position > 0) {
            scrollToTop(position);
            return;
        }
    
        super.onBackPressed();
    }
    
    @Override
    public void onThemeSwitch() {
        setTheme(ThemeHelper.getThemeStyle(Aequorea.Companion.getCurrentTheme()));
        currentTheme = Aequorea.Companion.getCurrentTheme();
        
        setStatusBarStyle();
        setStatusBarMask();
        
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
        int primaryDarkColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimaryDark);
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
        int rootColor = ThemeHelper.getResourceColor(this, R.attr.root_color);
        int searchDrawable = ThemeHelper.getResourceId(this, R.attr.icon_search);
        int moreDrawable = ThemeHelper.getResourceId(this, R.attr.icon_more);
        
        mAppBarLayout.setBackgroundColor(primaryColor);
        mToolbar.setTitleTextColor(titleColor);
        mSearchMenuItem.setIcon(searchDrawable);
        mRecyclerView.setBackgroundColor(rootColor);
        mToolbar.setOverflowIcon(getDrawable(moreDrawable));
        
        // change color in recent apps
        getWindow().setBackgroundDrawable(new ColorDrawable(primaryDarkColor));
    }
    
    private void scrollToTop(int currentPosition) {
        if (currentPosition >= 10) {
            mRecyclerView.scrollToPosition(6);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                IntentUtils.startSettingsActivity(this);
                break;
            case R.id.action_search:
                mSearchView.showSearch();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onSearchResultLoaded(List<SearchDatum> dataList) {
        if (mSearchView.getSearchEditText().length() != 0 && dataList.size() != 0) {
            ArrayList<InstantSearchAdapter.InstantSearchBean> arrayList = new ArrayList<>();
    
            int size = dataList.size() > 5 ? 5 : dataList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    Content content = dataList.get(i).getContent();
                    arrayList.add(i, new InstantSearchAdapter.InstantSearchBean(content.getId(), content
                        .getTitle()));
                }
                InstantSearchAdapter mSearchAdapter = new InstantSearchAdapter(this, arrayList);
                mSearchView.setAdapter(mSearchAdapter);
        
                mSearchView.showSuggestions();
            }
        } else {
            mSearchView.setAdapter(null);
            mSearchView.dismissSuggestions();
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
    
    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }
    
    private void showRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
        mRefreshView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}
