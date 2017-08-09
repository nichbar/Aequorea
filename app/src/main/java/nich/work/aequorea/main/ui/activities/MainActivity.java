package nich.work.aequorea.main.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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

    private MainPagePresenter presenter;
    private MainArticleAdapter adapter;
    private MainPageModel model;
    private LinearLayoutManager linearLayoutManager;

    private long clickTime;

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            autoLoad(dy);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            model.setRefreshing(true);
            presenter.refresh();
        }
    };

    @BindView(R.id.rec) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_content) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar) NestedScrollAppBarLayout appBarLayout;
    @BindView(R.id.status_bar) StatusBarView statusBar;
    @BindView(R.id.layout_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initModel();
        initView();
        initPresenter();
    }

    private void initModel() {
        model = new MainPageModel();
    }

    private void initPresenter() {
        if (presenter == null)
            presenter = new MainPagePresenter();
        presenter.attach(this);
    }

    private void initView() {
        ButterKnife.bind(this);

        setStatusBarStyle(true);
        statusBar.setLightMask();

        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setOnClickListener(this);

        adapter = new MainArticleAdapter(this, null);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);

        appBarLayout.setOnNestedListener(this);

        swipeRefreshLayout.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    public void onUpdateAdapter(List<Datum> data, boolean isRefresh) {
        List<Datum> articleList = adapter.getArticleList();

        if (articleList == null || isRefresh) {
            adapter.setArticleList(data);
        } else {
            for (Datum d : data) {
                if (!articleList.contains(d)) {
                    articleList.add(d);
                }
            }
            adapter.setArticleList(articleList);
        }
        adapter.notifyDataSetChanged();
    }

    public void onError(Throwable error) {
        SnackBarUtils.show(recyclerView, getString(R.string.network_error) + error.getMessage());
    }

    public void setRefreshing(boolean isRefreshing){
        swipeRefreshLayout.setRefreshing(isRefreshing);
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
        if (appBarLayout.getY() <= -appBarLayout.getMeasuredHeight()) {
            if (statusBar.isInitState()) {
                setStatusBarStyle(false);
                statusBar.setDarkMask();
            }
        } else {
            if (!statusBar.isInitState()) {
                setStatusBarStyle(true);
                statusBar.setLightMask();
            }
        }
    }

    public MainPageModel getModel(){
        return model;
    }

    private void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPosition();

        int totalCount = adapter.getItemCount();
        if (!model.isLoading() && !model.isRefreshing() && totalCount > 0 && dy > 0 && lastVisibleItem >= totalCount - 2) {
            model.setLoading(true);
            presenter.loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar:
                if (System.currentTimeMillis() - clickTime < 200){
                    scrollToTop();
                }
                clickTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int position = linearLayoutManager.findFirstVisibleItemPosition();
            if (position != 0) {
                recyclerView.smoothScrollToPosition(0);
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void scrollToTop() {
        recyclerView.smoothScrollToPosition(0);
    }
}
