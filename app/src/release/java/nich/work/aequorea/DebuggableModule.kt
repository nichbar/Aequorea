package nich.work.aequorea

import dagger.Module
import dagger.Provides
import nich.work.aequorea.common.Config.OKHTTP_TIMEOUT
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class DebuggableModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
                .build()
    }

}