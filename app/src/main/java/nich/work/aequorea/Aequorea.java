package nich.work.aequorea;

import android.app.Application;

import com.zzhoujay.richtext.RichText;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.cache.ArticleCache;
import nich.work.aequorea.common.utils.ThemeHelper;

public class Aequorea extends Application {
    private static Aequorea mApp;
    private static String mCurrentTheme;
    private static ExecutorService mExecutor;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mApp = this;
        mCurrentTheme = ThemeHelper.getTheme();
        mExecutor = Executors.newCachedThreadPool();
    
        initCache();
    
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
    }
    
    private void initCache() {
        File cacheDir = new File(getCacheDir().getAbsolutePath() + File.separator + Constants.ARTICLE_PIC_CACHE);
        RichText.initCacheDir(cacheDir);
        ArticleCache.getCache();
    }
    
    public static Application getApp() {
        return mApp;
    }
    
    public static String getCurrentTheme() {
        return mCurrentTheme;
    }
    
    public static boolean isLightTheme() {
        return mCurrentTheme.equals(Constants.THEME_LIGHT);
    }
    
    public static void setCurrentTheme(String theme) {
        mCurrentTheme = theme;
    }
    
    public static ExecutorService getExecutor(){
        return mExecutor;
    }
}
