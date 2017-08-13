package nich.work.aequorea.main.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.AbsPresenter;
import nich.work.aequorea.main.model.article.Article;
import nich.work.aequorea.main.ui.activities.ArticleActivity;

public class ArticlePresenter extends AbsPresenter {
    private CompositeDisposable mComposite;
    private ArticleActivity mView;
    private Article mArticle;
    private Throwable mThrowable;
    
    public ArticlePresenter(ArticleActivity articleActivity) {
        mView = articleActivity;
        initialize();
    }
    
    public void initialize() {
        mComposite = new CompositeDisposable();
    }

    public void load(long id) {
        NetworkService service = RequestManager.getInstance().getRetrofit().create(NetworkService.class);

        mComposite.add(service.getArticleDetailInfo(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Article>() {
                    @Override
                    public void accept(Article article) throws Exception {
                        mArticle = article;
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
        if (mArticle != null) {
            mView.onUpdate(mArticle.getData());
        } else if (mThrowable != null) {
            mView.onError(mThrowable);
        }
    }

    @Override
    public void detach() {
        mComposite.clear();
        mView = null;
    }

}
