package nich.work.aequorea.common.cache;

import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nich.work.aequorea.Aequorea;
import nich.work.aequorea.BuildConfig;

public class ArticleCache {
    private static final int MAX_CACHED_MEMORY_ARTICLE_SIZE = 20;
    private static final int MAX_CACHED_DISK_ARTICLE_SIZE = 1024 * 1024 * 10; // 10M
    
    private final LruCache<Long, String> mCache;
    private DiskLruCache mDiskCache;
    
    private ArticleCache() {
        this.mCache = new LruCache<>(MAX_CACHED_MEMORY_ARTICLE_SIZE);
        initDiskCache();
    }
    
    private void initDiskCache() {
        Aequorea.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File cacheDir = new File(Aequorea.getApp().getCacheDir().getAbsolutePath() + File.separator + "article_cache");
                    mDiskCache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1, MAX_CACHED_DISK_ARTICLE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void remove(final String key) {
        try {
            mDiskCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class ArticlePoolHolder {
        private static final ArticleCache ARTICLE_CACHE_POOL = new ArticleCache();
    }
    
    public static ArticleCache getCache() {
        return ArticlePoolHolder.ARTICLE_CACHE_POOL;
    }
    
    public void cache(final long id, final String articleData) {
        mCache.put(id, articleData);
        
        Aequorea.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                cacheInStorage(id, articleData);
            }
        });
    }
    
    // cache in internal storage
    private void cacheInStorage(long id, String articleData) {
        String key = Long.toString(id);
        if (mDiskCache != null) {
            try {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                
                if (snapshot == null) {
                    DiskLruCache.Editor editor = mDiskCache.edit(key);
    
                    OutputStream out = editor.newOutputStream(0);
                    out.write(articleData.getBytes());
                    out.close();
                    editor.commit();
                } else {
                    snapshot.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isCachedInStorage(long id) {
        String key = Long.toString(id);
        
        if (mDiskCache != null) {
            try {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public String loadCacheFromStorage(long id) {
        String key = Long.toString(id);
        
        if (mDiskCache != null) {
            try {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                InputStream in = snapshot.getInputStream(0);
                if (in != null) {
                    BufferedInputStream bis = new BufferedInputStream(in);
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    int result = bis.read();
                    while (result != -1) {
                        buf.write((byte) result);
                        result = bis.read();
                    }
                    return buf.toString("UTF-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    public String loadCache(long id) {
        return mCache.get(id);
    }
    
    public void recycle() {
        mCache.evictAll();
    }
}
