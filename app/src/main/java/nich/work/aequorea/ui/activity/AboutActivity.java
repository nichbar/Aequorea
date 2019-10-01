package nich.work.aequorea.ui.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.ui.activity.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;

public class AboutActivity extends BaseActivity {
    
    @BindView(R.id.status_bar)
    protected StatusBarView mStatusBar;
    
    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }
    
    @Override
    protected void initModel() {
    
    }
    
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        
        initStatusBarStyle();
    }
    
    @Override
    protected void initPresenter() {
    
    }
    
    protected void initStatusBarStyle() {
        if (isInLightTheme()) {
            setStatusBarStyle(true);
            mStatusBar.setLightMask();
        } else {
            setStatusBarStyle(false);
            mStatusBar.setDarkMask();
        }
    }
}
