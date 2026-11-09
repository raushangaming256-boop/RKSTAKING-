package com.example.ui.theme

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = OkGreenPrimary,
    secondary = OkGreenDark,
    background = Color(0xFF121413),
    surface = Color(0xFF1C1E1D),
    surfaceVariant = Color(0xFF262827),
    onSurface = Color(0xFFE3E2E6),
    onSurfaceVariant = Color(0xFF9EA3A5),
    onBackground = Color(0xFFE3E2E6),
    onPrimary = Color.White,
    onSecondary = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = OkGreenPrimary,
    secondary = OkGreenDark,
    background = OkBackgroundLight,
    surface = OkSurfaceWhite,
    surfaceVariant = Color(0xFFEAEAEA),
    onSurface = OkTextDark,
    onSurfaceVariant = OkTextMuted,
    onBackground = OkTextDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    error = OkOrangeRed
  )

private val NothingDarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFFF2A2A), // Luminous Nothing OS Red
    secondary = Color(0xFF8C8C8C), // Industrial Dot Grey
    background = Color(0xFF000000), // Pure OLED Black
    surface = Color(0xFF0B0B0B), // Matte Graphite Surface
    surfaceVariant = Color(0xFF1A1A1A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFA0A0A0),
    error = Color(0xFFFF2A2A),
    outline = Color(0xFF222222)
  )

private val NothingLightColorScheme =
  lightColorScheme(
    primary = Color(0xFF000000), // Monochrome Bold Black
    secondary = Color(0xFFFF2A2A), // Bright Nothing OS Red Accent
    background = Color(0xFFF9F9F9), // Ice Paper Background
    surface = Color(0xFFFFFFFF), // Clean White
    surfaceVariant = Color(0xFFEFEFEF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF666666),
    error = Color(0xFFFF2A2A),
    outline = Color(0xFFE5E5E5)
  )

private val Cyber2070DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF00F0FF), // Cyber Cyan Neon
    secondary = Color(0xFFFF007F), // Cyber Magenta Neon
    tertiary = Color(0xFF7000FF), // Quantum Violet
    background = Color(0xFF040711), // Deep Quantum OLED Dark
    surface = Color(0xFF0B1021), // Cyber Glass Surface
    surfaceVariant = Color(0xFF141D36), // Holographic Variant Surface
    onPrimary = Color(0xFF001A24),
    onSecondary = Color.White,
    onBackground = Color(0xFFE2F3FF),
    onSurface = Color(0xFFECF6FF),
    onSurfaceVariant = Color(0xFF8BAECF),
    error = Color(0xFFFF2E63),
    outline = Color(0xFF00F0FF)
  )

private val Cyber2070LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF0080FF), // Electric Cyber Blue
    secondary = Color(0xFFD8006C), // Deep Neon Magenta
    tertiary = Color(0xFF6200EE), // Quantum Purple
    background = Color(0xFFEEF5FF), // Cyber Ice White
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFDDEBFC),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF081226),
    onSurface = Color(0xFF0A162C),
    onSurfaceVariant = Color(0xFF425A78),
    error = Color(0xFFFF2E63),
    outline = Color(0xFF0080FF)
  )

private val KitsuneDarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFFF5722), // Signature Fox Flame Orange
    secondary = Color(0xFFFF9100), // Kitsune Amber Gold
    tertiary = Color(0xFFFF3D00), // Magisk Crimson Flare
    background = Color(0xFF0D0F17), // Deep Matte AMOLED Fox Canvas
    surface = Color(0xFF161924), // Smooth Squircle Card
    surfaceVariant = Color(0xFF222636), // Elevated Pill Surface
    primaryContainer = Color(0xFF3A180E), // Warm Fox Glow Container
    onPrimaryContainer = Color(0xFFFFCCBC),
    secondaryContainer = Color(0xFF382305),
    onSecondaryContainer = Color(0xFFFFE082),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F3F5),
    onSurface = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFFAEB7C6),
    error = Color(0xFFFF334B),
    errorContainer = Color(0xFF3B1015),
    onErrorContainer = Color(0xFFFFB4AB),
    outline = Color(0xFF363C4E)
  )

