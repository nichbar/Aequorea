package nich.work.aequorea.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TagPresenter extends SimpleArticleListPresenter {
    @Override
    public void load() {
        
        if (mPage > mTotalPage || mBaseView.getModel().isLoading()) {
            return;
        }
        mBaseView.getModel().setLoading(true);
        
        mComposite.add(mService.getTagsList(mBaseView.getModel().getId(), mPage, mPer)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onDataLoaded, this::onError));
    }
}
