package nich.work.aequorea.main.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.main.model.article.Article;
import nich.work.aequorea.main.model.mainpage.Data;
import nich.work.aequorea.main.ui.view.ArticleView;

public class ArticlePresenter extends BasePresenter<ArticleView> {
    private Article mArticle;
    private Throwable mThrowable;
    private NetworkService mService;
    
    @Override
    protected void onAttach() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
    }
    
    public void load(long id) {
        mComposite.add(mService.getArticleDetailInfo(id)
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
    
    public void loadRecommendedArticles(long id){
        mComposite.add(mService.getRecommendedArticle(id)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Data>() {
            @Override
            public void accept(Data data) throws Exception {
                mBaseView.onRecommendationLoaded(data.getData());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mBaseView.onRecommendationError(throwable);
            }
        }));
    }

    private void publish() {
        if (mArticle != null) {
            mBaseView.onArticleLoaded(mArticle.getData());
        } else if (mThrowable != null) {
            mBaseView.onArticleError(mThrowable);
        }
    }
}
