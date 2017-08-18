package nich.work.aequorea;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.bilibili.magicasakura.utils.ThemeUtils;

public class Aequorea extends Application implements ThemeUtils.switchColor {
    private static Aequorea mApp;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        ThemeUtils.setSwitchColor(this);
        mApp = this;
    }
    
    public static Application getApp(){
        return mApp;
    };
    
    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        return 0;
    }
    
    @Override
    public int replaceColor(Context context, @ColorInt int color) {
        return 0;
    }
}
