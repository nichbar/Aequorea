package nich.work.aequorea.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.zzhoujay.richtext.ext.MD5;

import java.io.File;

import nich.work.aequorea.BuildConfig;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.ui.activities.ArticleActivity;
import nich.work.aequorea.ui.activities.AuthorActivity;
import nich.work.aequorea.ui.activities.PhotoActivity;
import nich.work.aequorea.ui.activities.SearchActivity;
import nich.work.aequorea.ui.activities.SettingsActivity;
import nich.work.aequorea.ui.activities.TagActivity;

public class IntentUtils {
    public static void startSettingsActivity(Context context) {
        Intent i = new Intent(context, SettingsActivity.class);
        context.startActivity(i);
    }
    
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
    
    public static void startTagActivity(Context context, long id){
        Intent i = new Intent(context, TagActivity.class);
        i.putExtra(Constants.TAG_ID, id);
        context.startActivity(i);
    }
    
    public static void startSearchActivity(Context context, String key){
        Intent i = new Intent(context, SearchActivity.class);
        i.putExtra(Constants.SEARCH_KEY, key);
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
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }
    
    public static void openImageFromStorage(Context context, String path) {
        Intent intent = new Intent();
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
    
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/jpg");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(intent);
    }
    
    public static void openInNewPhotoActivity(Context context, String url) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(Constants.PHOTO, MD5.generate(url));
        context.startActivity(intent);
    }
}
