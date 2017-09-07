package nich.work.aequorea.common.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.view.BaseView;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.ThemeHelper;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    public String mTheme;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTheme = ThemeHelper.getTheme();
        setTheme(ThemeHelper.getThemeStyle(mTheme));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    public void setStatusBarStyle(boolean isLightStatusBar) {
        DisplayUtils.setStatusBarStyle(this, isLightStatusBar);
    }
    
    public void setStatusBarInLowProfileMode(boolean isLightStautsBar) {
        DisplayUtils.setStatusInLowProfileMode(this, isLightStautsBar);
    }
    
    public int dp2px(int dp) {
        return DisplayUtils.dp2px(this, dp);
    }
    
    // For those activity that need to change color when user have changed theme.
    @Override
    public void onThemeSwitch() {
        // do nothing
    }
    
    public boolean isInLightTheme() {
        return mTheme.equals(Constants.THEME_LIGHT);
    }
}
