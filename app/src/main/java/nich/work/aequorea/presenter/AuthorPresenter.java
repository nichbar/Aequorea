package nich.work.aequorea.presenter;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.model.entity.Author;
import nich.work.aequorea.model.entity.Data;
import nich.work.aequorea.model.entity.Datum;

public class AuthorPresenter extends SimpleArticleListPresenter {
    
    @Override
    public void load() {
        if (!NetworkUtils.isNetworkAvailable()) {
            onError(new Throwable(getString(R.string.please_connect_to_the_internet)));
            return;
        }
        
        if (mPage > mTotalPage || mBaseView.getModel().isLoading()) {
            return;
        }
        mBaseView.getModel().setLoading(true);
        
        mComposite.add(mService.getArticleList(mBaseView.getModel().getId(), mPage, mPer)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Data>() {
                @Override
                public void accept(Data author) throws Exception {
                    onDataLoaded(author);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    onError(throwable);
                }
            }));
    }
    
    public void findAuthorInfo(final Data a) {
        final List<Datum> data = a.getData();
        
        mComposite.add(Single.create(new SingleOnSubscribe<Author>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Author> e) throws Exception {
                if (data.size() != 0) {
                    for (Author author : data.get(0).getAuthors()) {
                        // sometimes the leader may mark as the first author, so we need to make sure it's the right author
                        if (author.getId() == mBaseView.getModel().getId()) {
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
}
