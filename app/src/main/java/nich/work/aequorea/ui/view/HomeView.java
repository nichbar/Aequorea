package nich.work.aequorea.ui.view;

import java.util.List;

import nich.work.aequorea.common.ui.view.NetworkView;
import nich.work.aequorea.model.MainPageModel;
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.model.entity.search.SearchDatum;

public interface HomeView extends NetworkView {
    void onDataLoaded(List<Datum> datumList, boolean isRefresh);
    
    void setRefreshing(boolean b);
    
    void onSearchResultLoaded(List<SearchDatum> data);
    
    MainPageModel getModel();
}
