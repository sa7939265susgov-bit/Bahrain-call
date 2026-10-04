package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BahrainRed,
    onPrimary = Color.White,
    primaryContainer = BahrainRedDark,
    onPrimaryContainer = Color.White,
    secondary = MeetGoogleBlue,
    onSecondary = Color.White,
    background = MeetDarkBackground,
    onBackground = Color.White,
    surface = MeetSurfaceDark,
    onSurface = Color.White,
    surfaceVariant = MeetSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC4C7C5)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BahrainRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD8),
    onPrimaryContainer = BahrainRedDark,
    secondary = MeetGoogleBlue,
    onSecondary = Color.White,
    background = MeetLightBackground,
    onBackground = Color(0xFF1B1B1F),
    surface = MeetSurfaceLight,
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44474F)
  )

@Composable
fun BahrainMeetTheme(
  darkTheme: Boolean = true, // Default to sleek dark video meet theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
