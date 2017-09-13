package nich.work.aequorea.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;

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
import nich.work.aequorea.common.utils.FilterUtils;
import nich.work.aequorea.common.utils.IOUtils;
import nich.work.aequorea.model.entity.Data;
import nich.work.aequorea.model.entity.DataWrapper;
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.ui.view.ArticleView;

public class ArticlePresenter extends BasePresenter<ArticleView> {
    private NetworkService mService;
    
    @Override
    protected void onAttach() {
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
    }
    
    public void loadArticle(long id) {
        
        // try to load from memory
        String cacheContent = ArticleCache.getCache().loadCache(id);

        if (!TextUtils.isEmpty(cacheContent)) {
    
            DataWrapper article = generateObjectFromString(cacheContent);
            if (article != null) {
                onArticleLoaded(article);
                return;
            }
        }
        
        // then from storage
        if (ArticleCache.getCache().isCachedInStorage(id)) {
            loadArticleFromStorage(id);
        } else {
            loadArticleFromInternet(id, false);
        }
    }
    
    private void loadArticleFromStorage(final long id) {
        mComposite.add(Single.create(new SingleOnSubscribe<DataWrapper>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<DataWrapper> e) throws Exception {
                String s = ArticleCache.getCache().loadCacheFromStorage(id);
                DataWrapper article = generateObjectFromString(s);
                e.onSuccess(article);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<DataWrapper>() {
                @Override
                public void accept(DataWrapper article) throws Exception {
                    onArticleLoaded(article);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    onArticleError(throwable);
                }
            }));
    }
    
    public void loadArticleFromInternet(final long id, boolean isRefresh) {
        
        mComposite.add(mService.getArticleDetailInfo(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataWrapper>() {
                @Override
                public void accept(DataWrapper article) throws Exception {
                    cacheArticle(id, article);
                    onArticleLoaded(article);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    onArticleError(throwable);
                }
            }));
    
        if (isRefresh) {
            ArticleCache.getCache().remove(Long.toString(id));
        }
    }
    
    public void loadRecommendedArticles(long id) {
        mComposite.add(mService.getRecommendedArticle(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Data>() {
                @Override
                public void accept(Data data) throws Exception {
                    
                    mBaseView.onRecommendationLoaded(filterData(data.getData()));
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mBaseView.onRecommendationError(throwable);
                }
            }));
    }
    
    private List<Datum> filterData(List<Datum> data) {
        Iterator<Datum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Datum d = iterator.next();
            if (!FilterUtils.underSupport(d.getArticleType())) {
                iterator.remove();
            }
        }
        return data;
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
    
    private void onArticleLoaded(DataWrapper article) {
        mBaseView.onArticleLoaded(article.getData());
    }
    
    private void onArticleError(Throwable throwable) {
        mBaseView.onArticleError(throwable);
    }
    
    private void cacheArticle(long id, DataWrapper article) {
        Gson gson = new Gson();
        String json = gson.toJson(article);
        ArticleCache.getCache().cache(id, json);
    }
    
    private DataWrapper generateObjectFromString(String cacheContent) {
        Gson gson = new Gson();
        return gson.fromJson(cacheContent, DataWrapper.class);
    }
}
