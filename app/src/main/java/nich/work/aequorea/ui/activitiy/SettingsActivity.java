package nich.work.aequorea.ui.activitiy;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.rx.RxBus;
import nich.work.aequorea.common.rx.RxEvent;
import nich.work.aequorea.common.ui.activity.BaseActivity;
import nich.work.aequorea.common.ui.widget.ACheckBox;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.utils.DisplayUtils;
import nich.work.aequorea.common.utils.SPUtils;
import nich.work.aequorea.common.utils.ThemeHelper;
import nich.work.aequorea.presenter.SettingsPresenter;
import nich.work.aequorea.ui.view.SettingsView;

public class SettingsActivity extends BaseActivity implements SettingsView {
    
    private SettingsPresenter mPresenter;
    
    private List<ACheckBox> mList;
    
    @BindView(R.id.cb_hd_screenshot)
    protected ACheckBox mHdScreenshotCb;
    @BindView(R.id.cb_enable_text_selection)
    protected ACheckBox mEnableSelectionCb;
    @BindView(R.id.cb_disable_recommend_article)
    protected ACheckBox mDisableRecommendArticleCb;
    @BindView(R.id.cb_dark_theme)
    protected ACheckBox mDarkThemeCb;
    @BindView(R.id.cb_offline_caching)
    protected ACheckBox mOfflineCb;
    @BindView(R.id.container_settings)
    protected ViewGroup mContainer;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.status_bar)
    protected StatusBarView mStatusBar;
    @BindView(R.id.main_content)
    protected CoordinatorLayout mCoordinatorLayout;
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
    
    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings;
    }
    
    @Override
    protected void initModel() {
        // do nothing
    }
    
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        setStatusBarStyle();
    
        mToolbar.setTitle(R.string.settings);
        mToolbar.setNavigationIcon(R.drawable.icon_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    
        mCoordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
    
        mHdScreenshotCb.initStatus(Constants.SP_HD_SCREENSHOT, SPUtils.getBoolean(Constants.SP_HD_SCREENSHOT));
        mDarkThemeCb.initStatus(Constants.THEME, SPUtils.getString(Constants.THEME).equals(Constants.THEME_DARK));
        mDarkThemeCb.setOnClickCallback(new ACheckBox.OnClickCallback() {
            @Override
            public void click(boolean status) {
                currentTheme = status ? Constants.THEME_DARK : Constants.THEME_LIGHT;
                
                ThemeHelper.setTheme(currentTheme);
                setTheme(ThemeHelper.getArticleThemeStyle(currentTheme));
                RxBus.getInstance().post(RxEvent.EVENT_TYPE_CHANGE_THEME, null);
            }
        });
        mOfflineCb.initStatus(Constants.SP_OFFLINE_CACHE, SPUtils.getBoolean(Constants.SP_OFFLINE_CACHE));
        mEnableSelectionCb.initStatus(Constants.SP_ENABLE_SELECTION, SPUtils.getBoolean(Constants.SP_ENABLE_SELECTION));
        mDisableRecommendArticleCb.initStatus(Constants.SP_DISABLE_RECOMMEND_ARTICLE, SPUtils.getBoolean(Constants.SP_DISABLE_RECOMMEND_ARTICLE));
        
        mList = new ArrayList<>();
        mList.add(mHdScreenshotCb);
        mList.add(mDarkThemeCb);
        mList.add(mOfflineCb);
        mList.add(mEnableSelectionCb);
        mList.add(mDisableRecommendArticleCb);
        
        colorCheckBox();
    }
    
    @Override
    protected void initPresenter() {
        mPresenter = new SettingsPresenter();
        mPresenter.attach(this);
    }
    
    @Override
    public void onThemeSwitch() {
        setTheme(ThemeHelper.getThemeStyle(Aequorea.getCurrentTheme()));
        currentTheme = Aequorea.getCurrentTheme();
    
        setStatusBarStyle();
    
        int primaryColor = ThemeHelper.getResourceColor(this, R.attr.colorPrimary);
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
    
        mToolbar.setTitleTextColor(titleColor);
        mToolbar.setBackgroundColor(primaryColor);
        
        Drawable drawable = getDrawable(R.drawable.icon_ab_back_material);
        if (drawable != null) {
            drawable.setTint(ThemeHelper.getResourceColor(this, R.attr.colorControlNormal));
        }
        mToolbar.setNavigationIcon(drawable);
        
        mCoordinatorLayout.setBackgroundColor(primaryColor);
        colorCheckBox();
    }
    
    private void colorCheckBox() {
        int titleColor = ThemeHelper.getResourceColor(this, R.attr.title_color);
        int accentColor = ThemeHelper.getResourceId(this, R.attr.colorAccent);
        int subTitleColor = ThemeHelper.getResourceColor(this, R.attr.subtitle_color);
    
        ColorStateList stateList = new ColorStateList(new int[][]{{ -android.R.attr.state_checked }, { android.R.attr.state_checked }},
            new int[]{0x8A000000, ContextCompat.getColor(this, accentColor)});
    
        for (ACheckBox cb : mList) {
            cb.setColorStateList(stateList);
            cb.setTextColor(titleColor, subTitleColor);
        }
    }
    
    protected void setStatusBarStyle() {
        if (isInLightTheme()) {
            setStatusBarStyle(true);
            mStatusBar.setLightMask();
        } else {
            setStatusBarStyle(false);
            mStatusBar.setDarkMask();
        }
    }
}
