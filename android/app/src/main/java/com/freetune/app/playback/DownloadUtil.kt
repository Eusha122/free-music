package com.freetune.app.playback

import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import java.io.File
import java.util.concurrent.Executors

/**
 * Shared singletons for offline playback: a disk [Cache] that downloaded
 * tracks are written into, and a [DownloadManager] that fills it. ExoPlayer
 * reads through the same cache, so once a track is downloaded it plays back
 * without hitting the network again.
 *
 * This is a minimal, direct DownloadManager wiring (no dedicated
 * DownloadService/notification yet) - good enough for in-session downloads;
 * add a DownloadService later for downloads that must survive app restarts.
 */
object DownloadUtil {
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    @Volatile private var databaseProvider: StandaloneDatabaseProvider? = null
    @Volatile private var downloadCache: Cache? = null
    @Volatile private var downloadManager: DownloadManager? = null

    @Synchronized
    private fun getDatabaseProvider(context: Context): StandaloneDatabaseProvider =
        databaseProvider ?: StandaloneDatabaseProvider(context.applicationContext).also {
            databaseProvider = it
        }

    @Synchronized
    fun getDownloadCache(context: Context): Cache {
        downloadCache?.let { return it }
        val dir = File(
            context.applicationContext.getExternalFilesDir(null) ?: context.filesDir,
            DOWNLOAD_CONTENT_DIRECTORY
        )
        return SimpleCache(dir, NoOpCacheEvictor(), getDatabaseProvider(context)).also {
            downloadCache = it
        }
    }

    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager {
        downloadManager?.let { return it }
        return DownloadManager(
            context.applicationContext,
            getDatabaseProvider(context),
            getDownloadCache(context),
            DefaultHttpDataSource.Factory(),
            Executors.newFixedThreadPool(2),
        ).also { downloadManager = it }
    }

    /** Cache-backed data source factory: reads local downloads first, falls back to the network. */
    fun cacheDataSourceFactory(context: Context): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(getDownloadCache(context))
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}
