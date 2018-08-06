package nich.work.aequorea.presenter;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.data.entity.Author;
import nich.work.aequorea.data.entity.Data;
import nich.work.aequorea.data.entity.Datum;

public class AuthorPresenter extends SimpleArticleListPresenter {
    
    @Override
    public void load() {
        
        if (mPage > mTotalPage || mBaseView.getModel().isLoading()) {
            return;
        }
        
        mBaseView.getModel().setLoading(true);
        
        mComposite.add(mService.getArticleList(mBaseView.getModel().getId(), mPage, mPer)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onDataLoaded, this::onError));
    }
    
    public void findAuthorInfo(final Data a) {
        final List<Datum> data = a.getData();
        
        mComposite.add(Single.create((SingleOnSubscribe<Author>) e -> {
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
        })
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(author -> mBaseView.onUpdateAuthorInfo(author)));
    }
}
