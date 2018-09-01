package nich.work.aequorea.common.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * A provider factory that persists ViewModels[ViewModel].
 * Used if the viewModel has a parameterized constructor.
 */
class ViewModelProviderFactory(private val viewModel: Any) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}
