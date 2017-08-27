package nich.work.aequorea.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import nich.work.aequorea.common.ui.activities.BaseActivity;

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
        Window window = activity.getWindow();
        View decor = window.getDecorView();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLight) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  // support MIUI 7.7+
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
    
    public static void setStatusInLowProfileMode(BaseActivity activity, boolean isLightStatusBar) {
        View decor = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLightStatusBar) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LOW_PROFILE);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
    
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getApplicationContext().getResources().getDisplayMetrics());
    }
    
    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getApplicationContext().getResources().getDisplayMetrics());
    }
    
    // take a screen shot of given nestedScrollView
    public static Bitmap shotNestedScrollView(NestedScrollView nestedScrollView, int backgroundColor) {
        
        View childView = nestedScrollView.getChildAt(0);
        
        childView.setBackgroundColor(backgroundColor);
    
        Bitmap bitmap = Bitmap.createBitmap(nestedScrollView.getWidth(), childView.getHeight(), Bitmap.Config.RGB_565);
    
        Canvas canvas = new Canvas(bitmap);
        nestedScrollView.draw(canvas);
    
        childView.setBackgroundColor(Color.TRANSPARENT);
    
        return bitmap;
    }
}
