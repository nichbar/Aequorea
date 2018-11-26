package nich.work.aequorea.common.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.jakewharton.disklrucache.DiskLruCache
import com.zzhoujay.richtext.ext.MD5
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import nich.work.aequorea.R
import nich.work.aequorea.common.Constants
import nich.work.aequorea.common.cache.ArticleCache
import nich.work.aequorea.common.network.ApiService
import nich.work.aequorea.common.network.RequestManager
import nich.work.aequorea.common.runOnIoThread
import nich.work.aequorea.common.utils.CacheUtils
import nich.work.aequorea.common.utils.FilterUtils
import nich.work.aequorea.common.utils.NetworkUtils
import nich.work.aequorea.common.utils.SPUtils
import nich.work.aequorea.data.entity.Data
import nich.work.aequorea.data.entity.DataWrapper
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class CacheService : Service() {

    companion object {
        private val CHANNEL_ID = "aequorea_cache"

        private val IMAGE_TAG_PATTERN = Pattern.compile("<(img|IMG)(.*?)>")
        private val IMAGE_SRC_PATTERN = Pattern.compile("(src|SRC)=\"(.*?)\"")

        private val TAG = CacheService::class.java.simpleName
    }

    private var mClient: OkHttpClient? = null
    private var mService: ApiService? = null
    private var mComposite: CompositeDisposable? = null
    private var mArticleList: LinkedList<Long>? = null
    private var mDownloadTasks: HashSet<Call>? = null
    private var mDiskLruCache: DiskLruCache? = null

    private var mNotificationManager: NotificationManager? = null
    private var mNetworkChangeReceiver: BroadcastReceiver? = null

    private var mPicToCacheSize: Int = 0
    private var mPicCachedCount: Int = 0

    private val mainPageDataFromSP: Data?
        get() {
            val cacheString = SPUtils.getString(Constants.SP_LATEST_MAIN_PAGE)
            if (!TextUtils.isEmpty(cacheString)) {
                val gson = Gson()
                return gson.fromJson(cacheString, Data::class.java)
            }
            return null
        }

    override fun onCreate() {
        super.onCreate()

        mClient = OkHttpClient()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        mService = RequestManager.getInstance().retrofit.create(ApiService::class.java)
        mComposite = CompositeDisposable()

        mNetworkChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!NetworkUtils.isWiFiNetwork()) {
                    cancelCaching()
                }
            }
        }

        registerReceiver(mNetworkChangeReceiver, intentFilter)

        val dataToCache = mainPageDataFromSP
        if (dataToCache != null) {
            preCache(dataToCache)

            if (mArticleList!!.size != 0) {
                showNotification()
                startCaching()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mNetworkChangeReceiver)
        hideNotification()
        if (mComposite != null) {
            mComposite!!.clear()
        }
    }

    @SuppressLint("UseSparseArrays")
    private fun preCache(data: Data) {
        mArticleList = LinkedList()
        mDownloadTasks = HashSet()
        mDiskLruCache = CacheUtils.getArticlePicDiskLruCache()

        val dataList = FilterUtils.filterData(data.data)

        val size = dataList.size
        for (i in 0 until size) {
            // check whether this article have been cached in memory or internal storage
            val d = dataList[i].data[0]
            if (needToCacheArticle(d.id!!)) {
                mArticleList!!.add(d.id)
            }
        }
    }

    private fun showNotification() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, getString(R.string.aequorea_offline_cache), NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.caching_offline_article))

        if (mNotificationManager != null) {
            mNotificationManager!!.notify(1, builder.build())
        }
    }

    private fun hideNotification() {
        if (mNotificationManager != null) {
            mNotificationManager!!.cancel(1)
        }
    }

    /**
     * Cache only text data, it won't take long.
     */
    private fun startCaching() {
        mPicCachedCount = 0
        mPicToCacheSize = 0

        if (mArticleList!!.size == 0) {
            Log.d(TAG, "No more article to cache, stop and leave")
            stopAndLeave()
            return
        }

        val id = mArticleList!!.poll()

        if (needToCacheArticle(id)) {
            cacheArticleText(id)
        }
    }

    private fun cacheArticleText(id: Long) {
        val disposable = mService!!.getArticleDetailInfo(id)
                .subscribeOn(Schedulers.io())
                .subscribe({ article ->
                    cacheArticleText(id, article)

                    cacheArticlePic(article.data.content)
                    Log.d(TAG, id.toString() + " text cache succeed")
                }, { it.printStackTrace() })
        mComposite!!.add(disposable)
    }

    private fun cacheArticleText(id: Long, article: DataWrapper) {
        val gson = Gson()
        val json = gson.toJson(article)
        ArticleCache.cache(id, json)
    }

    /**
     * Cache pic in article and show caching notification.
     */
    private fun cacheArticlePic(text: String) {
        runOnIoThread {
            val picList = analyzeImages(text)

            mPicToCacheSize = picList.size

            // this article contains no pic
            if (mPicToCacheSize == 0) {
                startCaching()
                return@runOnIoThread
            }

            for (url in picList) {
                val builder = Request.Builder().url(url).build()
                val call = mClient!!.newCall(builder)
                call.enqueue(WriteToCacheCallback())
                mDownloadTasks!!.add(call)
            }
        }
    }

    /**
     * Retrieve image src list from text.
     */
    private fun analyzeImages(text: String): List<String> {
        val picList = ArrayList<String>()
        val imageTagMatcher = IMAGE_TAG_PATTERN.matcher(text)
        while (imageTagMatcher.find()) {
            val image = imageTagMatcher.group(2).trim { it <= ' ' }
            val imageSrcMatcher = IMAGE_SRC_PATTERN.matcher(image)
            var src: String? = null
            if (imageSrcMatcher.find()) {
                src = imageSrcMatcher.group(2).trim { it <= ' ' }
            }
            if (!TextUtils.isEmpty(src)) {
                src?.let { picList.add(it) }
            }
        }
        return picList
    }

    private fun needToCacheArticle(id: Long): Boolean {
        return TextUtils.isEmpty(ArticleCache.loadCache(id)) && !ArticleCache.isCachedInStorage(id)
    }

    private inner class WriteToCacheCallback : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            call.cancel()

            checkPicCachingProcedure()
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val inputStream = response.body()!!.byteStream()

            val key = generateKey(call.request().url().url().toString())
            val snapshot = mDiskLruCache!!.get(key)

            Log.d(TAG, "$key cached")

            if (snapshot == null) {
                val edit = mDiskLruCache!!.edit(key)


                val os = edit.newOutputStream(0)
                os.write(0)

                val buffer = ByteArray(2048)
                var len: Int
                val outputStream = edit.newOutputStream(1)
                outputStream.write(1)
                while (inputStream.read(buffer).let { len = it; it != -1 }) {
                    outputStream.write(buffer, 0, len)
                }
                outputStream.flush()
                outputStream.close()

                edit.commit()
            }

            checkPicCachingProcedure()
        }
    }

    private fun checkPicCachingProcedure() {
        mPicCachedCount++
        if (mPicCachedCount == mPicToCacheSize) {
            startCaching()
        }
    }

    private fun generateKey(url: String): String {
        return MD5.generate(url)
    }

    private fun cancelCaching() {
        for (call in mDownloadTasks!!) {
            call.cancel()
        }
        stopAndLeave()
    }

    private fun stopAndLeave() {
        hideNotification()
        stopSelf()
    }
}
