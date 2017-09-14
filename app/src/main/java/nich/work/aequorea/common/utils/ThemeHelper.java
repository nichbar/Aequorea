package nich.work.aequorea.common.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;

public class ThemeHelper {
    public static String getTheme() {
        String theme = SPUtils.getString(Constants.THEME);
        return "".equals(theme) ? Constants.THEME_LIGHT : theme;
    }
    
    public static void setTheme(String theme) {
        SPUtils.setString(Constants.THEME, theme);
        Aequorea.setCurrentTheme(theme);
    }
    
    public static int getThemeStyle(String theme) {
        switch (theme) {
            default:
            case Constants.THEME_LIGHT:
                return R.style.AppTheme_Light;
            case Constants.THEME_DARK:
                return R.style.AppTheme_Dark;
        }
    }
    
    public static int getArticleThemeStyle(String theme) {
        switch (theme) {
            default:
            case Constants.THEME_LIGHT:
                return R.style.AppTheme_Article_Light;
            case Constants.THEME_DARK:
                return R.style.AppTheme_Article_Dark;
        }
    }
    
    public static int getResourceId(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }
    
    public static int getResourceColor(Context context, int attr){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
    }
}
