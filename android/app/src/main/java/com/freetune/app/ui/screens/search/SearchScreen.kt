package com.freetune.app.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freetune.app.ui.screens.common.TrackRow
import com.freetune.app.ui.theme.AccentGreen
import com.freetune.app.ui.theme.SurfaceCardRaised
import com.freetune.app.ui.theme.TextPrimary
import com.freetune.app.ui.theme.TextSecondary

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val state by viewModel.state.collectAsState()
    val downloadedIds by viewModel.downloadRepository.downloadedTrackIds.collectAsState()

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search independent artists & tracks") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceCardRaised,
                unfocusedContainerColor = SurfaceCardRaised,
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = SurfaceCardRaised,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = AccentGreen,
                unfocusedLabelColor = TextSecondary,
                cursorColor = AccentGreen,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentGreen)
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Search failed: ${state.error}", color = TextSecondary)
            }
            else -> LazyColumn(Modifier.fillMaxSize()) {
                items(state.results, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isDownloaded = track.id in downloadedIds,
                        onClick = { viewModel.playTrack(state.results.indexOf(track)) },
                        onToggleDownload = { viewModel.toggleDownload(track) },
                    )
                }
            }
        }
    }
}
