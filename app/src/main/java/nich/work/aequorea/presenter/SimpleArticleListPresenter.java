package nich.work.aequorea.presenter;

import java.util.Iterator;
import java.util.List;

import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.common.utils.FilterUtils;
import nich.work.aequorea.model.entity.Data;
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.ui.view.SimpleArticleListView;

public abstract class SimpleArticleListPresenter extends BasePresenter<SimpleArticleListView> {
    protected NetworkService mService;
    protected int mPage;
    protected int mPer;
    protected long mTotalPage;
    
    @Override
    protected void onAttach() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        
        mPage = 1;
        mPer = 20; // default value is 10, but due to unpredictable result it's set to 20.
        mTotalPage = 1;
    }
    
    public abstract void load();
    
    protected void onDataLoaded(Data data) {
        if (data.getData() != null && data.getData().size() != 0) {
            mPage++;
            mBaseView.getModel().setLoading(false);
            mTotalPage = data.getMeta().getTotalPages();
    
            data.setData(filter(data.getData()));
    
            // when filter method above do filter most of the item, make a load call to load enough data to display.
            if (data.getData().size() <= 5) {
                load();
            }
    
            mBaseView.onDataLoaded(data);
        } else {
            mBaseView.onNoData();
        }
    }
    
    protected void onError(Throwable throwable) {
        mBaseView.getModel().setLoading(false);
        mBaseView.onError(throwable);
    }
    
    private List<Datum> filter(List<Datum> data) {
        Iterator<Datum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Datum d = iterator.next();
            if (!FilterUtils.underSupport(d.getArticleType())) {
                iterator.remove();
            }
        }
        return data;
    }
}
