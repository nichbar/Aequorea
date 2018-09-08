package nich.work.aequorea.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import nich.work.aequorea.Aequorea
import nich.work.aequorea.DebuggableModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class,
    ActivityModule::class,
    DebuggableModule::class,
    AppModule::class])

interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: Aequorea)
}