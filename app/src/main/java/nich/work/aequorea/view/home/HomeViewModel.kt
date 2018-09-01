package nich.work.aequorea.view.home

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.common.utils.FilterUtils
import nich.work.aequorea.data.entity.Data
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.data.entity.search.SearchData
import nich.work.aequorea.data.entity.search.SearchDatum
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application, var apiService: ApiService)
    : ListViewModel<Datum, Datum>(application, DEFAULT_PAGE_SIZE) {

    private var mPublishSubject = PublishSubject.create<String>()
    var searchResult = MutableLiveData<List<SearchDatum>>()

    override fun decorateListDataAsItemListData(listData: List<Datum>): List<Datum> {
        return FilterUtils.filterData(listData)
    }

    override fun load(page: Int): Single<List<Datum>> {
        return mapData(apiService.getMainPageInfo(page, DEFAULT_PAGE_SIZE))
    }

    private fun mapData(data: Single<Data>): Single<List<Datum>> {
        return data.map { d -> d.data }
    }

    // TODO Post this searchContent to publishSubject.
    fun getSearchList(searchContent: String) {
        mPublishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe { doSearch(it) }
    }

    private fun doSearch(searchContent: String) {
        apiService.getArticleListWithKeyword(1, 10, searchContent, false)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        Consumer<SearchData> { searchResult.postValue(FilterUtils.filterSearchData(it.data)) },
                        Consumer<Throwable> { it.printStackTrace() }
                )
    }

}