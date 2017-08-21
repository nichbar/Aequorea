package nich.work.aequorea;

import android.app.Application;

public class Aequorea extends Application {
    private static Aequorea mApp;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mApp = this;
    }
    
    public static Application getApp(){
        return mApp;
    }
}
