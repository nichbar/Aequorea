package nich.work.aequorea.main.ui.view;

import java.util.List;

import nich.work.aequorea.common.ui.view.NetworkView;
import nich.work.aequorea.main.model.MainPageModel;
import nich.work.aequorea.main.model.mainpage.Datum;

public interface HomeView extends NetworkView {
    void onDataLoaded(List<Datum> datumList, boolean isRefresh);
    
    void setRefreshing(boolean b);
    
    MainPageModel getModel();
}
