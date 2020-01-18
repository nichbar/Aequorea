package nich.work.aequorea.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import androidx.core.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nich.work.aequorea.R;
import nich.work.aequorea.common.ui.activity.BaseActivity;

public class DisplayUtils {
    
    public static int getStatusBarHeight(Resources r) {
        int result = 0;
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId);
        }
        return result;
    }
    
    public static void setStatusBarStyle(BaseActivity activity, boolean isLight) {
        boolean isMIUI = setMIUIStatusBarStyle(activity, isLight);

        if (!isMIUI) {
            Window window = activity.getWindow();
            View decor = window.getDecorView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLight) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }
    
    public static void setStatusInLowProfileMode(BaseActivity activity, boolean isLight) {
        View decor = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLight) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LOW_PROFILE);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    private static boolean setMIUIStatusBarStyle(BaseActivity activity, boolean isLight) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isLight) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (isLight) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }
    
    public static void cancelTranslucentNavigation(Activity a) {
        a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getApplicationContext().getResources().getDisplayMetrics());
    }
    
    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getApplicationContext().getResources().getDisplayMetrics());
    }
    
    // take a screen shot of given nestedScrollView
    public static Bitmap shotNestedScrollView(NestedScrollView nestedScrollView, int backgroundColor) {
    
        int recHeight = 0;
        View childView = nestedScrollView.getChildAt(0);
        View recommendationView = nestedScrollView.findViewById(R.id.container_recommendation);
        ViewGroup subRecommendationView = nestedScrollView.findViewById(R.id.container_recommendation_sub);
        
        if (subRecommendationView != null && subRecommendationView.getChildCount() > 0) {
            recHeight = recommendationView.getHeight();
        }
            
        childView.setBackgroundColor(backgroundColor);
    
        Bitmap bitmap = Bitmap.createBitmap(nestedScrollView.getWidth(), childView.getHeight() - recHeight, Bitmap.Config.RGB_565);
    
        Canvas canvas = new Canvas(bitmap);
        nestedScrollView.draw(canvas);
        
        childView.setBackgroundColor(Color.TRANSPARENT);
    
        return bitmap;
    }
}
