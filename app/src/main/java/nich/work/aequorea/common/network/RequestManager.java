package nich.work.aequorea.common.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestManager {
    
    private Retrofit mRetrofit;
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 5;
    
    private static final String PLATFORM = "Android";
    private static final String VERSION = "3.1.1.0";

    private RequestManager() {
        init();
    }

    private void init() {
        
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(chain -> {
                    Request original = chain.request();
            
                    Request request = original.newBuilder()
                        .addHeader("Authorization","")
                        .addHeader("ClientPlatform", PLATFORM)
                        .addHeader("ClientVersion", VERSION)
                        .build();
            
                    return chain.proceed(request);
                })
                .build();
        
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.CBNWEEK_HTTPS_HOST)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }

    public static RequestManager getInstance() {
        return RequestManagerHolder.sInstance;
    }

    private static class RequestManagerHolder {
        private static RequestManager sInstance = new RequestManager();
    }
}
