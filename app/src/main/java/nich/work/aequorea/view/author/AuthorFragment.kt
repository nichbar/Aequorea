package nich.work.aequorea.view.author

import android.view.View
import nich.work.aequorea.R
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.common.arch.paging.ListFragment
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.di.Injectable
import nich.work.aequorea.common.utils.viewModelProvider
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.view.shared.SimpleArticleListAdapter

class AuthorFragment : ListFragment<Datum, Datum>(), Injectable {

    private lateinit var mViewModel: AuthorViewModel
    private lateinit var mAdapter: SimpleArticleListAdapter

    override fun provideViewModel(): ListViewModel<Datum, Datum> {
        mViewModel = viewModelProvider(factory)
        return mViewModel
    }

    override fun provideAdapter(): ListAdapter<Datum> {
        mAdapter = SimpleArticleListAdapter()
        return mAdapter
    }

    override fun provideContentView(): View {
        return inflate(R.layout.fragment_author)
    }

}