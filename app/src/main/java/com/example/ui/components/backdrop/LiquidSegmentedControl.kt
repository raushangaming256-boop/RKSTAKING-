package com.example.ui.components.backdrop

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Backdrop Catalog: LiquidSegmentedControl
 * Authentic Kyant0 / AndroidLiquidGlass signature iOS-style segmented control
 * with spring sliding frosted glass indicator and specular highlight.
 */
@Composable
fun LiquidSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF007AFF)
) {
    val isDark = isSystemInDarkTheme()
    val view = LocalView.current

    Box(
        modifier = modifier
            .height(46.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isDark) Color(0x66000000) else Color(0x22000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isDark) Color(0x8A1C1C1E) else Color(0xB8F2F2F7)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.35f else 0.85f),
                        Color.White.copy(alpha = if (isDark) 0.08f else 0.20f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalWidth = maxWidth
            val count = items.size.coerceAtLeast(1)
            val segmentWidth = totalWidth / count
            val safeIndex = selectedIndex.coerceIn(0, count - 1)

            // Spring Sliding Liquid Highlight Pill
            val animatedPillOffset by animateDpAsState(
                targetValue = segmentWidth * safeIndex,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = 380f
                ),
                label = "liquidSegmentOffset"
            )

            // The Active Sliding Glass Indicator
            Box(
                modifier = Modifier
                    .offset(x = animatedPillOffset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = activeColor.copy(alpha = 0.30f)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isDark) {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF2C2C2E),
                                    Color(0xFF1E1E20)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color(0xFFF9F9FB)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.45f else 0.95f),
                                activeColor.copy(alpha = 0.25f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            // Segment Item Labels
            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { index, title ->
                    val isSelected = index == safeIndex
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                try {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                } catch (_: Exception) {}
                                onItemSelected(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                if (isDark) Color.White else Color(0xFF1C1C1E)
                            } else {
                                if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                            }
                        )
                    }
                }
            }
        }
    }
}
