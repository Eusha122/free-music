package com.freetune.app.playback

import android.content.Context
import android.net.Uri
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import com.freetune.app.data.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DownloadRepository(private val context: Context) {
    private val downloadManager = DownloadUtil.getDownloadManager(context)

    private val _downloadedTrackIds = MutableStateFlow(loadDownloadedIds())
    val downloadedTrackIds: StateFlow<Set<String>> = _downloadedTrackIds

    private fun loadDownloadedIds(): Set<String> {
        val ids = mutableSetOf<String>()
        downloadManager.downloadIndex.getDownloads().use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.download.state == Download.STATE_COMPLETED) ids += cursor.download.request.id
            }
        }
        return ids
    }

    fun isDownloaded(trackId: String): Boolean = trackId in _downloadedTrackIds.value

    fun download(track: Track) {
        val request = DownloadRequest.Builder(track.id, Uri.parse(track.streamUrl)).build()
        downloadManager.addDownload(request)
        _downloadedTrackIds.value = _downloadedTrackIds.value + track.id
    }

    fun remove(track: Track) {
        downloadManager.removeDownload(track.id)
        _downloadedTrackIds.value = _downloadedTrackIds.value - track.id
    }
}
