package nich.work.aequorea

import android.app.Application
import com.zzhoujay.richtext.RichText
import nich.work.aequorea.common.Constants
import nich.work.aequorea.common.cache.ArticleCache
import nich.work.aequorea.common.utils.ThemeHelper
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Aequorea : Application() {

    companion object {
        private var mApp: Aequorea? = null
        private var mCurrentTheme: String? = null
        private var mExecutor: ExecutorService? = null

        fun getApp(): Application {
            return mApp!!
        }

        fun getCurrentTheme(): String {
            return mCurrentTheme!!
        }

        fun isLightTheme(): Boolean {
            return mCurrentTheme == Constants.THEME_LIGHT
        }

        fun setCurrentTheme(theme: String) {
            mCurrentTheme = theme
        }

        fun getExecutor(): ExecutorService {
            return this.mExecutor!!
        }
    }

    override fun onCreate() {
        super.onCreate()

        mApp = this
        mCurrentTheme = ThemeHelper.getTheme()
        mExecutor = Executors.newCachedThreadPool()

        initCache()
    }

    private fun initCache() {
        val cacheDir = File(cacheDir?.absolutePath + File.separator + Constants.ARTICLE_CACHE)
        RichText.initCacheDir(cacheDir)
        ArticleCache.getCache()
    }
}