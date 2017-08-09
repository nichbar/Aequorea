package nich.work.aequorea.common.utils;

import android.content.Context;
import android.content.Intent;

import nich.work.aequorea.author.ui.AuthorActivity;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.main.ui.activities.ArticleActivity;


public class IntentUtils {
    public static void startArticleActivity(Context context, long id) {
        Intent i = new Intent(context, ArticleActivity.class);
        i.putExtra(Constants.ARTICLE_ID, id);
        context.startActivity(i);
    }

    public static void startAuthorActivity(Context context, long id){
        Intent i = new Intent(context, AuthorActivity.class);
        i.putExtra(Constants.ARTICLE_ID, id);
        context.startActivity(i);
    }
}
