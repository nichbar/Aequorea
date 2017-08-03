package nich.work.aequorea.common.network;

import io.reactivex.Observable;
import nich.work.aequorea.main.model.mainpage.Page;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkService {
    
    @GET("/v2/first_page_infos")
    Observable<Page> getMainPageInfo(@Query("page") int page);
}
