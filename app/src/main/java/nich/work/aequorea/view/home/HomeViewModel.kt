package nich.work.aequorea.view.home

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import nich.work.aequorea.common.Event
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.common.utils.FilterUtils
import nich.work.aequorea.data.entity.Data
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.data.entity.search.SearchDatum
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class HomeViewModel(application: Application, private var apiService: ApiService)
    : ListViewModel<Datum, Datum>(application, DEFAULT_PAGE_SIZE) {

    private var mPublishSubject = PublishSubject.create<String>()

    var searchResult = MutableLiveData<List<SearchDatum>>()
        private set

    var snackBar = MutableLiveData<Event<String>>()
        private set

    init {
        mPublishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe { doSearch(it) }
    }

    override fun decorateListDataAsItemListData(listData: List<Datum>): List<Datum> {
        return FilterUtils.filterData(listData)
    }

    override fun load(page: Int): Single<List<Datum>> {
        return mapData(apiService.getMainPageInfo(page, DEFAULT_PAGE_SIZE))
    }

    private fun mapData(data: Single<Data>): Single<List<Datum>> {
        return data.map { d -> d.data }
    }

    fun getSearchList(searchContent: String) {
        mPublishSubject.onNext(searchContent)
    }

    private fun doSearch(searchContent: String) {
        apiService.getArticleListWithKeyword(1, 10, searchContent, false)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { searchResult.postValue(FilterUtils.filterSearchData(it.data)) },
                        {
                            it.printStackTrace()
                            snackBar.postValue(it.message?.let { msg -> Event(msg) })
                        }
                )
    }

}