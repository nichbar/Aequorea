package nich.work.aequorea.main.ui.view;

import java.util.List;

import nich.work.aequorea.common.ui.view.BaseView;
import nich.work.aequorea.main.model.MainPageModel;
import nich.work.aequorea.main.model.mainpage.Datum;

public interface HomeView extends BaseView {
    void onDataLoaded(List<Datum> datumList, boolean isRefresh);
    
    void onError(Throwable t);
    
    MainPageModel getModel();
    
    void setRefreshing(boolean b);
}
