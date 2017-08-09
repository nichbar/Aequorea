package nich.work.aequorea.author.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.author.ui.AuthorActivity;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.AbsPresenter;
import nich.work.aequorea.common.ui.activities.BaseActivity;

public class AuthorPresenter extends AbsPresenter{
    private NetworkService mService;
    private CompositeDisposable mComposite;
    private AuthorActivity mView;
    private Author mAuthor;
    private Throwable mThrowable;
    private int mPage;
    private int mPer;
    
    @Override
    public void initialize() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
    
        mComposite = new CompositeDisposable();
        mPage = 1;
        mPer = 10;
    }
    
    public void load(int id){
    
        mComposite.add(mService.getArticleList(id, mPage, mPer)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Author>() {
                @Override
                public void accept(Author author) throws Exception {
                    mAuthor = author;
                    publish();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mThrowable = throwable;
                    publish();
                }
            })
        );
    }
    
    private void publish() {
        if (mAuthor != null) {
            mView.onUpdate(mAuthor);
        } else if (mThrowable != null) {
            mView.onError(mThrowable);
        }
        mAuthor = null;
        mThrowable = null;
        mView.getModel().setLoading(false);
    }
    
    @Override
    public void attach(BaseActivity activity) {
        mView = (AuthorActivity) activity;
    }
    
    @Override
    public void detach() {
        mComposite.clear();
        mView = null;
    }
}
