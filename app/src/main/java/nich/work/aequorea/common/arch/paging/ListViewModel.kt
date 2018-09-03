package nich.work.aequorea.common.arch.paging

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

/**
 * LD -> ListData means data entity from internet
 * ILD -> ItemListData means decorated data entity for displaying.
 */
abstract class ListViewModel<LD, ILD>(application: Application, var pageSize: Int) : AndroidViewModel(application), ListRepository.CallMethod<List<LD>> {

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    var listRepository: ListRepository<LD> = ListRepository(this, application)

    var refreshStatus: LiveData<LoadingStatus> = listRepository.refreshStatus
    var loadMoreStatus: LiveData<LoadingStatus> = listRepository.loadMoreStatus
    var listLiveData: LiveData<List<LD>> = listRepository.listLiveData

    val itemListLiveData: MediatorLiveData<List<ILD>> = MediatorLiveData()

    init {
        itemListLiveData.addSource(listLiveData) { listData ->
            postItemListLiveData(listData!!)
        }
    }

    /**
     * Override this method to merge 3rd party entity with List<LD> into List<ILD>.
     */
    abstract fun decorateListDataAsItemListData(listData: List<LD>): List<ILD>

    fun postItemListLiveData() {
        listLiveData.value?.let(this@ListViewModel::postItemListLiveData)
    }

    private fun postItemListLiveData(listData: List<LD>) {
        itemListLiveData.postValue(decorateListDataAsItemListData(listData))
    }

    open fun initialLoad() {
        listRepository.load(LoadType.INITIAL)
    }

    open fun loadMore() {
        listRepository.load(LoadType.LOAD_MORE)
    }

    open fun refresh() {
        listRepository.load(LoadType.REFRESH)
    }

    override fun reachTheEnd(itemCount: Int): Boolean {
        return itemCount < pageSize
    }

}