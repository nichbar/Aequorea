package nich.work.aequorea;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    }
    
    public static Application getApp() {
        return mApp;
    }
    
    public static String getCurrentTheme() {
        return mCurrentTheme;
    }
    
    public static void setCurrentTheme(String theme) {
        mCurrentTheme = theme;
    }
    
    public static ExecutorService getExecutor(){
        return mExecutor;
    }
}
