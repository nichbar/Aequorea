package nich.work.aequorea.main.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.ui.widget.SwipeBackCoordinatorLayout;
import nich.work.aequorea.main.model.ArticleModel;
import nich.work.aequorea.main.model.article.Data;
import nich.work.aequorea.main.presenters.ArticlePresenter;

public class ArticleActivity extends BaseActivity {
    private ArticlePresenter mPresenter;
    private ArticleModel mModel;

    @BindView(R.id.tv_article_content) TextView mContentTv;
    @BindView(R.id.tv_author) TextView mAuthorTv;
    @BindView(R.id.tv_date) TextView mDateTv;
    @BindView(R.id.tv_title) TextView mTitleTv;
    @BindView(R.id.tv_tag) TextView mTagTv;
    @BindView(R.id.layout_swipe_back_article) SwipeBackCoordinatorLayout mSwipeBackLayout;
    @BindView(R.id.layout_container_article) CoordinatorLayout mContainer;
    @BindView(R.id.scrollview) NestedScrollView mScrollView;
    @BindView(R.id.status_bar) StatusBarView mStatusBar;

    private SwipeBackCoordinatorLayout.OnSwipeListener mSwipeBackListener = new SwipeBackCoordinatorLayout.OnSwipeListener() {
        @Override
        public boolean canSwipeBack(int dir) {
            return SwipeBackCoordinatorLayout.canSwipeBackForThisView(mScrollView, dir);
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

        mTitleTv.setText(article.getTitle());
        mDateTv.setText(article.getDisplayTime());

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

    }
}
