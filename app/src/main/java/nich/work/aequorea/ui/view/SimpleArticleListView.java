package nich.work.aequorea.ui.view;

import nich.work.aequorea.common.ui.view.NetworkView;
import nich.work.aequorea.model.SimpleArticleListModel;
import nich.work.aequorea.model.entity.Author;
import nich.work.aequorea.model.entity.Data;

public interface SimpleArticleListView extends NetworkView {
    
    void onDataLoaded(Data data);
    
    void onNoData();
    
    void onUpdateAuthorInfo(Author author);
    
    SimpleArticleListModel getModel();
}
