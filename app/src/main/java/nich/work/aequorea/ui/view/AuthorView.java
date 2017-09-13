package nich.work.aequorea.ui.view;

import nich.work.aequorea.model.AuthorModel;
import nich.work.aequorea.common.ui.view.NetworkView;
import nich.work.aequorea.model.entity.Author;

public interface AuthorView extends NetworkView {
    void onDataLoaded(Author author);
    
    void onUpdateAuthorInfo(Author author);
    
    AuthorModel getModel();
}
