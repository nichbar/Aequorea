package nich.work.aequorea.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import nich.work.aequorea.Aequorea;

public class NetworkUtils {

    public static boolean isNetworkAvailable() {
        Context context = Aequorea.getApp();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }
}
