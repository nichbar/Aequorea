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
import nich.work.aequorea.main.ui.holder.ThemeArticleHolder;

public class MainArticleAdapter extends RecyclerView.Adapter<MainArticleAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;

    private List<Integer> typeList;

    private List<Datum> articleList;

    public MainArticleAdapter(Context context, List<Datum> articleList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.articleList = articleList;

        updateTypeList();
    }

    private void updateTypeList() {
        typeList = new ArrayList<>();

        if (articleList != null){
            for (Datum data : articleList){
                switch (data.getType()){
                    case Constants.ARTICLE_TYPE_MAGAZINE:
                        typeList.add(Constants.TYPE_MAGAZINE);
                        break;
                    case Constants.ARTICLE_TYPE_THEME:
                        typeList.add(Constants.TYPE_THEME);
                        break;
                    case Constants.ARTICLE_TYPE_TOP_ARTICLE:
                        typeList.add(Constants.TYPE_TOP_ARTICLE);
                        break;
                    case Constants.ARTICLE_TYPE_NORMAL:
                    default:
                        typeList.add(Constants.TYPE_NORMAL);
                        break;
                }
            }
        }
    }


    public void setArticleList(List<Datum> articleList) {
        this.articleList = articleList;
        updateTypeList();
    }

    public List<Datum> getArticleList(){
        return articleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.TYPE_NORMAL:
                return new NormalArticleHolder(inflater.inflate(R.layout.item_article, parent, false));
//            case Constants.TYPE_THEME:
//                return new ThemeArticleHolder(inflater.inflate(R.layout.item_article_theme, parent, false));
            // Todo
            default:
                return new NormalArticleHolder(inflater.inflate(R.layout.item_article, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(context, articleList.get(position));
    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 : articleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return typeList.get(position);
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onBindView(Context context, Datum data);
    }
}
