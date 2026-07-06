package com.freetune.app.ui.screens.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.freetune.app.playback.PlaybackUiState

@Composable
fun PlayerScreen(viewModel: PlayerViewModel, onCollapse: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onCollapse) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Collapse")
            }
        }
        Spacer(Modifier.weight(1f))

        AsyncImage(
            model = state.currentTrack?.artworkUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.height(24.dp))

        Text(state.currentTrack?.title ?: "Nothing playing", style = MaterialTheme.typography.headlineSmall)
        Text(state.currentTrack?.artistName ?: "", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        PlaybackProgress(state)

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.skipToPrevious() }) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.width(16.dp))
            FilledIconButton(onClick = { viewModel.togglePlayPause() }, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = { viewModel.skipToNext() }) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier.size(36.dp))
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun PlaybackProgress(state: PlaybackUiState) {
    val progress = if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs else 0f
    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
}
