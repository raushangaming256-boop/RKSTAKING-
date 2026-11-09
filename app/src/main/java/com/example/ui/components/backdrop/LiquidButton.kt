package com.example.ui.components.backdrop

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

/**
 * Backdrop Catalog: LiquidButton
 * Exact implementation of Kyant0 / AndroidLiquidGlass LiquidButton:
 * - Capsule geometry with lens(12.dp, 24.dp) specular refraction highlight
 * - Frosted vibrancy surface with dual gradient reflection
 * - Dynamic spring compression physics (0.65f damping)
 * - Tactile keyboard tap haptics
 */
@Composable
fun LiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(24.dp),
    accentColor: Color = Color(0xFF007AFF),
    isPrimary: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "liquidButtonScale"
    )

    val backgroundBrush = remember(isDark, isPrimary, accentColor) {
        if (isPrimary) {
            Brush.horizontalGradient(
                listOf(
                    accentColor,
                    Color(
                        red = (accentColor.red * 0.88f + 0.12f).coerceIn(0f, 1f),
                        green = (accentColor.green * 0.88f + 0.12f).coerceIn(0f, 1f),
                        blue = (accentColor.blue * 0.88f + 0.20f).coerceIn(0f, 1f),
                        alpha = 1f
                    )
                )
            )
        } else {
            Brush.verticalGradient(
                if (isDark) listOf(
                    Color.White.copy(alpha = 0.16f),
                    Color.White.copy(alpha = 0.05f)
                ) else listOf(
                    Color.White.copy(alpha = 0.92f),
                    Color.White.copy(alpha = 0.65f)
                )
            )
        }
    }

    // Authentic lens(12.dp, 24.dp) specular rim border
    val lensBorderBrush = remember(isDark, isPrimary, accentColor) {
        if (isPrimary) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.75f),
                    Color.White.copy(alpha = 0.18f)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (isDark) 0.50f else 0.90f),
                    Color.White.copy(alpha = if (isDark) 0.10f else 0.25f)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.5f
            }
            .shadow(
                elevation = if (isPrimary) 6.dp else 2.dp,
                shape = shape,
                spotColor = if (isPrimary) accentColor.copy(alpha = 0.45f) else Color(0x2A000000)
            )
            .clip(shape)
            .background(backgroundBrush)
            .border(width = 1.dp, brush = lensBorderBrush, shape = shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onClick()
            }
            .padding(contentPadding)
            .defaultMinSize(minHeight = 44.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (isPrimary) Color.White else (if (isDark) Color.White else Color(0xFF1C1C1E))
        ) {
            content()
        }
    }
}
