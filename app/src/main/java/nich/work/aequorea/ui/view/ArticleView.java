package nich.work.aequorea.ui.view;

import java.util.List;

import nich.work.aequorea.common.ui.view.BaseView;
import nich.work.aequorea.model.entity.Datum;

public interface ArticleView extends BaseView {
    void onArticleLoaded(Datum data, boolean isRefresh);
    void onArticleError(Throwable t);
    
    void onRecommendationLoaded(List<Datum> dataList);
    void onRecommendationError(Throwable t);
    
    void onSnapshotSavedSucceeded(String s);
    void onSnapshotSavedFailed(Throwable t);
}
