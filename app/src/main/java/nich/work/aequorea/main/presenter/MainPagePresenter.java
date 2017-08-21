package nich.work.aequorea.main.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.main.model.mainpage.Data;
import nich.work.aequorea.main.ui.view.HomeView;

public class MainPagePresenter extends BasePresenter<HomeView> {
    private NetworkService mNetworkService;
    private Data mDataData;

    private Throwable mError;
    private int mPage = 1;
    
    @Override
    protected void onAttach() {
        mNetworkService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        loadData();
    }
    
    public void loadData() {
        if (!NetworkUtils.isNetworkAvailable()){
            mBaseView.onError(null);
            return;
        }
        
        if (mBaseView.getModel().isLoading()){
            return;
        }
    
        mBaseView.getModel().setLoading(true);
    
        mComposite.add(mNetworkService
                .getMainPageInfo(mPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Data>() {
                    @Override
                    public void accept(Data data) throws Exception {
                        mDataData = data;
                        mPage++;
                        publish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mError = throwable;
                        publish();
                    }
                })
        );
    }

    private void publish() {
        if (mBaseView != null) {
            mBaseView.setRefreshing(false);
            mBaseView.getModel().setLoading(false);
            
            if (mDataData != null) {
                mBaseView.onDataLoaded(mDataData.getData(), mBaseView.getModel().isRefreshing());
                mBaseView.getModel().setRefreshing(false);
            } else if (mError != null) {
                mBaseView.onError(mError);
            }
            mDataData = null;
            mError = null;
        }
    }

    public void refresh() {
        mPage = 1;
        loadData();
    }
}
