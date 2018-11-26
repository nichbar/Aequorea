package nich.work.aequorea.common.arch.paging

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import nich.work.aequorea.common.network.Response
import nich.work.aequorea.common.utils.NetworkUtils
import nich.work.aequorea.data.NetworkError

class ListRepository<T>(private val mCallMethod: CallMethod<List<T>>, private var mApplication: Application) {

    private var mPage = 1

    private val mList = ArrayList<T>()

    val listLiveData = MutableLiveData<List<T>>()
    val refreshStatus = MutableLiveData<LoadingStatus>()
    val loadMoreStatus = MutableLiveData<LoadingStatus>()

    var reachTheEnd = false

    fun load(type: LoadType) {

        if (!NetworkUtils.isNetworkAvailable(mApplication)) {
            when (type) {
                LoadType.INITIAL, LoadType.LOAD_MORE -> loadMoreStatus.postValue(LoadingStatus.errorNoInternetConnection())
                LoadType.REFRESH -> refreshStatus.postValue(LoadingStatus.errorNoInternetConnection())
            }
            return
        }

        when (type) {
            LoadType.INITIAL -> {

                if (loadMoreStatus.value?.status == LoadingStatus.Status.LOADING) return
                loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.LOADING))

                // mList is not empty means that the view had just recreated itself.
                if (mList.size != 0) {
                    loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.SUCCESS))
                    if (reachTheEnd) loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.REACH_THE_END))

                    listLiveData.postValue(mList)
                } else {
                    loadMore()
                }
            }

            LoadType.LOAD_MORE -> {
                loadMore()
            }

            LoadType.REFRESH -> {
                refresh()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun loadMore() {
        if (loadMoreStatus.value?.status == LoadingStatus.Status.LOADING || refreshStatus.value?.status == LoadingStatus.Status.LOADING) return

        loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.LOADING))

        mCallMethod.load(mPage).subscribeOn(Schedulers.io())
                .subscribe(object : Response<List<T>>() {
                    override fun onSuccess(data: List<T>) {
                        loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.SUCCESS))

                        if (data.isEmpty() || mCallMethod.reachTheEnd(data.size) || reachTheEnd) {
                            reachTheEnd = true
                            loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.REACH_THE_END))
                        } else {
                            mPage++
                        }

                        mList.addAll(data)
                        listLiveData.postValue(mList)
                    }

                    override fun onFailure(error: NetworkError) {
                        loadMoreStatus.postValue(LoadingStatus(status = LoadingStatus.Status.ERROR, message = error.message))
                    }
                })
    }

    @SuppressLint("CheckResult")
    private fun refresh() {
        if (refreshStatus.value?.status == LoadingStatus.Status.LOADING) return

        mPage = 1
        mList.clear()
        reachTheEnd = false

        refreshStatus.postValue(LoadingStatus(status = LoadingStatus.Status.LOADING))

        mCallMethod.load(mPage).subscribeOn(Schedulers.io())
                .subscribe(object : Response<List<T>>() {
                    override fun onSuccess(data: List<T>) {
                        mPage++
                        mList.addAll(data)
                        listLiveData.postValue(mList)
                        refreshStatus.postValue(LoadingStatus(status = LoadingStatus.Status.SUCCESS))
                    }

                    override fun onFailure(error: NetworkError) {
                        refreshStatus.postValue(LoadingStatus(status = LoadingStatus.Status.ERROR, message = error.message))
                    }
                })
    }

    interface CallMethod<V> {
        fun load(page: Int): Single<V>

        // Check if the list meets the end by comparing itemCount with preset pageSize.
        fun reachTheEnd(itemCount: Int): Boolean
    }
}