package nich.work.aequorea.common.arch.paging

data class LoadingStatus(var status: Status, var message: String = "", var errorType: ErrorType = ErrorType.UNKNOWN) {

    companion object {
        fun errorNoInternetConnection(): LoadingStatus {
            return LoadingStatus(status = Status.ERROR, errorType = ErrorType.NO_INTERNET_CONNECTION)
        }
    }

    enum class Status {
        ERROR,

        SUCCESS,

        LOADING,

        REACH_THE_END
    }

    enum class ErrorType {
        UNKNOWN,

        NO_INTERNET_CONNECTION
    }
}