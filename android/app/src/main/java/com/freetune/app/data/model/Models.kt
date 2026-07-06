package com.freetune.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    val source: String,
    val sourceId: String,
    val title: String,
    val artistName: String,
    val albumName: String? = null,
    val durationSec: Int,
    val streamUrl: String,
    val artworkUrl: String? = null,
    val genres: List<String> = emptyList(),
    val license: String? = null,
)

@Serializable
data class TrackListResponse(val tracks: List<Track>)

@Serializable
data class AuthRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val userId: String,
    val tracks: List<PlaylistTrackEntry> = emptyList(),
)

@Serializable
data class PlaylistTrackEntry(
    val id: String,
    val playlistId: String,
    val trackId: String,
    val position: Int,
    val track: Track,
)

@Serializable
data class PlaylistListResponse(val playlists: List<Playlist>)

@Serializable
data class CreatePlaylistRequest(val name: String)

@Serializable
data class CreatePlaylistResponse(val playlist: Playlist)

@Serializable
data class AddTrackRequest(val trackId: String)
