package com.freetune.app.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freetune.app.ui.screens.common.TrackRow

@Composable
fun LibraryScreen(viewModel: LibraryViewModel) {
    val state by viewModel.state.collectAsState()
    val downloadedIds by viewModel.downloadRepository.downloadedTrackIds.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Name") },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank()) viewModel.createPlaylist(newPlaylistName)
                    newPlaylistName = ""
                    showCreateDialog = false
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Your Library") },
            actions = {
                TextButton(onClick = { showCreateDialog = true }) { Text("New playlist") }
            },
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Couldn't load your library: ${state.error}")
            }
            else -> LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Text(
                        "Playlists",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                if (state.playlists.isEmpty()) {
                    item { Text("No playlists yet", modifier = Modifier.padding(horizontal = 16.dp)) }
                }
                items(state.playlists, key = { it.id }) { playlist ->
                    ListItem(
                        headlineContent = { Text(playlist.name) },
                        supportingContent = { Text("${playlist.tracks.size} tracks") },
                    )
                }

                item {
                    Text(
                        "Liked Songs",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                items(state.likedTracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isDownloaded = track.id in downloadedIds,
                        onClick = { viewModel.playLiked(state.likedTracks.indexOf(track)) },
                        onToggleDownload = { viewModel.toggleDownload(track) },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
