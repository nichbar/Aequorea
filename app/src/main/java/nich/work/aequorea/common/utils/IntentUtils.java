package nich.work.aequorea.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
        i.putExtra(Constants.AUTHOR_ID, id);
        context.startActivity(i);
    }
    
    public static void shareText(Context context, String title, String content, String dialogTitle){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, title);
        share_intent.putExtra(Intent.EXTRA_TEXT, content);
        share_intent = Intent.createChooser(share_intent, dialogTitle);
        context.startActivity(share_intent);
    }
    
    public static void openInBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }
}
