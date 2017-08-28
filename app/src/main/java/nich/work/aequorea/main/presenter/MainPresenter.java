package nich.work.aequorea.main.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.R;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.main.model.mainpage.Data;
import nich.work.aequorea.main.ui.view.HomeView;

public class MainPresenter extends BasePresenter<HomeView> {
    private NetworkService mNetworkService;
    
    private int mPage = 1;
    
    @Override
    protected void onAttach() {
        mNetworkService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        loadData();
    }
    
    public void loadData() {
        loadData(-1);
    }
    
    public void loadData(int page) {
        if (!NetworkUtils.isNetworkAvailable()){
            onError(new Throwable(getString(R.string.please_connect_to_the_internet)));
            return;
        }
        
        if (mBaseView.getModel().isLoading()){
            return;
        }
        
        if (page != -1){
            mPage = page;
        }
    
        mBaseView.getModel().setLoading(true);
    
        mComposite.add(mNetworkService
                .getMainPageInfo(mPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Data>() {
                    @Override
                    public void accept(Data data) throws Exception {
                        onDataLoaded(data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        onError(throwable);
                    }
                })
        );
    }
    
    private void onDataLoaded(Data data) {
        mPage++;
    
        setLoadingFinish();
        mBaseView.onDataLoaded(data.getData(), mBaseView.getModel().isRefreshing());
    }
    
    private void onError(Throwable t) {
        setLoadingFinish();
        mBaseView.onError(t);
    }
    
    private void setLoadingFinish() {
        mBaseView.setRefreshing(false);
        mBaseView.getModel().setLoading(false);
        mBaseView.getModel().setRefreshing(false);
    }

    public void refresh() {
        loadData(1);
    }
}
