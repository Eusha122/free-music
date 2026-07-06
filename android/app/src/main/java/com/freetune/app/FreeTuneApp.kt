package com.freetune.app

import android.app.Application
import com.freetune.app.data.TokenStore
import com.freetune.app.data.api.ApiService
import com.freetune.app.data.api.NetworkModule
import com.freetune.app.data.repository.AuthRepository
import com.freetune.app.data.repository.CatalogRepository
import com.freetune.app.data.repository.PlaylistRepository
import com.freetune.app.playback.DownloadRepository
import com.freetune.app.playback.PlayerController

class FreeTuneApp : Application() {
    lateinit var tokenStore: TokenStore
        private set
    lateinit var apiService: ApiService
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var catalogRepository: CatalogRepository
        private set
    lateinit var playlistRepository: PlaylistRepository
        private set
    lateinit var playerController: PlayerController
        private set
    lateinit var downloadRepository: DownloadRepository
        private set

    override fun onCreate() {
        super.onCreate()
        tokenStore = TokenStore(this)
        apiService = NetworkModule.create(tokenStore)
        authRepository = AuthRepository(apiService, tokenStore)
        catalogRepository = CatalogRepository(apiService)
        playlistRepository = PlaylistRepository(apiService)
        playerController = PlayerController(this)
        downloadRepository = DownloadRepository(this)
    }
}
