package nich.work.aequorea.author.presenter;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.author.model.entities.Datum;
import nich.work.aequorea.author.ui.AuthorActivity;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.AbsPresenter;
import nich.work.aequorea.common.ui.activities.BaseActivity;

public class AuthorPresenter extends AbsPresenter {
    private NetworkService mService;
    private CompositeDisposable mComposite;
    private AuthorActivity mView;
    private Author mAuthor;
    private Throwable mThrowable;
    private int mPage;
    private int mPer;
    private long mTotalPage;
    
    @Override
    public void initialize() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        
        mComposite = new CompositeDisposable();
        mPage = 1;
        mPer = 15;
    }
    
    public void load(int id) {
        if (mPage > mTotalPage) {
            return;
        }
        
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
            }));
    }
    
    public void findAuthorInfo(final Author a) {
        final List<Datum> data = a.getData();
        
        mComposite.add(Single.create(new SingleOnSubscribe<Author>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Author> e) throws Exception {
                if (data.size() != 0) {
                    for (Author author : data.get(0).getAuthors()) {
                        // sometimes the leader may mark as the first author, so we need to make sure it's the right author
                        if (author.getId() == mView.getModel().getAuthorId()) {
                            author.setMeta(a.getMeta());
                            e.onSuccess(author);
                            return;
                        }
                    }
                }
            }
        })
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Author>() {
                @Override
                public void accept(Author author) throws Exception {
                    mView.updateAuthorInfo(author);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    
                }
            }));
    }
    
    private void publish() {
        if (mAuthor != null) {
            mView.onUpdate(mAuthor);
            mPage++;
            mTotalPage = mAuthor.getMeta().getTotalPages();
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
