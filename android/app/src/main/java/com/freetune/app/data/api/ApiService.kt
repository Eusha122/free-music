package com.freetune.app.data.api

import com.freetune.app.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body body: AuthRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @GET("tracks/home")
    suspend fun home(): TrackListResponse

    @GET("tracks/search")
    suspend fun search(@Query("q") query: String): TrackListResponse

    @GET("tracks/recommendations")
    suspend fun recommendations(): TrackListResponse

    @GET("tracks/shuffle-queue")
    suspend fun shuffleQueue(@Query("trackIds") trackIds: String): TrackListResponse

    @POST("tracks/{id}/play")
    suspend fun recordPlay(@Path("id") trackId: String)

    @POST("tracks/{id}/like")
    suspend fun like(@Path("id") trackId: String)

    @DELETE("tracks/{id}/like")
    suspend fun unlike(@Path("id") trackId: String)

    @GET("tracks/liked")
    suspend fun likedTracks(): TrackListResponse

    @GET("playlists")
    suspend fun playlists(): PlaylistListResponse

    @POST("playlists")
    suspend fun createPlaylist(@Body body: CreatePlaylistRequest): CreatePlaylistResponse

    @POST("playlists/{id}/tracks")
    suspend fun addTrackToPlaylist(@Path("id") playlistId: String, @Body body: AddTrackRequest)

    @DELETE("playlists/{id}/tracks/{trackId}")
    suspend fun removeTrackFromPlaylist(@Path("id") playlistId: String, @Path("trackId") trackId: String)

    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(@Path("id") playlistId: String)
}
