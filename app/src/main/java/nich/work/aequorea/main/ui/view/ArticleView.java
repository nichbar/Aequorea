package nich.work.aequorea.main.ui.view;

import java.util.List;

import nich.work.aequorea.common.ui.view.BaseView;
import nich.work.aequorea.main.model.article.Data;
import nich.work.aequorea.main.model.mainpage.Datum;

public interface ArticleView extends BaseView {
    void onArticleLoaded(Data data);
    void onArticleError(Throwable t);
    
    void onRecommendationLoaded(List<Datum> dataList);
    void onRecommendationError(Throwable t);
}
