package nich.work.aequorea.main.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.main.model.ArticleModel;
import nich.work.aequorea.main.model.article.Article;
import nich.work.aequorea.main.presenters.ArticlePresenter;

public class ArticleActivity extends BaseActivity {
    private ArticlePresenter mPresenter;
    private ArticleModel mModel;

    @BindView(R.id.tv_article_content) TextView mContentTv;

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

    public void onUpdate(Article article) {
        mContentTv.setText(Html.fromHtml(article.getData().getContent()));
    }

    public void onError(Throwable throwable) {

    }
}
