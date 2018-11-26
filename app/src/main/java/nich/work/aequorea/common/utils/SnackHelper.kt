package nich.work.aequorea.common.utils

import com.google.android.material.snackbar.Snackbar
import android.view.View

object SnackHelper {
    fun show(view: View, content: String) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show()
    }

    fun show(view: View, content: String, duration: Int) {
        Snackbar.make(view, content, duration).show()
    }

    fun getSnackbar(view: View, content: String, duration: Int): Snackbar {
        return Snackbar.make(view, content, duration)
    }
}