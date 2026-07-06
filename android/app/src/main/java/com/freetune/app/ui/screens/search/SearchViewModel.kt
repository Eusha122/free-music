package com.freetune.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetune.app.data.model.Track
import com.freetune.app.data.repository.CatalogRepository
import com.freetune.app.playback.DownloadRepository
import com.freetune.app.playback.PlayerController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Track> = emptyList(),
    val error: String? = null,
)

class SearchViewModel(
    private val catalogRepository: CatalogRepository,
    private val playerController: PlayerController,
    val downloadRepository: DownloadRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.value = _state.value.copy(query = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.value = _state.value.copy(results = emptyList(), isLoading = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(350) // debounce keystrokes before hitting the API
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val results = catalogRepository.search(query)
                _state.value = _state.value.copy(isLoading = false, results = results)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Search failed")
            }
        }
    }

    fun playTrack(index: Int) {
        val results = _state.value.results
        playerController.playQueue(results, index)
        viewModelScope.launch { runCatching { catalogRepository.recordPlay(results[index]) } }
    }

    fun toggleDownload(track: Track) {
        if (downloadRepository.isDownloaded(track.id)) downloadRepository.remove(track)
        else downloadRepository.download(track)
    }
}
