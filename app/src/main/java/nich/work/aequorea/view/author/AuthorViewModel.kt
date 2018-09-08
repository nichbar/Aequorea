package nich.work.aequorea.view.author

import android.app.Application
import io.reactivex.Single
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.data.entity.Data
import nich.work.aequorea.data.entity.Datum

class AuthorViewModel(application: Application, private var apiService: ApiService)
    : ListViewModel<Datum, Datum>(application, DEFAULT_PAGE_SIZE) {

    var authorId: Long = 0

    override fun decorateListDataAsItemListData(listData: List<Datum>): List<Datum> {
        return listData
    }

    override fun load(page: Int): Single<List<Datum>> {
        return mapData(apiService.getArticleList(authorId, page, DEFAULT_PAGE_SIZE))
    }

    private fun mapData(data: Single<Data>): Single<List<Datum>> {
        return data.map { d -> d.data }
    }

}