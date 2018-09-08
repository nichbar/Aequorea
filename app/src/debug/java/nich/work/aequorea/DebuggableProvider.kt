package nich.work.aequorea

import android.app.Application
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary

object DebuggableProvider {

    fun init(application: Application) {
        Stetho.initializeWithDefaults(application)
        if (!LeakCanary.isInAnalyzerProcess(application)) {
            LeakCanary.install(application)
        }
    }

}