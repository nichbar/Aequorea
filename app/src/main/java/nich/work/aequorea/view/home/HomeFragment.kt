package nich.work.aequorea.view.home

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import butterknife.BindView
import nich.work.aequorea.R
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.common.arch.paging.ListFragment
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.di.Injectable
import nich.work.aequorea.common.di.ViewModelProviderFactory
import nich.work.aequorea.common.ui.widget.MaterialSearchView
import nich.work.aequorea.common.ui.widget.NestedScrollAppBarLayout
import nich.work.aequorea.common.ui.widget.StatusBarView
import nich.work.aequorea.common.utils.DisplayUtils
import nich.work.aequorea.common.utils.IntentUtils
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.ui.adapter.InstantSearchAdapter
import javax.inject.Inject

class HomeFragment : ListFragment<Datum, Datum>(), Injectable {

    private var mClickTime: Long = 0

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.main_content)
    lateinit var coordinatorLayout: CoordinatorLayout
    @BindView(R.id.appbar)
    lateinit var appBarLayout: NestedScrollAppBarLayout
    @BindView(R.id.status_bar)
    lateinit var statusBar: StatusBarView
    @BindView(R.id.search_view)
    lateinit var searchView: MaterialSearchView
    @BindView(R.id.search_mask)
    lateinit var searchMask: View

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarStyle()

        toolbar.title = resources.getString(R.string.app_name)
        toolbar.setOnClickListener {
            if (System.currentTimeMillis() - mClickTime < 200) {
                val position = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                scrollToTop(position)
            }
            mClickTime = System.currentTimeMillis()
        }
//        mAppBarLayout.setOnNestedListener(this)

        searchView.run {
            setHint(getString(R.string.search_article))
            setOnQueryTextListener(
                    object : MaterialSearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            searchView.closeSearch()
                            IntentUtils.startSearchActivity(context, query)
                            return false
                        }

                        override fun onQueryTextChange(newText: String): Boolean {
//                            mViewModel.getSearchList(newText)
                            return false
                        }
                    })
            setOnItemClickListener { _, _, _, _ ->
                val bean = view.getTag(R.string.app_name) as InstantSearchAdapter.InstantSearchBean
                bean.let {
                    IntentUtils.startArticleActivity(context, bean.id)
                    searchView.closeSearch()
                }
            }
            setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
                override fun onSearchViewShown() {
                    searchMask.visibility = View.VISIBLE
                }

                override fun onSearchViewClosed() {
                    searchMask.visibility = View.GONE
                }
            })
        }
    }

    private fun setStatusBarStyle() {
        if (isInLightTheme()) {
            statusBar.setLightMask()
            DisplayUtils.setStatusBarStyle(activity, true)
        } else {
            statusBar.setDarkMask()
            DisplayUtils.setStatusBarStyle(activity, false)
        }
    }

    private fun scrollToTop(currentPosition: Int) {
        if (currentPosition >= 10) {
            mRecyclerView.scrollToPosition(6)
        }
        mRecyclerView.smoothScrollToPosition(0)
    }

}