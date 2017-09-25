package nich.work.aequorea.common.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.view.BaseView;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.ThemeHelper;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    public String currentTheme;
    public boolean needToReTheme;
    
    private boolean isForeground;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        
        setContentView(getContentViewId());
        initModel();
        initView();
        initPresenter();
    }
    
    protected void initTheme() {
        currentTheme = ThemeHelper.getTheme();
        setTheme(ThemeHelper.getThemeStyle(currentTheme));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        isForeground = true;
        if (needToReTheme) {
            onThemeSwitchPending();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        isForeground = false;
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
    
    protected abstract int getContentViewId();
    
    protected abstract void initModel();
    
    protected abstract void initView();
    
    protected abstract void initPresenter();
    
    // For those activity that need to change color when user have changed theme.
    @Override
    public void onThemeSwitch() {
        // do nothing
    }
    
    @Override
    public void onThemeSwitchPending() {
        // do nothing
    }
    
    public boolean isInLightTheme() {
        return currentTheme.equals(Constants.THEME_LIGHT);
    }
    
    public boolean activityInForeground() {
        return isForeground;
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }
}
