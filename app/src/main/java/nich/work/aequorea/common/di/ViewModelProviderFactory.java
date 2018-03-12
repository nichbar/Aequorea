package nich.work.aequorea.common.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * A provider factory that persists ViewModels{@link ViewModel}.
 * Used if the viewmodel has a parameterized constructor.
 */
public class ViewModelProviderFactory<V> implements ViewModelProvider.Factory {
    
    private V viewModel;
    
    public ViewModelProviderFactory(V viewModel) {
        this.viewModel = viewModel;
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(viewModel.getClass())) {
            return (T) viewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}
