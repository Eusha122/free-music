package com.freetune.app.ui.screens.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.freetune.app.ui.theme.AccentGreen
import com.freetune.app.ui.theme.ShadowColor
import com.freetune.app.ui.theme.SkeuomorphicButton
import com.freetune.app.ui.theme.TextPrimary
import com.freetune.app.ui.theme.TextSecondary

@Composable
fun MiniPlayer(viewModel: PlayerViewModel, onExpand: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val track = state.currentTrack ?: return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpand)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(6.dp), ambientColor = ShadowColor, spotColor = ShadowColor)
                .clip(RoundedCornerShape(6.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(track.title, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                track.artistName,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.width(8.dp))
        SkeuomorphicButton(
            onClick = { viewModel.togglePlayPause() },
            size = 46.dp,
            baseColor = AccentGreen,
        ) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                tint = Color.Black,
            )
        }
    }
}
