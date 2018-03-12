package nich.work.aequorea.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.ImageHelper;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.model.entity.Datum;

public class MainArticleAdapter extends RecyclerView.Adapter<MainArticleAdapter.ViewHolder> {
    
    private LayoutInflater mInflater;
    private Context mContext;
    
    private List<Datum> mArticleList;
    private Set<Long> mIdSet;
    
    public MainArticleAdapter(Context context, List<Datum> articleList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mArticleList = articleList;
        this.mIdSet = new HashSet<>();
    }
    
    public void setArticleList(List<Datum> articleList, boolean isRefresh) {
        if (this.mArticleList == null) {
            this.mArticleList = articleList;
        } else if (isRefresh) {
            int i = 0;
            for (Datum d : articleList) {
                if (!listContains(d)) {
                    this.mArticleList.add(i++, d);
                }
            }
        } else {
            for (Datum d : articleList) {
                if (!listContains(d)) {
                    this.mArticleList.add(d);
                }
            }
        }
        notifyDataSetChanged();
        updateIdSet();
    }
    
    private void updateIdSet() {
        int size = this.mArticleList.size();
        for (int i = 0; i < size; i++) {
            mIdSet.add(this.mArticleList.get(i).getData().get(0).getId());
        }
    }
    
    private boolean listContains(Datum d) {
        Long id = d.getData().get(0).getId();
        return id != null && mIdSet.contains(id);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleHolder(mInflater.inflate(R.layout.item_article, parent, false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(mContext, mArticleList.get(position));
    }
    
    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
    }
    
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
        
        public abstract void onBindView(Context context, Datum data);
    }
    
    class ArticleHolder extends MainArticleAdapter.ViewHolder {
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
        
        public ArticleHolder(View itemView) {
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
                
                ImageHelper.setImage(context, articleData.getAuthors().get(0).getAvatar(), authorImg, true);
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
                    ImageHelper.setImage(context, articleData.getAuthors().get(1).getAvatar(), authorImg2, true);
                    authorText.setVisibility(View.GONE);
                }
                
                if (size >= 3) {
                    linker_2.setVisibility(View.VISIBLE);
                    authorImg3.setVisibility(View.VISIBLE);
                    ImageHelper.setImage(context, articleData.getAuthors().get(2).getAvatar(), authorImg3, true);
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
}
