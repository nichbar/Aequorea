package nich.work.aequorea

import android.app.Activity
import android.app.Application
import com.flurry.android.FlurryAgent
import com.zzhoujay.richtext.RichText
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import nich.work.aequorea.common.Constants
import nich.work.aequorea.common.utils.ThemeHelper
import nich.work.aequorea.common.utils.releaseOnly
import nich.work.aequorea.di.AppInjector
import java.io.File
import javax.inject.Inject

class Aequorea : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    companion object {
        lateinit var app: Aequorea
        lateinit var currentTheme: String

        fun isLightTheme(): Boolean {
            return currentTheme == Constants.THEME_LIGHT
        }
    }

    override fun onCreate() {
        super.onCreate()

        app = this
        currentTheme = ThemeHelper.getTheme()

        releaseOnly { FlurryAgent.Builder().build(this, BuildConfig.FLURRY_ID) }

        AppInjector.init(this)
        DebuggableProvider.init(this)

        initCache()
    }

    private fun initCache() {
        val cacheDir = File(cacheDir?.absolutePath + File.separator + Constants.ARTICLE_CACHE)
        RichText.initCacheDir(cacheDir)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

}