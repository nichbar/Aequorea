package nich.work.aequorea.ui.holder;

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
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.ui.adapters.MainArticleAdapter;

public class NormalArticleHolder extends MainArticleAdapter.ViewHolder {
    private Datum articleData;
    private Context context;
    
    @BindView(R.id.iv_article)
    protected ImageView articleImg;
    @BindView(R.id.iv_author)
    protected ImageView authorImg;
    @BindView(R.id.iv_author_2)
    protected ImageView authorImg2;
    @BindView(R.id.iv_author_3)
    protected ImageView authorImg3;
    @BindView(R.id.tv_author_linker_1)
    protected TextView linker_1;
    @BindView(R.id.tv_author_linker_2)
    protected TextView linker_2;
    @BindView(R.id.tv_article_title)
    protected TextView titleText;
    @BindView(R.id.tv_author)
    protected TextView authorText;
    @BindView(R.id.tv_like)
    protected TextView likeText;
    
    @OnClick(R.id.iv_article)
    protected void startArticleActivity() {
        IntentUtils.startArticleActivity(context, articleData.getId());
    }
    
    @OnClick(R.id.container_author)
    protected void startAuthorActivity() {
        if (authorText.getVisibility() == View.VISIBLE) {
            startAuthorActivity(0, authorImg);
        }
    }
    
    @OnClick({R.id.iv_author, R.id.iv_author_2, R.id.iv_author_3})
    protected void onClick(View view) {
        int id = 0;
        if (view.equals(authorImg)) {
            id = 0;
        } else if (view.equals(authorImg2)) {
            id = 1;
        } else if (view.equals(authorImg3)) {
            id = 2;
        }
        
        if (articleData.getAuthors().get(id) != null) {
            startAuthorActivity(id, view);
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
        ImageHelper.setImage(context, articleData.getCoverUrl(), articleImg, ImageHelper.generateRandomPlaceholder(articleData.getCoverUrl()));
        
        // article title
        titleText.setText(articleData.getTitle());
        
        // author
        if (articleData.getAuthors() != null) {
            int size = articleData.getAuthors().size() > 3 ? 3 : articleData.getAuthors().size();
            
            ImageHelper.setImage(context, articleData.getAuthors()
                .get(0)
                .getAvatar(), authorImg, true, R.drawable.icon_author);
            authorText.setText(articleData.getAuthors().get(0).getName());
            
            if (size == 1) {
                linker_1.setVisibility(View.GONE);
                linker_2.setVisibility(View.GONE);
                authorImg3.setVisibility(View.GONE);
                authorImg2.setVisibility(View.GONE);
                authorText.setVisibility(View.VISIBLE);
            }
            
            if (size >= 2) {
                linker_1.setVisibility(View.VISIBLE);
                authorImg2.setVisibility(View.VISIBLE);
                ImageHelper.setImage(context, articleData.getAuthors().get(1).getAvatar(), authorImg2, true, R.drawable.icon_author);
                authorText.setVisibility(View.GONE);
            }
    
            if (size >= 3) {
                linker_2.setVisibility(View.VISIBLE);
                authorImg3.setVisibility(View.VISIBLE);
                ImageHelper.setImage(context, articleData.getAuthors().get(2).getAvatar(), authorImg3, true, R.drawable.icon_author);
            }
        }
        
        // like
        Long likeCount = articleData.getLikeTimes() == null ? 0 : articleData.getLikeTimes();
        likeText.setText(Long.toString(likeCount));
    }
    
    private void startAuthorActivity(int i, View view) {
        if (articleData.getAuthors().get(i) != null) {
            IntentUtils.startAuthorActivity(context, view, articleData.getAuthors().get(i));
        }
    }
}
