package com.freetune.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Spotify's actual production green, not a stylized variant.
val AccentGreen = Color(0xFF1ED760)

// Flat, rich near-black surfaces - matches Spotify/Uber's real dark UI
// rather than a stylized "dark mode". Depth comes from elevation shadows,
// not from color gradients.
val AppBackground = Color(0xFF121212)
val SurfaceCard = Color(0xFF1C1C1E)
val SurfaceCardRaised = Color(0xFF242426)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB3B3B3)
val HairlineBorder = Color(0x1FFFFFFF)
val ShadowColor = Color(0xFF000000)

private val DarkColors = darkColorScheme(
    primary = AccentGreen,
    background = AppBackground,
    surface = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

// The whole UI is one deliberate dark theme (rich black surfaces, real
// elevation shadows) - there's no paired light backdrop, so this
// intentionally ignores the system's light/dark setting rather than
// accepting one.
@Composable
fun FreeTuneTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
