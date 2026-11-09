package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// OkKhata Custom Emerald Ledger Color Tokens
val OkGreenPrimary = Color(0xFF008955)
val OkGreenLight = Color(0xFFE8F5E9)
val OkGreenDark = Color(0xFF005A36)
val OkOrangeRed = Color(0xFFD32F2F)
val OkOrangeRedLight = Color(0xFFFFEBEE)
val OkTextDark = Color(0xFF1A1C1E)
val OkTextMuted = Color(0xFF6E7174)
val OkBackgroundLight = Color(0xFFF3F5F4)
val OkSurfaceWhite = Color(0xFFFFFFFF)

// Theme-adaptive composable color helpers
val okTextDark: Color
  @Composable
  get() = androidx.compose.material3.MaterialTheme.colorScheme.onSurface

val okTextMuted: Color
  @Composable
  get() = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant

val okBackgroundLight: Color
  @Composable
  get() = androidx.compose.material3.MaterialTheme.colorScheme.background

val okSurfaceWhite: Color
  @Composable
  get() = androidx.compose.material3.MaterialTheme.colorScheme.surface


