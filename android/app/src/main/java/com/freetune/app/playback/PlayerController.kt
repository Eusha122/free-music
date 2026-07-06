package com.freetune.app.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.freetune.app.data.model.Track
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlaybackUiState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
)

/**
 * Thin client-side wrapper around a MediaController connected to
 * [PlaybackService]. Lives for the app's lifetime; UI observes [state].
 */
class PlayerController(private val context: Context) {
    private var controller: MediaController? = null
    private var queue: List<Track> = emptyList()

    private val _state = MutableStateFlow(PlaybackUiState())
    val state: StateFlow<PlaybackUiState> = _state

    fun connect(onReady: () -> Unit = {}) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener({
            controller = future.get().also { attachListener(it) }
            onReady()
        }, MoreExecutors.directExecutor())
    }

    private fun attachListener(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val track = queue.find { it.id == mediaItem?.mediaId }
                _state.value = _state.value.copy(currentTrack = track, positionMs = 0)
            }

            override fun onEvents(player: Player, events: Player.Events) {
                _state.value = _state.value.copy(
                    positionMs = player.currentPosition,
                    durationMs = player.duration.coerceAtLeast(0),
                )
            }
        })
    }

    /** Loads [tracks] as the play queue and starts playback at [startIndex]. */
    fun playQueue(tracks: List<Track>, startIndex: Int = 0) {
        queue = tracks
        val items = tracks.map { it.toMediaItem() }
        controller?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        controller?.apply { if (isPlaying) pause() else play() }
    }

    fun skipToNext() = controller?.seekToNextMediaItem()
    fun skipToPrevious() = controller?.seekToPreviousMediaItem()
    fun seekTo(positionMs: Long) = controller?.seekTo(positionMs)

    fun release() {
        controller?.release()
        controller = null
    }
}

private fun Track.toMediaItem(): MediaItem =
    MediaItem.Builder()
        .setMediaId(id)
        .setUri(streamUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artistName)
                .setArtworkUri(artworkUrl?.let { android.net.Uri.parse(it) })
                .build()
        )
        .build()
