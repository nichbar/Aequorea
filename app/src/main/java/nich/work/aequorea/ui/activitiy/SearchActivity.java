package nich.work.aequorea.ui.activitiy;

import nich.work.aequorea.common.Constants;
import nich.work.aequorea.data.SimpleArticleListModel;
import nich.work.aequorea.data.entity.Data;
import nich.work.aequorea.presenter.SearchPresenter;

public class SearchActivity extends TagActivity {
    
    @Override
    protected void initModel() {
        mModel = new SimpleArticleListModel();
        mModel.setKey(getIntent().getStringExtra(Constants.SEARCH_KEY));
    }
    
    @Override
    protected void initView() {
        super.initView();
        mToolbar.setTitle(mModel.getKey());
    }
    
    @Override
    protected void initPresenter() {
        mPresenter = new SearchPresenter();
        mPresenter.attach(this);
        mPresenter.load();
    }
    
    @Override
    public void onDataLoaded(Data a) {
        super.onDataLoaded(a);
    }
}
