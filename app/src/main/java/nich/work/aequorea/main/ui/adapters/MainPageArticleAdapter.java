package nich.work.aequorea.main.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.main.model.mainpage.Datum;

public class MainPageArticleAdapter extends RecyclerView.Adapter<MainPageArticleAdapter.MainPageArticleViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    public List<Datum> mArticleList;

    public MainPageArticleAdapter(Context context, List<Datum> articleList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mArticleList = articleList;
    }

    @Override
    public MainPageArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainPageArticleViewHolder(mInflater.inflate(R.layout.item_article, parent, false));
    }

    @Override
    public void onBindViewHolder(MainPageArticleViewHolder holder, int position) {
        Datum data = mArticleList.get(position).getData().get(0);

        holder.articleImg.setImageURI(data.getCoverUrl());
        holder.titleText.setText(data.getTitle());

        if (data.getAuthors() != null) {
            holder.authorImg.setImageURI(data.getAuthors().get(0).getAvatar());
            holder.authorText.setText(data.getAuthors().get(0).getName());
        }
        Long likeCount = data.getLikeTimes() == null ? 0 : data.getLikeTimes();
        holder.likeText.setText(Long.toString(likeCount));
    }

    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
    }

    public  class MainPageArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_article) SimpleDraweeView articleImg;
        @BindView(R.id.iv_author) SimpleDraweeView authorImg;
        @BindView(R.id.tv_article_title) TextView titleText;
        @BindView(R.id.tv_author) TextView authorText;
        @BindView(R.id.tv_like) TextView likeText;

        @OnClick(R.id.iv_article) void startArticleActivity(){
            if (this.getAdapterPosition() < mArticleList.size()){
                IntentUtils.startArticleActivity(mContext, mArticleList.get(this.getAdapterPosition()).getData().get(0).getId());
            }
        }

        public MainPageArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
