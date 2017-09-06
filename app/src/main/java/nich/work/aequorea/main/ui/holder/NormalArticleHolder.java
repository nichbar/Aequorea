package nich.work.aequorea.main.ui.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.ImageHelper;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.ui.adapters.MainArticleAdapter;

public class NormalArticleHolder extends MainArticleAdapter.ViewHolder {
    private Datum articleData;
    private Context context;

    @BindView(R.id.iv_article)
    ImageView articleImg;
    @BindView(R.id.iv_author)
    ImageView authorImg;
    @BindView(R.id.tv_article_title)
    TextView titleText;
    @BindView(R.id.tv_author)
    TextView authorText;
    @BindView(R.id.tv_like)
    TextView likeText;

    @OnClick(R.id.iv_article)
    void startArticleActivity() {
        IntentUtils.startArticleActivity(context, articleData.getId());
    }
    
    @OnClick(R.id.iv_author)
    void startAuthorActivity() {
        if (articleData.getAuthors().get(0).getId() != null) {
            IntentUtils.startAuthorActivity(context, articleData.getAuthors().get(0).getId());
        }
    }

    public NormalArticleHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindView(Context context, Datum data) {
        this.context = context;
    
        articleData = data.getData().get(0);

        // article cover
        ImageHelper.setImage(context, articleData.getCoverUrl(), articleImg, ImageHelper.generateRandomPlaceholder());

        // article title
        titleText.setText(articleData.getTitle());

        // author
        if (articleData.getAuthors() != null) {
            ImageHelper.setImage(context, articleData.getAuthors().get(0).getAvatar(), authorImg, true, R.drawable.icon_author);
            authorText.setText(articleData.getAuthors().get(0).getName());
        }

        // like
        Long likeCount = articleData.getLikeTimes() == null ? 0 : articleData.getLikeTimes();
        likeText.setText(Long.toString(likeCount));
    }
}
