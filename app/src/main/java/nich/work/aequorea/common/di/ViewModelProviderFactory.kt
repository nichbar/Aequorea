package nich.work.aequorea.common.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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
