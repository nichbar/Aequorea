package nich.work.aequorea.presenter;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
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
import nich.work.aequorea.model.entity.search.SearchData;
import nich.work.aequorea.ui.view.HomeView;

public class MainPresenter extends BasePresenter<HomeView> {
    
    private NetworkService mNetworkService;
    private Timer mTimer;
    
    private static final int ITEM_PER_PAGE = 15;
    
    private static final int INSTANT_SEARCH_DELAY = 300;
    
    private int mPage = 1;
    
    @Override
    protected void onAttach() {
        mNetworkService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        mTimer = new Timer();
        loadCacheData();
    }
    
    private void loadCacheData() {
        if (mPage == 1 && !mBaseView.getModel().isRefreshing()) {
            Data cacheData = useCache();
            if (cacheData != null) {
                onDataLoaded(cacheData);
            }
        }
    }
    
    public void loadData() {
        loadData(-1);
    }
    
    private void loadData(int page) {
        
        if (mBaseView.getModel().isLoading()) {
            return;
        }
        
        if (page != -1) {
            mPage = page;
        }
        
        mBaseView.getModel().setLoading(true);
      
        mComposite.add(mNetworkService.getMainPageInfo(mPage, ITEM_PER_PAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(data -> {
                if (mPage == 1) {
                    cacheData(data);
                }
                mPage++;
                onDataLoaded(data);
            }, this::onError));
    }
    
    private void onDataLoaded(Data data) {
        mBaseView.onDataLoaded(FilterUtils.filterData(data.getData()), mBaseView.getModel()
            .isRefreshing());
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
        if (!NetworkUtils.isNetworkAvailable()) {
            onError(new Throwable(getString(R.string.please_connect_to_the_internet)));
            return;
        }
        
        loadData(1);
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
    
    public void getSearchList(final String key) {
        if (TextUtils.isEmpty(key)) return;
        
        mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getSearchListAfterDelay(key);
            }
        }, INSTANT_SEARCH_DELAY);
    }
    
    private void getSearchListAfterDelay(String key) {
        mComposite.add(mNetworkService.getArticleListWithKeyword(1, 10, key, false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onSearchResultLoaded, this::onError));
    }
    
    private void onSearchResultLoaded(SearchData data) {
        mBaseView.onSearchResultLoaded(FilterUtils.filterSearchData(data.getData()));
    }
}
