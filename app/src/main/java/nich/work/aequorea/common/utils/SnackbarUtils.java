package nich.work.aequorea.common.utils;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

public class SnackbarUtils {
    
    public static void show(View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
    }
    
    public static void show(View view, String content, int duration) {
        Snackbar.make(view, content, duration).show();
    }
    
    public static Snackbar getSnackbar(View view, String content, int duration) {
        return Snackbar.make(view, content, duration);
    }
}
