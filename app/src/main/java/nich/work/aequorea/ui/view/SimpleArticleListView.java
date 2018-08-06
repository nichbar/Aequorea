package nich.work.aequorea.ui.view;

import nich.work.aequorea.common.ui.view.NetworkView;
import nich.work.aequorea.data.SimpleArticleListModel;
import nich.work.aequorea.data.entity.Author;
import nich.work.aequorea.data.entity.Data;

public interface SimpleArticleListView extends NetworkView {
    
    void onDataLoaded(Data data);
    
    void onNoData();
    
    void onUpdateAuthorInfo(Author author);
    
    SimpleArticleListModel getModel();
}
