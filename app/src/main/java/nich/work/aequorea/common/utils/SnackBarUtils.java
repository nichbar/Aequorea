package nich.work.aequorea.common.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackBarUtils {

    public static void show(View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
    }
}
