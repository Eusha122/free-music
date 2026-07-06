package com.freetune.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A solid, raised card: flat surface color + a real elevation shadow + a
 * hairline border for edge definition. This is the "3D" depth cue real
 * premium apps (Spotify, Uber) actually use - shadow and layering, not
 * color gradients or translucency.
 */
fun Modifier.elevatedCard(
    cornerRadius: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    color: androidx.compose.ui.graphics.Color = SurfaceCard,
    elevation: Dp = 8.dp,
): Modifier = this
    .shadow(elevation = elevation, shape = shape, ambientColor = ShadowColor, spotColor = ShadowColor)
    .clip(shape)
    .background(color)
    .border(width = 1.dp, color = HairlineBorder, shape = shape)

/** A recessed, darker inset - used for the progress-bar groove. */
fun Modifier.insetTrack(
    cornerRadius: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
): Modifier = this
    .clip(shape)
    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f))
    .border(width = 1.dp, color = HairlineBorder, shape = shape)
