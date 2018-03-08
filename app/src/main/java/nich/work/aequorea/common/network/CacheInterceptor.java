package nich.work.aequorea.common.network;

import java.io.IOException;

import nich.work.aequorea.common.utils.NetworkUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        if (!NetworkUtils.isNetworkAvailable()) {
            // 4 weeks stale
            request = request.newBuilder()
                .header("Cache-Control", "public, max-stale=2419200")
                .build();
        }
        
        Response originalResponse = chain.proceed(request);
        return originalResponse.newBuilder().header("Cache-Control", "max-age=600").build();
    }
}
