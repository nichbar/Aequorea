package nich.work.aequorea.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import nich.work.aequorea.view.MainActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [HomeFragmentProvider::class])
    abstract fun contributeMainActivity(): MainActivity

}