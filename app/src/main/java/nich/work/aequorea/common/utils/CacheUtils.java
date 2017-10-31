package nich.work.aequorea.common.utils;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zzhoujay.richtext.ig.BitmapPool;

import java.io.File;
import java.io.IOException;

import nich.work.aequorea.Aequorea;
import nich.work.aequorea.common.Constants;

public class CacheUtils {
    
    private static final int MAX_BITMAP_SIZE = 60 * 1024 * 1024;
    
    public static DiskLruCache getArticlePicDiskLruCache() {
        
        File cacheDir = new File(Aequorea.getApp()
            .getCacheDir()
            .getAbsolutePath() + File.separator + Constants.ARTICLE_PIC_CACHE);
        
        try {
            return DiskLruCache.open(checkDir(cacheDir), BitmapPool.getVersion(), 2, MAX_BITMAP_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File checkDir(File dir) {
        File cacheDir = new File(dir, "_rt");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheDir;
    }
}
