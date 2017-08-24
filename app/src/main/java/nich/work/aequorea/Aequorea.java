package nich.work.aequorea;

import android.app.Application;

import nich.work.aequorea.common.utils.ThemeHelper;

public class Aequorea extends Application {
    private static Aequorea mApp;
    private static String mCurrentTheme;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mApp = this;
        mCurrentTheme = ThemeHelper.getTheme();
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
}
