package nich.work.aequorea.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nich.work.aequorea.R;
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.ui.holder.NormalArticleHolder;

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
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalArticleHolder(mInflater.inflate(R.layout.item_article, parent, false));
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
}
