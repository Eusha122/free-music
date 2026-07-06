package com.freetune.app.data.repository

import com.freetune.app.data.api.ApiService
import com.freetune.app.data.model.Track

class CatalogRepository(private val api: ApiService) {
    suspend fun home(): List<Track> = api.home().tracks
    suspend fun search(query: String): List<Track> = api.search(query).tracks
    suspend fun recommendations(): List<Track> = api.recommendations().tracks
    suspend fun likedTracks(): List<Track> = api.likedTracks().tracks

    suspend fun shuffleQueue(tracks: List<Track>): List<Track> =
        api.shuffleQueue(tracks.joinToString(",") { it.id }).tracks

    suspend fun recordPlay(track: Track) = api.recordPlay(track.id)
    suspend fun like(track: Track) = api.like(track.id)
    suspend fun unlike(track: Track) = api.unlike(track.id)
}
