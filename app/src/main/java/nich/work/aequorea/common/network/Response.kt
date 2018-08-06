package nich.work.aequorea.common.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.functions.BiConsumer
import nich.work.aequorea.common.utils.ToastUtils
import nich.work.aequorea.data.NetworkError
import retrofit2.HttpException

abstract class Response<T> : BiConsumer<T, Throwable> {

    override fun accept(t: T, throwable: Throwable?) {
        if (t != null) onSuccess(t)

        if (throwable != null) {
            throwable.printStackTrace()
            when (throwable) {
                is HttpException -> {
                    val response = throwable.response()

                    response?.let {
                        try {
                            val error = Gson().fromJson(response.errorBody()?.string(), NetworkError::class.java)
                            error?.let {
                                onFailure(error)
                            }
                        } catch (e: Exception) {
                            onFailure(NetworkError(message = "${response.code()} error."))
                            e.printStackTrace()
                        }
                    }
                }

                is JsonSyntaxException -> {
                    onFailure(NetworkError(message = "数据解析出错 -> ${throwable.message}"))
                }

                else -> {
                    val message = throwable.message
                    if (message == null) {
                        onFailure(NetworkError(message = "遇到未知问题"))
                    } else {
                        onFailure(NetworkError(message = message))
                    }
                }
            }
        }
    }

    abstract fun onSuccess(data: T)

    open fun onFailure(error: NetworkError) {
        ToastUtils.showShortToast(error.message)
    }

}