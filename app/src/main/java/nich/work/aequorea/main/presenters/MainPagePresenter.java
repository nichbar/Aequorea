package nich.work.aequorea.main.presenters;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenters.AbsPresenter;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.main.model.mainpage.Page;
import nich.work.aequorea.main.ui.activities.MainActivity;

public class MainPagePresenter extends AbsPresenter {
    private MainActivity mView;
    private Page mPageData;

    private CompositeDisposable disposables;
    private Throwable mError;
    private int mPage = 1;

    @Override
    public void initialize() {
        disposables = new CompositeDisposable();
        loadData();
    }

    public void loadData() {
        NetworkService networkService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        disposables.add(networkService
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
                }));
    }

    private void publish() {
        if (mView != null) {
            if (mPageData != null)
                mView.onUpdateAdapter(mPageData.getData(), mView.getModel().isRefreshing());
            else if (mError != null) {
                mView.onError(mError);
            }
            mView.getModel().setRefreshing(false);
            mView.getModel().setLoading(false);
        }
    }

    @Override
    public void attach(BaseActivity activity) {
        mView = (MainActivity) activity;
        publish();
    }

    @Override
    public void detach() {
        disposables.clear();
        mView = null;
    }

    public void refresh(){
        mPage = 1;
        loadData();
    }
}
