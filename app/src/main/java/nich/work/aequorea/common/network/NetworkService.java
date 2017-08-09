package nich.work.aequorea.common.network;

import io.reactivex.Observable;
import nich.work.aequorea.author.model.entities.Author;
import nich.work.aequorea.main.model.article.Article;
import nich.work.aequorea.main.model.mainpage.Page;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {

    // main page
    @GET("/v2/first_page_infos")
    Observable<Page> getMainPageInfo(@Query("page") int page);

    // article details
    @GET("/v2/articles/{article_id}/")
    Observable<Article> getArticleDetailInfo(@Path("article_id") long articleId);

    // article that belonging to specific author
    @GET("/v2/authors/{author_id}/articles")
    Observable<Author> getArticleList(@Path("author_id") long authorId, @Query("page") int page, @Query("per") int per);
}
