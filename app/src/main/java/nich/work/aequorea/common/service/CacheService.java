package nich.work.aequorea.common.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.disklrucache.DiskLruCache;
import com.zzhoujay.richtext.ext.MD5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.cache.ArticleCache;
import nich.work.aequorea.common.network.NetworkService;
import nich.work.aequorea.common.network.RequestManager;
import nich.work.aequorea.common.utils.CacheUtils;
import nich.work.aequorea.common.utils.FilterUtils;
import nich.work.aequorea.common.utils.NetworkUtils;
import nich.work.aequorea.common.utils.SPUtils;
import nich.work.aequorea.model.entity.Data;
import nich.work.aequorea.model.entity.DataWrapper;
import nich.work.aequorea.model.entity.Datum;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CacheService extends Service {
    
    private OkHttpClient mClient;
    private NetworkService mService;
    private CompositeDisposable mComposite;
    private LinkedList<Long> mArticleList;
    private HashSet<Call> mDownloadTasks;
    private DiskLruCache mDiskLruCache;
    
    private NotificationManager mNotificationManager;
    private BroadcastReceiver mNetworkChangeReceiver;

    private int mPicToCacheSize;
    private int mPicCachedCount;
    
    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("<(img|IMG)(.*?)>");
    private static Pattern IMAGE_SRC_PATTERN = Pattern.compile("(src|SRC)=\"(.*?)\"");
    
    private static final String TAG = CacheService.class.getSimpleName();
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mClient = new OkHttpClient();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mService = RequestManager.getInstance().getRetrofit().create(NetworkService.class);
        mComposite = new CompositeDisposable();
    
        mNetworkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!NetworkUtils.isWiFiNetwork()) {
                    cancelCaching();
                }
            }
        };
        
        registerReceiver(mNetworkChangeReceiver, intentFilter);
        
        Data dataToCache = getMainPageDataFromSP();
        if (dataToCache != null) {
            preCache(dataToCache);
            
            if (mArticleList.size() != 0) {
                showNotification();
                startCaching();
            }
        }
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeReceiver);
        hideNotification();
        if (mComposite != null) {
            mComposite.clear();
        }
    }
    
    @SuppressLint("UseSparseArrays")
    private int preCache(Data data) {
        mArticleList = new LinkedList<>();
        mDownloadTasks = new HashSet<>();
        mDiskLruCache = CacheUtils.getArticlePicDiskLruCache();
        
        List<Datum> dataList = FilterUtils.filterData(data.getData());
        
        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            // check whether this article have been cached in memory or internal storage
            Datum d = dataList.get(i).getData().get(0);
            if (needToCacheArticle(d.getId())) {
                mArticleList.add(d.getId());
            }
        }
    
        return mArticleList.size();
    }
    
    private void showNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "cache")
            .setSmallIcon(R.mipmap.ic_notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.caching_offline_article));
        
        if (mNotificationManager != null) {
            mNotificationManager.notify(1, builder.build());
        }
    }
    
    private void hideNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(1);
        }
    }
    
    /**
     * Cache only text data, it won't take long.
     */
    private void startCaching() {
        mPicCachedCount = 0;
        mPicToCacheSize = 0;
        
        if (mArticleList.size() == 0) {
            Log.d(TAG, "No more article to cache, stop and leave");
            stopAndLeave();
            return;
        }
        
        long id = mArticleList.poll();
        
        if (needToCacheArticle(id)) {
            cacheArticleText(id);
        }
    }
    
    private void cacheArticleText(final long id) {
        Disposable disposable = mService.getArticleDetailInfo(id)
            .subscribeOn(Schedulers.io())
            .subscribe(new Consumer<DataWrapper>() {
                @Override
                public void accept(DataWrapper article) throws Exception {
                    cacheArticleText(id, article);
                    
                    cacheArticlePic(article.getData().getContent());
                    Log.d(TAG, id + " text cache succeed");
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });
        mComposite.add(disposable);
    }
    
    private void cacheArticleText(long id, DataWrapper article) {
        Gson gson = new Gson();
        String json = gson.toJson(article);
        ArticleCache.getCache().cache(id, json);
    }
    
    /**
     * Cache pic in article and show caching notification.
     */
    private void cacheArticlePic(final String text) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<String> picList = analyzeImages(text);
    
                mPicToCacheSize = picList.size();
                
                // this article contains no pic
                if (mPicToCacheSize == 0) {
                    startCaching();
                    return;
                }
                
                for (final String url : picList) {
                    Request builder = new Request.Builder().url(url).build();
                    Call call = mClient.newCall(builder);
                    call.enqueue(new WriteToCacheCallback());
                    mDownloadTasks.add(call);
                }
            }
        };
        
        Aequorea.getExecutor().submit(runnable);
    }
    
    /**
     * Retrieve image src list from text.
     */
    private List<String> analyzeImages(String text) {
        List<String> picList = new ArrayList<>();
        Matcher imageTagMatcher = IMAGE_TAG_PATTERN.matcher(text);
        while (imageTagMatcher.find()) {
            String image = imageTagMatcher.group(2).trim();
            Matcher imageSrcMatcher = IMAGE_SRC_PATTERN.matcher(image);
            String src = null;
            if (imageSrcMatcher.find()) {
                src = imageSrcMatcher.group(2).trim();
            }
            if (!TextUtils.isEmpty(src)) {
                picList.add(src);
            }
        }
        return picList;
    }
    
    private boolean needToCacheArticle(long id) {
        return TextUtils.isEmpty(ArticleCache.getCache().loadCache(id)) && !ArticleCache.getCache()
            .isCachedInStorage(id);
    }
    
    private Data getMainPageDataFromSP() {
        String cacheString = SPUtils.getString(Constants.SP_LATEST_MAIN_PAGE);
        if (!TextUtils.isEmpty(cacheString)) {
            Gson gson = new Gson();
            return gson.fromJson(cacheString, Data.class);
        }
        return null;
    }
    
    private class WriteToCacheCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            call.cancel();
            
            checkPicCachingProcedure();
        }
        
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream in = response.body().byteStream();
            
            String key = generateKey(call.request().url().url().toString());
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            
            Log.d(TAG, key + " cached");
            
            if (snapshot == null) {
                DiskLruCache.Editor edit = mDiskLruCache.edit(key);
                
                
                OutputStream os = edit.newOutputStream(0);
                os.write(0);
                
                byte[] buffer = new byte[2048];
                int len;
                OutputStream outputStream = edit.newOutputStream(1);
                outputStream.write(1);
                while ((len = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
                outputStream.close();
                
                edit.commit();
            }
    
            checkPicCachingProcedure();
        }
    }
    
    private void checkPicCachingProcedure() {
        mPicCachedCount++;
        if (mPicCachedCount == mPicToCacheSize) {
            startCaching();
        }
    }
    
    private String generateKey(String url) {
        return MD5.generate(url);
    }
    
    private void cancelCaching() {
        for (Call call : mDownloadTasks) {
            call.cancel();
        }
        stopAndLeave();
    }
    
    private void stopAndLeave() {
        hideNotification();
        stopSelf();
    }
}
