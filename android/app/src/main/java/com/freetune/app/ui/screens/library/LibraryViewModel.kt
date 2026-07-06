package com.freetune.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetune.app.data.model.Playlist
import com.freetune.app.data.model.Track
import com.freetune.app.data.repository.CatalogRepository
import com.freetune.app.data.repository.PlaylistRepository
import com.freetune.app.playback.DownloadRepository
import com.freetune.app.playback.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val isLoading: Boolean = true,
    val likedTracks: List<Track> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val error: String? = null,
)

class LibraryViewModel(
    private val catalogRepository: CatalogRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerController: PlayerController,
    val downloadRepository: DownloadRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val liked = catalogRepository.likedTracks()
                val playlists = playlistRepository.playlists()
                _state.value = _state.value.copy(isLoading = false, likedTracks = liked, playlists = playlists)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Failed to load library")
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            runCatching { playlistRepository.create(name) }
            refresh()
        }
    }

    fun playLiked(index: Int) {
        val tracks = _state.value.likedTracks
        playerController.playQueue(tracks, index)
        viewModelScope.launch { runCatching { catalogRepository.recordPlay(tracks[index]) } }
    }

    fun toggleDownload(track: Track) {
        if (downloadRepository.isDownloaded(track.id)) downloadRepository.remove(track)
        else downloadRepository.download(track)
    }
}
