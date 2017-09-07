package nich.work.aequorea.main.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.common.ArticlePool;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.presenter.BasePresenter;
import nich.work.aequorea.common.utils.IOUtils;
import nich.work.aequorea.main.model.article.Article;
import nich.work.aequorea.main.model.mainpage.Data;
import nich.work.aequorea.main.ui.view.ArticleView;

public class ArticlePresenter extends BasePresenter<ArticleView> {
    private NetworkService mService;
    
    @Override
    protected void onAttach() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
    }
    
    public void load(final long id) {
        String cacheContent = ArticlePool.getCache().loadCache(id);
        
        if (!TextUtils.isEmpty(cacheContent)) {
            Gson gson = new Gson();
            Article article = gson.fromJson(cacheContent, Article.class);
            
            if (article != null) {
                onArticleLoaded(article);
                return;
            }
        }
        
        mComposite.add(mService.getArticleDetailInfo(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Article>() {
                @Override
                public void accept(Article article) throws Exception {
                    cacheArticle(id, article);
                    onArticleLoaded(article);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    onArticleError(throwable);
                }
            }));
    }
    
    public void loadRecommendedArticles(long id) {
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
    
    public void startSaveArticleToStorage(final Bitmap bitmap, final String title) {
        mComposite.add(Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                String address = IOUtils.saveBitmapToExternalStorage(bitmap, title);
                e.onSuccess(address);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    mBaseView.onArticleSavedAsPictureSucceeded(s);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mBaseView.onArticleSavedAsPictureFailed(throwable);
                }
            }));
    }
    
    private void onArticleLoaded(Article article) {
        mBaseView.onArticleLoaded(article.getData());
    }
    
    private void onArticleError(Throwable throwable) {
        mBaseView.onArticleError(throwable);
    }
    
    private void cacheArticle(long id, Article article) {
        Gson gson = new Gson();
        String json = gson.toJson(article);
        ArticlePool.getCache().cache(id, json);
    }
}
