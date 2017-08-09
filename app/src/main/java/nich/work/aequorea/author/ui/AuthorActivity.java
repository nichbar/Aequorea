package nich.work.aequorea.author.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.utils.DisplayUtils;

public class AuthorActivity extends BaseActivity {

    @BindView(R.id.status_bar) StatusBarView statusBar;
    @BindView(R.id.text) TextView tv;
    @BindView(R.id.container_collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.main_content) CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        initModel();
        initView();
        initPresenter();
    }

    private void initModel() {
    }

    private void initView() {
        ButterKnife.bind(this);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< 1000; i++){
            builder.append("gundam exia gumdam exia");
        }

        tv.setText(builder);
        setStatusBarStyle(true);

        collapsingToolbarLayout.setTitle("Gundam");
        coordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        statusBar.setLightMask();
    }

    private void initPresenter() {
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
