package nich.work.aequorea.common.utils;

import android.widget.Toast;

import nich.work.aequorea.Aequorea;

public class ToastUtils {
    private static Toast mToast;
    
    public static void showShortToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(Aequorea.Companion.getApp(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
