package nich.work.aequorea.view.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import butterknife.BindView
import nich.work.aequorea.R
import nich.work.aequorea.common.EventObserver
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.common.arch.paging.ListFragment
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.di.Injectable
import nich.work.aequorea.common.di.ViewModelProviderFactory
import nich.work.aequorea.common.ui.widget.MaterialSearchView
import nich.work.aequorea.common.ui.widget.NestedScrollAppBarLayout
import nich.work.aequorea.common.ui.widget.StatusBarView
import nich.work.aequorea.common.utils.*
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.data.entity.search.SearchDatum
import nich.work.aequorea.ui.adapter.InstantSearchAdapter
import java.util.*
import javax.inject.Inject

class HomeFragment : ListFragment<Datum, Datum>(), Injectable {

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

    private var mClickTime: Long = 0

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var mViewModel: HomeViewModel
    private lateinit var mAdapter: HomeAdapter

    override fun provideContentView(): View {
        return inflate(R.layout.fragment_home)
    }

    override fun provideViewModel(): ListViewModel<Datum, Datum> {
        mViewModel = viewModelProvider(factory)
        return mViewModel
    }

    override fun provideAdapter(): ListAdapter<Datum> {
        mAdapter = HomeAdapter()
        return mAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initSearch()
        initAppBar()

        initStatusBar()

        mViewModel.searchResult.observe(this, Observer { it ->
            it?.let { showSearchResult(it) }
        })

        mViewModel.snackBar.observe(this, EventObserver {
            SnackbarUtils.show(coordinatorLayout, it)
        })
    }

    private fun showSearchResult(resultList: List<SearchDatum>?) {
        resultList?.let {
            if (searchView.searchEditText.isNotEmpty() && it.isNotEmpty()) {
                val arrayList = ArrayList<InstantSearchAdapter.InstantSearchBean>()
                val size = if (it.size > 5) 5 else it.size
                if (size > 0) {
                    for (i in 0 until size) {
                        val content = it[i].content
                        arrayList.add(i, InstantSearchAdapter.InstantSearchBean(content.id, content.title))
                    }
                    val mSearchAdapter = InstantSearchAdapter(context, arrayList)
                    searchView.setAdapter(mSearchAdapter)

                    searchView.showSuggestions()
                }
            } else {
                searchView.setAdapter(null)
                searchView.dismissSuggestions()
            }
        }
    }

    private fun initToolbar() {
        toolbar.title = resources.getString(R.string.app_name)
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> consume { IntentUtils.startSettingsActivity(context) }
                R.id.action_search -> consume { searchView.showSearch() }
                else -> consume {}
            }
        }
        toolbar.setOnClickListener {
            if (System.currentTimeMillis() - mClickTime < 200) {
                val position = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                scrollToTop(position)
            }
            mClickTime = System.currentTimeMillis()
        }
    }

    private fun initSearch() {
        searchMask.setOnClickListener {
            if (it.visibility == View.VISIBLE) {
                searchView.closeSearch()
                it.visibility = View.GONE
            }
        }
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
                            mViewModel.getSearchList(newText)
                            return false
                        }
                    })
            setOnItemClickListener { _, _, _, _ ->
                val bean = view?.getTag(R.string.app_name) as InstantSearchAdapter.InstantSearchBean
                bean.let {
                    IntentUtils.startArticleActivity(context, it.id)
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

    private fun initAppBar() {
        appBarLayout.setOnNestedListener(object : NestedScrollAppBarLayout.OnNestedScrollListener {
            override fun onNestedScrolling() {
                updateStatusBarStyle()
            }

            override fun onStopNestedScrolling() {
                updateStatusBarStyle()
            }
        })
    }

    private fun initStatusBar() {
        if (isInLightTheme()) {
            activity?.let { DisplayHelper.setStatusBarStyle(it, statusBar, true) }
        } else {
            activity?.let { DisplayHelper.setStatusBarStyle(it, statusBar, false) }
        }
    }

    private fun updateStatusBarStyle() {
        // light status bar only show in light theme
        if (isInLightTheme()) {
            if (appBarLayout.y <= -appBarLayout.measuredHeight) {
                if (statusBar.isInitState) {
                    activity?.let { DisplayHelper.setStatusBarStyle(it, statusBar, false) }
                }
            } else {
                if (!statusBar.isInitState) {
                    activity?.let { DisplayHelper.setStatusBarStyle(it, statusBar, true) }
                }
            }
        }
    }

    private fun scrollToTop(currentPosition: Int) {
        if (currentPosition >= 10) {
            mRecyclerView.scrollToPosition(6)
        }
        mRecyclerView.smoothScrollToPosition(0)
    }

}