package nich.work.aequorea;

import android.app.Application;

import okhttp3.OkHttpClient;

public class App extends Application {
    private static OkHttpClient mHttpClient;
    
    public static OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient();
        }
        return mHttpClient;
    }
}
