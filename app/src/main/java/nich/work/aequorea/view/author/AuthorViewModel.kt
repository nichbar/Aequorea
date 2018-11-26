package nich.work.aequorea.view.author

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.common.runOnIoThread
import nich.work.aequorea.data.entity.Author
import nich.work.aequorea.data.entity.Datum

class AuthorViewModel(application: Application, private var apiService: ApiService)
    : ListViewModel<Datum, Datum>(application, DEFAULT_PAGE_SIZE) {

    var authorId: Long = 0
    var author: Author? = null

    var authorInfo = MutableLiveData<Author>()

    override fun decorateListDataAsItemListData(listData: List<Datum>): List<Datum> {
        if (authorInfo.value == null && listData.isNotEmpty()) {
            findAuthorInfo(listData[0])
        }
        return listData
    }

    override fun load(page: Int): Single<List<Datum>> {
        return apiService.getArticleList(authorId, page, DEFAULT_PAGE_SIZE).map { d -> d.data }
    }

    private fun findAuthorInfo(datum: Datum) {
        runOnIoThread {
            for (author in datum.authors) {
                // sometimes the leader may mark as the first author, so we need to make sure it's the right author
                if (author.id == authorId) {
                    // lack article count
                    // author.meta = datum.getMeta()
                    this.author = author
                    authorInfo.postValue(author)
                }
            }
        }
    }

}