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
    private Page mPage;

    private CompositeDisposable disposables;
    private Throwable mError;

    @Override
    public void initialize() {
        NetworkService networkService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);

        disposables = new CompositeDisposable();
        disposables.add(networkService
                .getMainPageInfo(1)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Page>() {
                    @Override
                    public void accept(Page page) throws Exception {
                        mPage = page;
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
        if (mView != null ) {
            if (mPage != null)
                mView.onUpdateAdapter(mPage.getData());
            else if (mError != null){
                mView.onError(mError);
            }
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
}
