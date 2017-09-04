package nich.work.aequorea.main.ui.holder;

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
    private Datum wrapperData;
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
//            case ARTICLE_TYPE_MAGAZINE:
//                SnackBarUtils.show(articleImg, mContext.getString(R.string.cover_story_not_supported));
//                break;
//            case ARTICLE_TYPE_NORMAL:
//            case ARTICLE_TYPE_THEME:
                // TODO: Add activity to fit type.
//            default:
//                SnackBarUtils.show(articleImg, mContext.getString(R.string.not_supported) + data.getType());
//                break;
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

    @Override
    public void onBindView(Context context, Datum data) {
        this.context = context;

        wrapperData = data;
        articleData = wrapperData.getData().get(0);


        // article cover
        ImageHelper.setImage(context, articleData.getCoverUrl(), articleImg, R.color.colorPrimary_dark);

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