private val KitsuneLightColorScheme =
  lightColorScheme(
    primary = Color(0xFFFF5722), // Vivid Fox Coral Flame
    secondary = Color(0xFFFF9100), // Rich Amber
    tertiary = Color(0xFFE64A19), // Deep Magisk Fox Red
    background = Color(0xFFFBF8F5), // Smooth Warm Fox Porcelain
    surface = Color(0xFFFFFFFF), // Pure White Squircle
    surfaceVariant = Color(0xFFF7EFE9), // Warm Fox Accent Surface
    primaryContainer = Color(0xFFFFEBE5), // Soft Fox Glow Container
    onPrimaryContainer = Color(0xFF8B2500),
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFF8C4C00),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E2022),
    onSurface = Color(0xFF1E2022),
    onSurfaceVariant = Color(0xFF5F6368),
    error = Color(0xFFD32F2F),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    outline = Color(0xFFE8DDD5)
  )

// 🍎 Apple iOS Pure Clean Liquid Glass Color Scheme (Glass Effect, Zero-Lag 7-Core Engine)
private val IosLightColorScheme =
  lightColorScheme(
    primary = Color(0xFF007AFF), // Apple iOS System Blue
    secondary = Color(0xFF34C759), // Apple iOS System Green
    tertiary = Color(0xFF5856D6), // Apple iOS System Purple
    background = Color(0xFFF4F7FC), // Frosted Crystal Glass Light Canvas
    surface = Color(0xFFFFFFFF), // Pure White Glass Surface
    surfaceVariant = Color(0xFFEBF1FA), // Light Frosted Glass Pill
    primaryContainer = Color(0xFFE5F0FF),
    onPrimaryContainer = Color(0xFF004085),
    secondaryContainer = Color(0xFFE6F9EC),
    onSecondaryContainer = Color(0xFF135A27),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E293B), // Deep Slate Navy Glass Text (instead of harsh black)
    onSurface = Color(0xFF1E293B), // Deep Slate Navy Glass Text (instead of harsh black)
    onSurfaceVariant = Color(0xFF64748B), // Frosted Slate Secondary Label
    error = Color(0xFFFF3B30), // Apple iOS System Red
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFD32F2F),
    outline = Color(0x33007AFF), // Translucent Apple Glass Border
    outlineVariant = Color(0x1F007AFF)
  )

// 🍎 Apple iOS Liquid Glass Dark Color Scheme (Glass Effect Colors instead of Harsh Black)
private val IosDarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF0A84FF), // iOS Dynamic Glass Blue
    secondary = Color(0xFF30D158), // iOS Dynamic Glass Green
    tertiary = Color(0xFF5E5CE6),
    background = Color(0xFF111827), // Deep Frosted Glass Sapphire-Slate (Glass Effect instead of #000000 Black)
    surface = Color(0xFF1A2234), // Frosted Liquid Glass Surface (instead of #1C1C1E Black)
    surfaceVariant = Color(0xFF243048), // Elevated Frosted Glass Card Sheen
    primaryContainer = Color(0xFF0F325E),
    onPrimaryContainer = Color(0xFFB8DAFF),
    secondaryContainer = Color(0xFF0D391A),
    onSecondaryContainer = Color(0xFFB5F2C7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9), // Luminous Crystal White Text
    onSurface = Color(0xFFF1F5F9), // Luminous Crystal White Text
    onSurfaceVariant = Color(0xFF94A3B8), // Frosted Glass Muted Text
    error = Color(0xFFFF453A),
    outline = Color(0x3DFFFFFF), // Crystal Specular Glass Edge Highlight (instead of dull dark gray)
    outlineVariant = Color(0x1AFFFFFF)
  )

@Composable
fun MyApplicationTheme(
  iosTheme: Boolean = false,
  kitsuneTheme: Boolean = true,
  nothingTheme: Boolean = false,
  cyber2070Theme: Boolean = false,
  selectedFont: String = "Google Inter (Clean Sans)",
  customFontPath: String = "",
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      iosTheme -> {
        if (darkTheme) IosDarkColorScheme else IosLightColorScheme
      }
      kitsuneTheme -> {
        if (darkTheme) KitsuneDarkColorScheme else KitsuneLightColorScheme
      }
      cyber2070Theme -> {
        if (darkTheme) Cyber2070DarkColorScheme else Cyber2070LightColorScheme
      }
      nothingTheme -> {
        if (darkTheme) NothingDarkColorScheme else NothingLightColorScheme
      }
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val resolvedFontFamily = remember(selectedFont, customFontPath) {
    resolveFontFamily(selectedFont, customFontPath)
  }
  val typography = remember(resolvedFontFamily, nothingTheme) {
    if (nothingTheme) NothingTypography else getTypographyForFont(resolvedFontFamily)
  }

  CompositionLocalProvider(
    LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = resolvedFontFamily)
  ) {
    MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
  }
}


