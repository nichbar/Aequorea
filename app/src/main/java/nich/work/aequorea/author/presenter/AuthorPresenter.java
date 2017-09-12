package nich.work.aequorea.author.presenter;

import java.util.Iterator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.R;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.author.model.entities.Datum;
import nich.work.aequorea.author.ui.AuthorView;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.common.utils.FilterUtils;
import nich.work.aequorea.common.utils.NetworkUtils;

public class AuthorPresenter extends BasePresenter<AuthorView> {
    private NetworkService mService;
    private int mPage;
    private int mPer;
    private long mTotalPage;
    
    @Override
    protected void onAttach() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        
        mPage = 1;
        mPer = 15; // default value is 10, but due to unpredictable result it's set to 15.
        mTotalPage = 1;
    }
    
    public void load() {
        if (!NetworkUtils.isNetworkAvailable()) {
            onError(new Throwable(getString(R.string.please_connect_to_the_internet)));
            return;
        }
        
        if (mPage > mTotalPage || mBaseView.getModel().isLoading()) {
            return;
        }
        mBaseView.getModel().setLoading(true);
        
        mComposite.add(mService.getArticleList(mBaseView.getModel().getAuthorId(), mPage, mPer)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Author>() {
                @Override
                public void accept(Author author) throws Exception {
                    onDataLoaded(author);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    onError(throwable);
                }
            }));
    }
    
    private void onDataLoaded(Author author) {
        mPage++;
        mBaseView.getModel().setLoading(false);
        mTotalPage = author.getMeta().getTotalPages();
    
        // filter the content that can not display at this very moment
        // TODO support this kind of contents
        author.setData(filter(author.getData()));
    
        // when filter method above do filter most of the item, make a load call to load enough data to display.
        if (author.getData().size() <= 5) {
            load();
        }
        
        mBaseView.onDataLoaded(author);
    }
    
    private void onError(Throwable throwable) {
        mBaseView.getModel().setLoading(false);
        mBaseView.onError(throwable);
    }
    
    public void findAuthorInfo(final Author a) {
        final List<Datum> data = a.getData();
        
        mComposite.add(Single.create(new SingleOnSubscribe<Author>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Author> e) throws Exception {
                if (data.size() != 0) {
                    for (Author author : data.get(0).getAuthors()) {
                        // sometimes the leader may mark as the first author, so we need to make sure it's the right author
                        if (author.getId() == mBaseView.getModel().getAuthorId()) {
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
                    mBaseView.onUpdateAuthorInfo(author);
                }
            }));
    }
    
    private List<Datum> filter(List<Datum> data) {
        Iterator<Datum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Datum d = iterator.next();
            if (FilterUtils.underSupport(d.getArticleType())) {
                iterator.remove();
            }
        }
        return data;
    }
}
