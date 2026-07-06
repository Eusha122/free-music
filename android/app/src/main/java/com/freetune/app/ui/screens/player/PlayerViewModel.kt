package com.freetune.app.ui.screens.player

import androidx.lifecycle.ViewModel
import com.freetune.app.playback.PlaybackUiState
import com.freetune.app.playback.PlayerController
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel(private val playerController: PlayerController) : ViewModel() {
    val state: StateFlow<PlaybackUiState> = playerController.state

    fun togglePlayPause() = playerController.togglePlayPause()
    fun skipToNext() = playerController.skipToNext()
    fun skipToPrevious() = playerController.skipToPrevious()
    fun seekTo(positionMs: Long) = playerController.seekTo(positionMs)
}
