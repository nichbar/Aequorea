package nich.work.aequorea.presenter;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.common.utils.FilterUtils;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.common.utils.SPUtils;
import nich.work.aequorea.model.entity.Data;
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.ui.view.HomeView;

public class MainPresenter extends BasePresenter<HomeView> {
    private NetworkService mNetworkService;
    
    private static final int ITEM_PER_PAGE = 15;
    
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
        if (!NetworkUtils.isNetworkAvailable()) {
            if (mPage == 1 && !mBaseView.getModel().isRefreshing()) {
                Data cacheData = useCache();
                if (cacheData != null) {
                    onDataLoaded(cacheData);
                    return;
                }
            }
            onError(new Throwable(getString(R.string.please_connect_to_the_internet)));
            return;
        }
        
        if (mBaseView.getModel().isLoading()){
            return;
        }
        
        if (page != -1) {
            mPage = page;
        }
    
        mBaseView.getModel().setLoading(true);
    
        mComposite.add(mNetworkService
                .getMainPageInfo(mPage, ITEM_PER_PAGE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Data>() {
                    @Override
                    public void accept(Data data) throws Exception {
                        if (mPage == 1) {
                            cacheData(data);
                        }
                        mPage++;
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
        mBaseView.onDataLoaded(filter(data.getData()), mBaseView.getModel().isRefreshing());
        setLoadingFinish();
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
    
    // filter the content that can not display at this very moment
    // TODO support this kind of contents
    private List<Datum> filter(List<Datum> data) {
        Iterator<Datum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Datum d = iterator.next();
            if (!FilterUtils.underSupport(d.getType())) {
                iterator.remove();
            }
        }
        return data;
    }
    
    private void cacheData(Data data) {
        Gson gson = new Gson();
        String cacheString = gson.toJson(data);
        SPUtils.setString(Constants.SP_LATEST_MAIN_PAGE, cacheString);
    }
    
    private Data useCache() {
        String cacheString = SPUtils.getString(Constants.SP_LATEST_MAIN_PAGE);
        if (!TextUtils.isEmpty(cacheString)) {
            Gson gson = new Gson();
            return gson.fromJson(cacheString, Data.class);
        }
        return null;
    }
}
