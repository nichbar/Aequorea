package nich.work.aequorea.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import nich.work.aequorea.common.AppExecutor
import nich.work.aequorea.common.di.ViewModelProviderFactory
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.view.home.HomeViewModel

@Module
class HomeFragmentModule {

    @Provides
    fun provideViewModel(application: Application, apiService: ApiService, appExecutor: AppExecutor): HomeViewModel {
        return HomeViewModel(application, appExecutor, apiService)
    }

    @Provides
    fun provideViewModelFactory(viewModel: HomeViewModel): ViewModelProviderFactory<HomeViewModel> {
        return ViewModelProviderFactory(viewModel)
    }
}