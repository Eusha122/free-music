package com.freetune.app.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.freetune.app.playback.PlaybackUiState
import com.freetune.app.ui.theme.AccentGreen
import com.freetune.app.ui.theme.AppBackground
import com.freetune.app.ui.theme.ShadowColor
import com.freetune.app.ui.theme.SkeuomorphicButton
import com.freetune.app.ui.theme.SurfaceCardRaised
import com.freetune.app.ui.theme.TextPrimary
import com.freetune.app.ui.theme.TextSecondary
import com.freetune.app.ui.theme.insetTrack

@Composable
fun PlayerScreen(viewModel: PlayerViewModel, onCollapse: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SolidIconChip(onClick = onCollapse) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Collapse", tint = TextPrimary)
                }
            }
            Spacer(Modifier.weight(1f))

            // Album art as a physical print: a real, solid drop shadow gives
            // it lift - no color wash over the artwork itself.
            AsyncImage(
                model = state.currentTrack?.artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(12.dp), ambientColor = ShadowColor, spotColor = ShadowColor)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(Modifier.height(28.dp))

            Text(
                state.currentTrack?.title ?: "Nothing playing",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                state.currentTrack?.artistName ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )
            Spacer(Modifier.height(20.dp))

            GroovePlaybackProgress(state)

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SolidIconChip(onClick = { viewModel.skipToPrevious() }, size = 52.dp) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = TextPrimary, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(24.dp))
                SkeuomorphicButton(
                    onClick = { viewModel.togglePlayPause() },
                    size = 76.dp,
                    baseColor = AccentGreen,
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(34.dp),
                    )
                }
                Spacer(Modifier.width(24.dp))
                SolidIconChip(onClick = { viewModel.skipToNext() }, size = 52.dp) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = TextPrimary, modifier = Modifier.size(26.dp))
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun SolidIconChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 44.dp,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(SurfaceCardRaised),
    ) {
        content()
    }
}

/** A recessed groove track with a solid filled bar riding inside it. */
@Composable
private fun GroovePlaybackProgress(state: PlaybackUiState) {
    val progress = if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs else 0f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .insetTrack(cornerRadius = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(3.dp))
                .background(AccentGreen),
        )
    }
}
