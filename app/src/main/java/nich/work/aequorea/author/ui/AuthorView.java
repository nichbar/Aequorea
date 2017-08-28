package nich.work.aequorea.author.ui;

import nich.work.aequorea.author.model.AuthorModel;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.common.ui.view.NetworkView;

public interface AuthorView extends NetworkView {
    void onDataLoaded(Author author);
    
    void onUpdateAuthorInfo(Author author);
    
    AuthorModel getModel();
}
