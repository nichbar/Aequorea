package nich.work.aequorea.main.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.NestedScrollAppBarLayout;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.presenters.MainPagePresenter;
import nich.work.aequorea.main.ui.adapters.MainPageArticleAdapter;

public class MainActivity extends BaseActivity implements NestedScrollAppBarLayout.OnNestedScrollListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private MainPagePresenter mPresenter;
    private MainPageArticleAdapter mAdapter;

    @BindView(R.id.rec) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.appbar) NestedScrollAppBarLayout mAppBarLayout;
    @BindView(R.id.status_bar) StatusBarView mStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initModel();
        initView();
        initPresenter();
    }

    private void initModel() {

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
        mAdapter = new MainPageArticleAdapter(this, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mAppBarLayout.setOnNestedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    public void onUpdateAdapter(List<Datum> data) {
        mAdapter.mArticleList = data;
        mAdapter.notifyDataSetChanged();
    }

    public void onError(Throwable error){
        Snackbar.make(mRecyclerView, "网络异常", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNestedScrolling() {
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
}
