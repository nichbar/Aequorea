package nich.work.aequorea.author.ui;

import nich.work.aequorea.author.model.AuthorModel;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.common.ui.view.BaseView;

public interface AuthorView extends BaseView {
    void onDataLoaded(Author author);
    
    void onError(Throwable t);
    
    void onUpdateAuthorInfo(Author author);
    
    AuthorModel getModel();
}
