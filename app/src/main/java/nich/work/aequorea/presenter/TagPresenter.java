package nich.work.aequorea.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.model.entity.Data;

public class TagPresenter extends SimpleArticleListPresenter {
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
    
        mComposite.add(mService.getTagsList(mBaseView.getModel().getId(), mPage, mPer)
            .subscribeOn(Schedulers.newThread())
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
}
