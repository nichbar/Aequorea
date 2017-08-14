package nich.work.aequorea.main.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.AbsPresenter;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.main.model.mainpage.Page;
import nich.work.aequorea.main.ui.activities.MainActivity;

public class MainPagePresenter extends AbsPresenter {
    private NetworkService mNetworkService;
    private MainActivity mView;
    private Page mPageData;

    private CompositeDisposable mComposite;
    private Throwable mError;
    private int mPage = 1;
    
    public MainPagePresenter(MainActivity mainActivity) {
        mView = mainActivity;
        initialize();
    }
    
    public void initialize() {
        mNetworkService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        mComposite = new CompositeDisposable();
        loadData();
    }

    public void loadData() {
        if (!NetworkUtils.isNetworkAvailable()){
            mView.onError(null);
            return;
        }
        
        if (mView.getModel().isLoading()){
            return;
        }
    
        mView.getModel().setLoading(true);
    
        mComposite.add(mNetworkService
                .getMainPageInfo(mPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Page>() {
                    @Override
                    public void accept(Page page) throws Exception {
                        mPageData = page;
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
        if (mView != null) {
            mView.setRefreshing(false);
            mView.getModel().setLoading(false);
            
            if (mPageData != null) {
                mView.onUpdate(mPageData.getData(), mView.getModel().isRefreshing());
                mView.getModel().setRefreshing(false);
            } else if (mError != null) {
                mView.onError(mError);
            }
            mPageData = null;
            mError = null;
        }
    }

    @Override
    public void detach() {
        mComposite.clear();
        mView = null;
    }

    public void refresh() {
        mPage = 1;
        loadData();
    }
}
