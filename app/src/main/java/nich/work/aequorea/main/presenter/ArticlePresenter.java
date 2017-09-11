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
import nich.work.aequorea.common.cache.ArticleCache;
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
    
    public void loadArticle(long id) {
        
//        // try to load from memory
        String cacheContent = ArticleCache.getCache().loadCache(id);

        if (!TextUtils.isEmpty(cacheContent)) {

            Article article = generateObjectFromString(cacheContent);
            if (article != null) {
                onArticleLoaded(article);
                return;
            }
        }
        
        // then from storage
        if (ArticleCache.getCache().isCachedInStorage(id)) {
            loadArticleFromStorage(id);
        } else {
            loadArticleFromInternet(id);
        }
    }
    
    private void loadArticleFromStorage(final long id) {
        mComposite.add(Single.create(new SingleOnSubscribe<Article>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Article> e) throws Exception {
                String s = ArticleCache.getCache().loadCacheFromStorage(id);
                Article article = generateObjectFromString(s);
                e.onSuccess(article);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Article>() {
                @Override
                public void accept(Article article) throws Exception {
                    onArticleLoaded(article);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    onArticleError(throwable);
                }
            }));
    }
    
    public void loadArticleFromInternet(final long id) {
        
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
    
    public void saveSnapshotToStorage(final Bitmap bitmap, final String title) {
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
                    mBaseView.onSnapshotSavedSucceeded(s);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mBaseView.onSnapshotSavedFailed(throwable);
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
        ArticleCache.getCache().cache(id, json);
    }
    
    private Article generateObjectFromString(String cacheContent) {
        Gson gson = new Gson();
        return gson.fromJson(cacheContent, Article.class);
    }
}
