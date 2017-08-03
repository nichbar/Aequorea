package nich.work.aequorea.main.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
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
        holder.draweeView.setImageURI(mArticleList.get(position).getData().get(0).getCoverUrl());
        holder.titleTextView.setText(mArticleList.get(position).getData().get(0).getTitle());
    }

    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
    }

    public static class MainPageArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_article)
        SimpleDraweeView draweeView;

        @BindView(R.id.tv_article_title)
        TextView titleTextView;

        public MainPageArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
