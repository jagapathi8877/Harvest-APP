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

private val AppColorScheme =
  darkColorScheme(
      primary = Saffron,
      secondary = DeepViolet,
      tertiary = Saffron,
      background = DarkBackground,
      surface = DarkSurface,
      error = DangerRed,
      onPrimary = DarkBackground,
      onSecondary = Color.White,
      onBackground = TextPrimary,
      onSurface = TextPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For branding, we turn off dynamic color to keep saffron
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      else -> AppColorScheme // This is a dark-only app basically based on specs
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
