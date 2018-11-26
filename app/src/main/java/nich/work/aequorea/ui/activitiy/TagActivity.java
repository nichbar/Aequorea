package nich.work.aequorea.ui.activitiy;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.LinearLayoutManager;

import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.data.SimpleArticleListModel;
import nich.work.aequorea.data.entity.Data;
import nich.work.aequorea.presenter.TagPresenter;
import nich.work.aequorea.ui.adapter.SimpleArticleListAdapter;

public class TagActivity extends SimpleArticleListActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_tag;
    }
    
    @Override
    protected void initModel() {
        mModel = new SimpleArticleListModel();
        mModel.setId((int) getIntent().getLongExtra(Constants.TAG_ID, 0));
        mModel.setTitle(getIntent().getStringExtra(Constants.TAG));
    }
    
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        
        mToolbar.setNavigationIcon(R.drawable.icon_ab_back_material);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mToolbar.setTitle(mModel.getTitle());
        mCoordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        
        mAdapter = new SimpleArticleListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(mScrollListener);
        
        setStatusBarStyle();
    }
    
    @Override
    protected void initPresenter() {
        mPresenter = new TagPresenter();
        mPresenter.attach(this);
        mPresenter.load();
    }
    
    @Override
    public void onDataLoaded(Data a) {
        super.onDataLoaded(a);
    }
    
    @Override
    public void onThemeSwitch() {
        super.onThemeSwitch();
        
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
        
        mToolbar.setTitleTextColor(titleColor);
        mToolbar.setBackgroundColor(primaryColor);
    
        Drawable drawable = getDrawable(R.drawable.icon_ab_back_material);
        if (drawable != null) {
            drawable.setTint(ThemeHelper.getResourceColor(this, R.attr.colorControlNormal));
        }
        mToolbar.setNavigationIcon(drawable);
    }
}
