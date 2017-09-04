package nich.work.aequorea.main.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.ui.holder.NormalArticleHolder;

public class MainArticleAdapter extends RecyclerView.Adapter<MainArticleAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<Integer> mTypeList;

    private List<Datum> mArticleList;

    public MainArticleAdapter(Context context, List<Datum> articleList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mArticleList = articleList;

        updateTypeList();
    }

    private void updateTypeList() {
        mTypeList = new ArrayList<>();

        if (mArticleList != null){
            for (Datum data : mArticleList){
                switch (data.getType()){
                    case Constants.ARTICLE_TYPE_MAGAZINE:
                        mTypeList.add(Constants.TYPE_MAGAZINE);
                        break;
                    case Constants.ARTICLE_TYPE_THEME:
                        mTypeList.add(Constants.TYPE_THEME);
                        break;
                    case Constants.ARTICLE_TYPE_TOP_ARTICLE:
                        mTypeList.add(Constants.TYPE_TOP_ARTICLE);
                        break;
                    case Constants.ARTICLE_TYPE_NORMAL:
                    default:
                        mTypeList.add(Constants.TYPE_NORMAL);
                        break;
                }
            }
        }
    }
    
    public void setArticleList(List<Datum> articleList, boolean isRefresh) {
        if (this.mArticleList == null || isRefresh) {
            this.mArticleList = articleList;
        } else {
            for (Datum d : articleList) {
                if (!this.mArticleList.contains(d)) {
                    this.mArticleList.add(d);
                }
            }
        }
        updateTypeList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.TYPE_NORMAL:
                return new NormalArticleHolder(mInflater.inflate(R.layout.item_article, parent, false));
//            case Constants.TYPE_THEME:
//                return new ThemeArticleHolder(mInflater.inflate(R.layout.item_article_theme, parent, false));
            // Todo
            default:
                return new NormalArticleHolder(mInflater.inflate(R.layout.item_article, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(mContext, mArticleList.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onBindView(Context context, Datum data);
    }
}
