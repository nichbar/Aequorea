package nich.work.aequorea.common.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import nich.work.aequorea.BuildConfig

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
        provider: ViewModelProvider.Factory
) =
        ViewModelProviders.of(this, provider).get(VM::class.java)


inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
        provider: ViewModelProvider.Factory
) =
        ViewModelProviders.of(this, provider).get(VM::class.java)

/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun <T> LiveData<T?>.observeNonNull(owner: LifecycleOwner, callback: (T) -> Unit) {
    observe(owner, Observer { value ->
        if (value != null) {
            callback(value)
        }
    })
}

inline fun debugOnly(f: () -> Unit) {
    if (BuildConfig.DEBUG) {
        f()
    }
}

inline fun releaseOnly(f: () -> Unit) {
    if (!BuildConfig.DEBUG) {
        f()
    }
}