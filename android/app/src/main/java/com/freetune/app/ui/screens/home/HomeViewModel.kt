package com.freetune.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetune.app.data.model.Track
import com.freetune.app.data.repository.CatalogRepository
import com.freetune.app.playback.DownloadRepository
import com.freetune.app.playback.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val forYou: List<Track> = emptyList(),
    val discover: List<Track> = emptyList(),
    val error: String? = null,
)

class HomeViewModel(
    private val catalogRepository: CatalogRepository,
    private val playerController: PlayerController,
    val downloadRepository: DownloadRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val discover = catalogRepository.home()
                val forYou = runCatching { catalogRepository.recommendations() }.getOrDefault(emptyList())
                _state.value = _state.value.copy(isLoading = false, forYou = forYou, discover = discover)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Failed to load")
            }
        }
    }

    fun playTrack(queue: List<Track>, index: Int) {
        playerController.playQueue(queue, index)
        viewModelScope.launch { runCatching { catalogRepository.recordPlay(queue[index]) } }
    }

    fun shufflePlay(tracks: List<Track>) {
        viewModelScope.launch {
            val shuffled = runCatching { catalogRepository.shuffleQueue(tracks) }.getOrDefault(tracks.shuffled())
            playerController.playQueue(shuffled, 0)
            runCatching { catalogRepository.recordPlay(shuffled.first()) }
        }
    }

    fun toggleDownload(track: Track) {
        if (downloadRepository.isDownloaded(track.id)) downloadRepository.remove(track)
        else downloadRepository.download(track)
    }
}
