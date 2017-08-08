package nich.work.aequorea.main.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.NestedScrollAppBarLayout;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.utils.SnackBarUtils;
import nich.work.aequorea.main.model.MainPageModel;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.presenters.MainPagePresenter;
import nich.work.aequorea.main.ui.adapters.MainArticleAdapter;

public class MainActivity extends BaseActivity implements NestedScrollAppBarLayout.OnNestedScrollListener, View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private MainPagePresenter mPresenter;
    private MainArticleAdapter mAdapter;
    private MainPageModel mModel;

    private long mClickTime;

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            autoLoad(dy);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mModel.setRefreshing(true);
            mPresenter.refresh();
        }
    };

    @BindView(R.id.rec) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.appbar) NestedScrollAppBarLayout mAppBarLayout;
    @BindView(R.id.status_bar) StatusBarView mStatusBar;
    @BindView(R.id.layout_swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initModel();
        initView();
        initPresenter();
    }

    private void initModel() {
        mModel = new MainPageModel();
    }

    private void initPresenter() {
        if (mPresenter == null)
            mPresenter = new MainPagePresenter();
        mPresenter.attach(this);
    }

    private void initView() {
        ButterKnife.bind(this);

        setStatusBarStyle(true);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setOnClickListener(this);

        mAdapter = new MainArticleAdapter(this, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mScrollListener);

        mAppBarLayout.setOnNestedListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    public void onUpdateAdapter(List<Datum> data, boolean isRefresh) {
        List<Datum> articleList = mAdapter.getArticleList();

        if (articleList == null || isRefresh) {
            mAdapter.setArticleList(data);
        } else {
            for (Datum d : data) {
                if (!articleList.contains(d)) {
                    articleList.add(d);
                }
            }
            mAdapter.setArticleList(articleList);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void onError(Throwable error) {
        SnackBarUtils.show(mRecyclerView, getString(R.string.network_error) + error.getMessage());
    }

    public void setRefreshing(boolean isRefreshing){
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
        if (mAppBarLayout.getY() <= -mAppBarLayout.getMeasuredHeight()) {
            if (mStatusBar.isOriginalStyle()) {
                setStatusBarStyle(false);
                mStatusBar.setDarkMask();
            }
        } else {
            if (!mStatusBar.isOriginalStyle()) {
                setStatusBarStyle(true);
                mStatusBar.setLightMask();
            }
        }
    }

    public MainPageModel getModel(){
        return mModel;
    }

    private void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findLastVisibleItemPosition();

        int totalCount = mAdapter.getItemCount();
        if (!mModel.isLoading() && !mModel.isRefreshing() && totalCount > 0 && dy > 0 && lastVisibleItem >= totalCount - 2) {
            mModel.setLoading(true);
            mPresenter.loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar:
                if (System.currentTimeMillis() - mClickTime < 200){
                    scrollToTop();
                }
                mClickTime = System.currentTimeMillis();
        }
    }

    private void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }
}
