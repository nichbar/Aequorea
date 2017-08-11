package nich.work.aequorea.main.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.ui.widget.SwipeBackCoordinatorLayout;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.main.model.ArticleModel;
import nich.work.aequorea.main.model.article.Data;
import nich.work.aequorea.main.presenter.ArticlePresenter;

public class ArticleActivity extends BaseActivity {
    private ArticlePresenter mPresenter;
    private ArticleModel mModel;

    @BindView(R.id.tv_article_content) TextView mContentTv;
    @BindView(R.id.tv_author) TextView mAuthorTv;
    @BindView(R.id.tv_date) TextView mDateTv;
    @BindView(R.id.tv_title) TextView mTitleTv;
    @BindView(R.id.tv_tag) TextView mTagTv;
    @BindView(R.id.loading_progressbar) ProgressBar mProgressBar;
    @BindView(R.id.container_refresh) View mRefreshView;
    @BindView(R.id.layout_swipe_back_article) SwipeBackCoordinatorLayout mSwipeBackLayout;
    @BindView(R.id.layout_container_article) CoordinatorLayout mContainer;
    @BindView(R.id.scrollview) NestedScrollView mScrollView;
    @BindView(R.id.status_bar) StatusBarView mStatusBar;
    
    @OnClick(R.id.tv_author)
    void gotoAuthorPage() {
        IntentUtils.startAuthorActivity(this, mModel.getData().getAuthors().get(0).getId());
    }
    
    @OnClick(R.id.container_refresh) void refresh() {
        mPresenter.load(mModel.getId());
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private SwipeBackCoordinatorLayout.OnSwipeListener mSwipeBackListener = new SwipeBackCoordinatorLayout.OnSwipeListener() {
        @Override
        public boolean canSwipeBack(int dir) {
            return true;
        }

        @Override
        public void onSwipeProcess(float percent) {
            mContainer.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
        }

        @Override
        public void onSwipeFinish(int dir) {
            finish();
            switch (dir) {
                case SwipeBackCoordinatorLayout.UP_DIR:
                    overridePendingTransition(0, R.anim.activity_slide_out_top);
                    break;

                case SwipeBackCoordinatorLayout.DOWN_DIR:
                    overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        initModel();
        initView();
        initPresenter();
    }

    private void initModel() {
        mModel = new ArticleModel();
        mModel.setId(getIntent().getLongExtra(Constants.ARTICLE_ID, 0));
    }

    private void initView() {
        ButterKnife.bind(this);

        setStatusBarStyle(true);
        mStatusBar.setLightMask();
        mSwipeBackLayout.setOnSwipeListener(mSwipeBackListener);
    }

    private void initPresenter() {
        if (mPresenter == null){
            mPresenter = new ArticlePresenter();
        }
        mPresenter.attach(this);
        mPresenter.load(mModel.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    public void onUpdate(Data article) {
        mModel.setData(article);
        
        mProgressBar.setVisibility(View.GONE);
        mRefreshView.setVisibility(View.GONE);
        
        mTitleTv.setText(article.getTitle());
    
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        String targetDateString = null;
        try {
            Date date = sourceDateFormat.parse(article.getDisplayTime());
            targetDateString = targetDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
        mDateTv.setText(targetDateString);

        if (article.getAuthors().size() != 0 && article.getAuthors().get(0) != null) {
            mAuthorTv.setText(article.getAuthors().get(0).getName());
        } else {
            mAuthorTv.setText(R.string.default_author);
        }

        if (article.getTopics() != null && article.getTopics().size() != 0 && article.getTopics().get(0) != null) {
            mTagTv.setText(String.format("# %s", article.getTopics().get(0).getName()));
        } else {
            mTagTv.setVisibility(View.GONE);
        }

        RichText.from(article.getContent()).into(mContentTv);
    }

    public void onError(Throwable throwable) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshView.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }
}
