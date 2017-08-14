package nich.work.aequorea.common.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import nich.work.aequorea.common.utils.DisplayUtils;

public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    public void setStatusBarStyle(boolean isLightStatusBar) {
        DisplayUtils.setStatusBarStyle(this, isLightStatusBar);
    }
    
    public void setStatusBarInLowProfileMode() {
        DisplayUtils.setStatusInLowProfileMode(this);
    }
}
