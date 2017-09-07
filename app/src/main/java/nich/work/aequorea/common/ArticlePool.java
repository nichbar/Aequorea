package nich.work.aequorea.common;

import android.util.LruCache;

public class ArticlePool {
    private static final int MAX_CACHED_ARTICLE_SIZE = 20;
    
    private final LruCache<Long, String> mCache;
    
    private ArticlePool() {
        this.mCache = new LruCache<>(MAX_CACHED_ARTICLE_SIZE);
    }
    
    private static class ArticlePoolHolder {
        private static final ArticlePool ARTICLE_CACHE_POOL = new ArticlePool();
    }
    
    public static ArticlePool getCache(){
        return ArticlePoolHolder.ARTICLE_CACHE_POOL;
    }
    
    public void cache(long id, String articleData){
        mCache.put(id, articleData);
    }
    
    public String loadCache(long id) {
        return mCache.get(id);
    }
    
    public void recycle() {
        mCache.evictAll();
    }
}
