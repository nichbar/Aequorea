package nich.work.aequorea.author.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.author.model.AuthorModel;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.author.model.entities.Datum;
import nich.work.aequorea.author.presenter.AuthorPresenter;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.StatusBarView;
import nich.work.aequorea.common.ui.widget.glide.CircleTransformation;
import nich.work.aequorea.common.utils.DisplayUtils;

public class AuthorActivity extends BaseActivity {
    
    private AuthorModel mModel;
    private AuthorPresenter mPresenter;
    private boolean mIsFirstPage = true;

    @BindView(R.id.status_bar) StatusBarView mStatusBar;
    @BindView(R.id.text) TextView mTv;
    @BindView(R.id.tv_introduction) TextView mIntroductionTv;
    @BindView(R.id.tv_article_count) TextView mArticleCount;
    @BindView(R.id.iv_author) ImageView mAuthorIv;
    @BindView(R.id.container_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        initModel();
        initView();
        initPresenter();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    private void initModel() {
        mModel = new AuthorModel();
        
        mModel.setAuthorId((int) getIntent().getLongExtra(Constants.AUTHOR_ID, 0));
    }

    private void initView() {
        ButterKnife.bind(this);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< 1000; i++){
            builder.append("gundam exia gumdam exia");
        }

        mTv.setText(builder);
        setStatusBarStyle(true);
        
        mCoordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);
        mStatusBar.setLightMask();
    }
    
    public AuthorModel getModel(){
        return mModel;
    }

    private void initPresenter() {
        mPresenter = new AuthorPresenter();
        mPresenter.attach(this);
        
        mPresenter.load(mModel.getAuthorId());
    }
    
    public void onUpdate(Author a) {
        if (mIsFirstPage) {
            updateAuthorInfo(a);
            mIsFirstPage = false;
        }
        
    }
    
    public void onError(Throwable mThrowable) {
        
    }
    
    
    private void updateAuthorInfo(Author a) {
        List<Datum> data = a.getData();
        
        if (data.size() != 0) {
            for (Author author : data.get(0).getAuthors()) {
                // sometimes the leader may mark as the first author
                if (author.getId() == mModel.getAuthorId()) {
    
                    mCollapsingToolbarLayout.setTitle(author.getName());
                    
                    Glide.with(this)
                        .load(author.getAvatar())
                        .transform(new CircleTransformation(this))
                        .into(mAuthorIv);
                    
                    String intro = author.getIntroduction();
                    if (!TextUtils.isEmpty(intro) && !intro.equals(" ")) {
                        
                        if (intro.contains("。")) {
                            int position = intro.indexOf("。");
                            intro = intro.substring(0, position);
                        }
    
                        mIntroductionTv.setText(intro);
    
                    } else {
                        mIntroductionTv.setText(R.string.default_introduction);
                    }
    
                    mArticleCount.setText(String.format(getString(R.string.article_count), a.getMeta().getTotalCount()));
                    return;
                }
            }
        }
    }
}
