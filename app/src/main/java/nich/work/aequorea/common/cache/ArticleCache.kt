package nich.work.aequorea.common.cache

import android.util.LruCache
import com.jakewharton.disklrucache.DiskLruCache
import nich.work.aequorea.Aequorea
import nich.work.aequorea.BuildConfig
import nich.work.aequorea.common.Constants
import nich.work.aequorea.common.runOnIoThread
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

object ArticleCache {

    const val MAX_CACHED_MEMORY_ARTICLE_SIZE = 20
    const val MAX_CACHED_DISK_ARTICLE_SIZE = 1024 * 1024 * 100 // 100M

    private val mCache: LruCache<Long, String>
    private var mDiskCache: DiskLruCache? = null

    init {
        this.mCache = LruCache(MAX_CACHED_MEMORY_ARTICLE_SIZE)
        runOnIoThread {
            try {
                val cacheDir = File(Aequorea.app!!.cacheDir.absolutePath + File.separator + Constants.ARTICLE_CACHE)
                mDiskCache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1, MAX_CACHED_DISK_ARTICLE_SIZE.toLong())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun remove(key: String) {
        try {
            mDiskCache?.remove(key)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun cache(id: Long, articleData: String) {
        mCache.put(id, articleData)

        runOnIoThread {
            cacheInStorage(id, articleData)
        }
    }

    // cache in internal storage
    private fun cacheInStorage(id: Long, articleData: String) {
        val key = java.lang.Long.toString(id)
        if (mDiskCache != null) {
            try {
                val snapshot = mDiskCache?.get(key)

                if (snapshot == null) {
                    val editor = mDiskCache?.edit(key)
                    editor?.let {
                        val out = editor.newOutputStream(0)
                        out.write(articleData.toByteArray())
                        out.close()
                        editor.commit()
                    }
                } else {
                    snapshot.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun isCachedInStorage(id: Long): Boolean {
        val key = java.lang.Long.toString(id)

        if (mDiskCache != null) {
            try {
                val snapshot = mDiskCache!!.get(key)
                if (snapshot != null) {
                    return true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return false
    }

    @JvmStatic
    fun loadCacheFromStorage(id: Long): String {
        val key = java.lang.Long.toString(id)

        if (mDiskCache != null) {
            try {
                val snapshot = mDiskCache!!.get(key)
                val `in` = snapshot.getInputStream(0)
                if (`in` != null) {
                    val bis = BufferedInputStream(`in`)
                    val buf = ByteArrayOutputStream()
                    var result = bis.read()
                    while (result != -1) {
                        buf.write(result.toByte().toInt())
                        result = bis.read()
                    }
                    return buf.toString("UTF-8")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return ""
    }

    @JvmStatic
    fun loadCache(id: Long): String? {
        return mCache.get(id)
    }

    @JvmStatic
    fun recycle() {
        mCache.evictAll()
    }
}
