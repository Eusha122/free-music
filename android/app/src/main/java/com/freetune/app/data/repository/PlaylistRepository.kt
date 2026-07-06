package com.freetune.app.data.repository

import com.freetune.app.data.api.ApiService
import com.freetune.app.data.model.AddTrackRequest
import com.freetune.app.data.model.CreatePlaylistRequest
import com.freetune.app.data.model.Playlist
import com.freetune.app.data.model.Track

class PlaylistRepository(private val api: ApiService) {
    suspend fun playlists(): List<Playlist> = api.playlists().playlists
    suspend fun create(name: String): Playlist = api.createPlaylist(CreatePlaylistRequest(name)).playlist
    suspend fun addTrack(playlistId: String, track: Track) =
        api.addTrackToPlaylist(playlistId, AddTrackRequest(track.id))
    suspend fun removeTrack(playlistId: String, trackId: String) =
        api.removeTrackFromPlaylist(playlistId, trackId)
    suspend fun delete(playlistId: String) = api.deletePlaylist(playlistId)
}
