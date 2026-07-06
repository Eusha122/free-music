package com.freetune.app.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.freetune.app.data.model.Track
import com.freetune.app.ui.theme.ShadowColor
import com.freetune.app.ui.theme.SurfaceCardRaised
import com.freetune.app.ui.theme.TextPrimary
import com.freetune.app.ui.theme.TextSecondary

@Composable
fun TrackRow(
    track: Track,
    isDownloaded: Boolean,
    onClick: () -> Unit,
    onToggleDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(6.dp), ambientColor = ShadowColor, spotColor = ShadowColor)
                .clip(RoundedCornerShape(6.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                track.title,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                track.artistName,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SurfaceCardRaised),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onToggleDownload, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.Download,
                    contentDescription = if (isDownloaded) "Downloaded" else "Download for offline",
                    tint = if (isDownloaded) com.freetune.app.ui.theme.AccentGreen else TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
