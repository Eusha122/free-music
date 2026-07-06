package com.freetune.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.freetune.app.FreeTuneApp
import com.freetune.app.ui.screens.home.HomeViewModel
import com.freetune.app.ui.screens.library.LibraryViewModel
import com.freetune.app.ui.screens.player.PlayerViewModel
import com.freetune.app.ui.screens.search.SearchViewModel

class ViewModelFactory(private val app: FreeTuneApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(app.catalogRepository, app.playerController, app.downloadRepository)
            SearchViewModel::class.java -> SearchViewModel(app.catalogRepository, app.playerController, app.downloadRepository)
            LibraryViewModel::class.java -> LibraryViewModel(app.catalogRepository, app.playlistRepository, app.playerController, app.downloadRepository)
            PlayerViewModel::class.java -> PlayerViewModel(app.playerController)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}
