package com.example.ui.components.backdrop

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

/**
 * Backdrop Catalog: LiquidSlider
 * Smooth interactive liquid scrubber with dynamic expanding thumb, glowing fill track,
 * spring physics, and haptic feedback.
 */
@Composable
fun LiquidSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    accentColor: Color = Color(0xFF007AFF),
    enabled: Boolean = true
) {
    val isDark = isSystemInDarkTheme()
    val view = LocalView.current
    val density = LocalDensity.current

    val normalizedValue = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

    var isDragging by remember { mutableStateOf(false) }
    var lastHapticStep by remember { mutableIntStateOf(-1) }

    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "sliderThumbScale"
    )

    val thumbSizeDp = 22.dp
    val trackHeightDp = 10.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val thumbSizePx = with(density) { thumbSizeDp.toPx() }
        val usableWidthPx = (totalWidthPx - thumbSizePx).coerceAtLeast(1f)

        fun updateFromPx(x: Float) {
            val fraction = (x / usableWidthPx).coerceIn(0f, 1f)
            val computedValue = if (steps > 0) {
                val stepInterval = 1f / (steps + 1)
                val stepIndex = (fraction / stepInterval + 0.5f).toInt()
                if (stepIndex != lastHapticStep) {
                    lastHapticStep = stepIndex
                    try {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    } catch (_: Exception) {}
                }
                (valueRange.start + stepIndex * stepInterval * (valueRange.endInclusive - valueRange.start))
                    .coerceIn(valueRange.start, valueRange.endInclusive)
            } else {
                valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
            }
            onValueChange(computedValue)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeightDp)
                .clip(RoundedCornerShape(trackHeightDp / 2))
                .background(
                    if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.10f)
                )
                .border(
                    width = 0.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (isDark) 0.3f else 0.6f),
                            Color.White.copy(alpha = if (isDark) 0.05f else 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(trackHeightDp / 2)
                )
        ) {
            // Filled glowing portion
            Box(
                modifier = Modifier
                    .fillMaxWidth(normalizedValue)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(trackHeightDp / 2))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accentColor.copy(alpha = 0.85f),
                                accentColor
                            )
                        )
                    )
            )
        }

        // Draggable Liquid Thumb
        val thumbOffsetPx = normalizedValue * usableWidthPx
        val thumbOffsetDp = with(density) { thumbOffsetPx.toDp() }

        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp)
                .size(thumbSizeDp * thumbScale)
                .shadow(elevation = 4.dp, shape = CircleShape, spotColor = accentColor.copy(alpha = 0.5f))
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
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.95f),
                            accentColor.copy(alpha = 0.35f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Gesture detector across full area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .pointerInput(usableWidthPx, enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { offset ->
                        updateFromPx(offset.x - thumbSizePx / 2f)
                        try {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        } catch (_: Exception) {}
                    }
                }
                .pointerInput(usableWidthPx, enabled) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            updateFromPx(offset.x - thumbSizePx / 2f)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, _ ->
                            change.consume()
                            updateFromPx(change.position.x - thumbSizePx / 2f)
                        }
                    )
                }
        )
    }
}
