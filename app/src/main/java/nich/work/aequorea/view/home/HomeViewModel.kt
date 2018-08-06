package nich.work.aequorea.view.home

import android.app.Application
import io.reactivex.Single
import nich.work.aequorea.common.AppExecutor
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.common.utils.FilterUtils
import nich.work.aequorea.data.entity.Data
import nich.work.aequorea.data.entity.Datum

class HomeViewModel(application: Application, var appExecutor: AppExecutor, var apiService: ApiService) : ListViewModel<Datum, Datum>(application, DEFAULT_PAGE_SIZE) {

    override fun decorateListDataAsItemListData(listData: List<Datum>): List<Datum> {
        return FilterUtils.filterData(listData)
    }

    override fun load(page: Int): Single<List<Datum>> {
        return mapData(apiService.getMainPageInfo(page, DEFAULT_PAGE_SIZE))
    }

    private fun mapData(data: Single<Data>): Single<List<Datum>> {
        return data.map { d -> d.data }
    }

}