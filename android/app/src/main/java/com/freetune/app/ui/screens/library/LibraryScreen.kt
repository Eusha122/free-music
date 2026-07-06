package com.freetune.app.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freetune.app.ui.screens.common.TrackRow
import com.freetune.app.ui.theme.AccentGreen
import com.freetune.app.ui.theme.TextPrimary
import com.freetune.app.ui.theme.TextSecondary
import com.freetune.app.ui.theme.elevatedCard

@OptIn(ExperimentalMaterial3Api::class)
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
            title = {
                Text(
                    "Your Library",
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            actions = {
                TextButton(onClick = { showCreateDialog = true }) {
                    Text("New playlist", color = AccentGreen, fontWeight = FontWeight.Bold)
                }
            },
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentGreen)
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Couldn't load your library: ${state.error}", color = TextSecondary)
            }
            else -> LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Text(
                        "Playlists",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                if (state.playlists.isEmpty()) {
                    item {
                        Text(
                            "No playlists yet",
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
                items(state.playlists, key = { it.id }) { playlist ->
                    ListItem(
                        headlineContent = { Text(playlist.name, color = TextPrimary) },
                        supportingContent = {
                            Text("${playlist.tracks.size} tracks", color = TextSecondary)
                        },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .elevatedCard(cornerRadius = 12.dp, shape = RoundedCornerShape(12.dp), elevation = 2.dp),
                    )
                }

                item {
                    Text(
                        "Liked Songs",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
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
