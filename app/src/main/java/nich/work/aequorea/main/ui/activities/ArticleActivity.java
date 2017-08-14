package nich.work.aequorea.main.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.presenter.ArticlePresenter;

public class ArticleActivity extends BaseActivity {
    private ArticlePresenter mPresenter;
    private ArticleModel mModel;
    private LayoutInflater mInflater;
    private static final String TAG = ArticleActivity.class.getSimpleName();
    
    private boolean mIsStatusBarInLowProfileMode = false;

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
    @BindView(R.id.container_recommendation) LinearLayout mRecommendationContainer;
    
    @OnClick(R.id.tv_author)
    void gotoAuthorPage() {
        IntentUtils.startAuthorActivity(this, mModel.getData().getAuthors().get(0).getId());
    }
    
    @OnClick(R.id.container_refresh) void refresh() {
        mRefreshView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.load(mModel.getId());
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
    
    private NestedScrollView.OnScrollChangeListener mScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY - oldScrollY > 20) {
                showLowProfileStatusBar();
            } else if (scrollY - oldScrollY < -20) {
                showOriginalStatusBar();
            }
        }
    };
    
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        boolean actionTap = false;
        float oldY;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    oldY = event.getY();
                    actionTap = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() - oldY > 10) {
                        actionTap = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (actionTap){
                        toggleShowStatus();
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
    
        mInflater = LayoutInflater.from(this);
        
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
        
        mContentTv.setOnTouchListener(mTouchListener);
        
        mScrollView.setOnScrollChangeListener(mScrollChangeListener);
    }

    private void initPresenter() {
        if (mPresenter == null){
            mPresenter = new ArticlePresenter(this);
        }
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
        
        // load recommendation after rendering the context
        mPresenter.loadRecommendedArticles(mModel.getId());
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
    
    public void showOriginalStatusBar() {
        if (mIsStatusBarInLowProfileMode) {
            setStatusBarStyle(true);
            mStatusBar.animate().scaleY(1).setDuration(200);
            mIsStatusBarInLowProfileMode = false;
        }
    }
    
    public void showLowProfileStatusBar() {
        if (!mIsStatusBarInLowProfileMode) {
            setStatusBarInLowProfileMode();
            mStatusBar.animate().scaleY(0).setDuration(200);
            mIsStatusBarInLowProfileMode = true;
        }
    }
    
    private void toggleShowStatus() {
        if (mIsStatusBarInLowProfileMode) {
            showOriginalStatusBar();
        } else {
            showLowProfileStatusBar();
        }
    }
    
    public void onRecommendationLoaded(List<Datum> data) {
        mRecommendationContainer.setVisibility(View.VISIBLE);
        for (Datum d : data) {
            mRecommendationContainer.addView(createRecommendArticle(d));
        }
    }
    
    @SuppressLint("InflateParams")
    private View createRecommendArticle(final Datum datum) {
        View view = mInflater.inflate(R.layout.piece_recommend_article, null);
        
        TextView title = (TextView) view.findViewById(R.id.tv_article_recommend_title);
        title.setText(datum.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startArticleActivity(ArticleActivity.this, datum.getId());
            }
        });
        
        return view;
    }
    
    public void onRecommendationError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }
}
