package nich.work.aequorea.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import nich.work.aequorea.view.home.HomeFragment

@Module
abstract class HomeFragmentProvider {
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun contributeHomeFragment(): HomeFragment
}