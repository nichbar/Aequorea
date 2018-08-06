package nich.work.aequorea.view.home

import android.arch.lifecycle.ViewModelProviders
import android.view.View
import nich.work.aequorea.R
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.common.arch.paging.ListFragment
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.di.Injectable
import nich.work.aequorea.common.di.ViewModelProviderFactory
import nich.work.aequorea.data.entity.Datum
import javax.inject.Inject

class HomeFragment : ListFragment<Datum, Datum>(), Injectable {

    @Inject
    lateinit var factory: ViewModelProviderFactory<HomeViewModel>

    private lateinit var mViewModel: HomeViewModel
    private lateinit var mAdapter: HomeAdapter

    override fun provideContentView(): View {
        return inflate(R.layout.fragment_home)
    }

    override fun provideViewModel(): ListViewModel<Datum, Datum> {
        mViewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        return mViewModel
    }

    override fun provideAdapter(): ListAdapter<Datum> {
        mAdapter = HomeAdapter()
        return mAdapter
    }

}