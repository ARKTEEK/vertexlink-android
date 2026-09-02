package com.vertexlink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VertexLightColorScheme = lightColorScheme(
  primary = VertexColors.AccentPrimary,
  onPrimary = VertexColors.TextOnAccent,
  primaryContainer = VertexColors.AccentSubtle,
  onPrimaryContainer = VertexColors.AccentPrimary,
  secondary = VertexColors.AccentSecondary,
  onSecondary = VertexColors.TextOnAccent,
  background = VertexColors.BgRootEnd,
  onBackground = VertexColors.TextPrimary,
  surface = VertexColors.BgSurfaceHigh,
  onSurface = VertexColors.TextPrimary,
  surfaceVariant = VertexColors.BgSurfaceMid,
  onSurfaceVariant = VertexColors.TextSecondary,
  outline = VertexColors.BorderSubtle,
  outlineVariant = VertexColors.BorderSubtle,
  error = VertexColors.Danger,
  onError = VertexColors.TextOnAccent
)

@Composable
fun VertexLinkTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = VertexLightColorScheme,
    typography = Typography,
    content = content
  )
}