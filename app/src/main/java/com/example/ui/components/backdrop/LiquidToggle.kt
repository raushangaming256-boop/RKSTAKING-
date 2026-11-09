package com.example.ui.components.backdrop

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

/**
 * Backdrop Catalog: LiquidToggle
 * Glassmorphic sliding switch with specular rim reflection, glowing active track,
 * spring sliding physics, and tactile haptic feedback.
 */
@Composable
fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activeColor: Color = Color(0xFF34C759) // iOS System Green
) {
    val isDark = isSystemInDarkTheme()
    val view = LocalView.current
    val density = LocalDensity.current

    val trackWidth = 50.dp
    val trackHeight = 28.dp
    val thumbSize = 24.dp
    val padding = 2.dp

    val maxOffset = trackWidth - thumbSize - (padding * 2)

    val thumbProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "liquidToggleThumb"
    )

    val trackBgBrush = remember(checked, isDark, activeColor) {
        if (checked) {
            Brush.horizontalGradient(
                listOf(
                    activeColor,
                    Color(
                        red = (activeColor.red * 0.85f).coerceIn(0f, 1f),
                        green = (activeColor.green * 1.05f).coerceIn(0f, 1f),
                        blue = (activeColor.blue * 0.95f).coerceIn(0f, 1f),
                        alpha = 1f
                    )
                )
            )
        } else {
            Brush.verticalGradient(
                if (isDark) listOf(
                    Color.White.copy(alpha = 0.16f),
                    Color.White.copy(alpha = 0.06f)
                ) else listOf(
                    Color.Black.copy(alpha = 0.12f),
                    Color.Black.copy(alpha = 0.08f)
                )
            )
        }
    }

    val trackBorderBrush = remember(checked, isDark) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (checked) 0.6f else (if (isDark) 0.35f else 0.7f)),
                Color.White.copy(alpha = if (checked) 0.15f else (if (isDark) 0.08f else 0.15f))
            )
        )
    }

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(14.dp))
            .background(trackBgBrush)
            .border(width = 0.75.dp, brush = trackBorderBrush, shape = RoundedCornerShape(14.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onCheckedChange(!checked)
            }
            .padding(padding),
        contentAlignment = Alignment.CenterStart
    ) {
        val currentOffset = maxOffset * thumbProgress

        Box(
            modifier = Modifier
                .offset(x = currentOffset)
                .size(thumbSize)
                .shadow(
                    elevation = 3.dp,
                    shape = CircleShape,
                    spotColor = Color(0x33000000)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color(0xFFF2F2F7)
                        )
                    )
                )
                .border(
                    width = 0.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.Black.copy(alpha = 0.1f)
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}
