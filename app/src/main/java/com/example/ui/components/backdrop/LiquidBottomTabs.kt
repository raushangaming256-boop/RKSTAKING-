package com.example.ui.components.backdrop

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Backdrop Catalog: LiquidBottomTabs
 * Authentic Kyant0 / AndroidLiquidGlass signature liquid bottom navigation dock
 * with fluid sliding liquid capsule highlight pill driven by spring physics.
 */
@Composable
fun LiquidBottomTabs(
    selectedTabIndex: () -> Int,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    activeHighlightColor: Color = Color(0xFF007AFF),
    content: @Composable RowScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(30.dp),
                spotColor = if (isDark) Color(0x77000000) else Color(0x33000000),
                ambientColor = if (isDark) Color(0x55000000) else Color(0x1A000000)
            )
            .clip(RoundedCornerShape(30.dp))
            .background(
                if (isDark) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xCC17212B),
                            Color(0xBF0E1621),
                            Color(0xB317212B)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xEBFFFFFF),
                            Color(0xD9F0F4F8),
                            Color(0xCCFFFFFF)
                        )
                    )
                }
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.45f else 0.85f),
                        activeHighlightColor.copy(alpha = if (isDark) 0.15f else 0.20f),
                        Color.White.copy(alpha = if (isDark) 0.08f else 0.25f)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val totalWidth = maxWidth
            val singleTabWidth = totalWidth / tabsCount.coerceAtLeast(1)
            val activeIndex = selectedTabIndex().coerceIn(0, (tabsCount - 1).coerceAtLeast(0))

            // Signature Kyant Liquid Sliding Capsule Pill
            val animatedPillOffset by animateDpAsState(
                targetValue = singleTabWidth * activeIndex,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = 320f
                ),
                label = "kyantLiquidSlidingPill"
            )

            // Animated highlight glow pill under selected tab
            Box(
                modifier = Modifier
                    .offset(x = animatedPillOffset)
                    .width(singleTabWidth)
                    .height(56.dp)
                    .padding(horizontal = 3.dp, vertical = 2.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = activeHighlightColor.copy(alpha = 0.35f)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                activeHighlightColor.copy(alpha = if (isDark) 0.28f else 0.18f),
                                activeHighlightColor.copy(alpha = if (isDark) 0.16f else 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.65f else 0.95f),
                                activeHighlightColor.copy(alpha = 0.30f)
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

/**
 * Backdrop Catalog: LiquidBottomTab
 * Interactive tab item matching Kyant Backdrop Catalog specifications
 */
@Composable
fun RowScope.LiquidBottomTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF007AFF),
    badgeCount: Int? = null,
    badgeText: String? = null,
    amountText: String? = null,
    amountColor: Color = activeColor
) {
    val isDark = isSystemInDarkTheme()
    val unselectedColor = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.88f
            isSelected -> 1.06f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMedium),
        label = "tabItemScale"
    )

    Box(
        modifier = modifier
            .weight(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onClick()
            }
            .defaultMinSize(minHeight = 54.dp)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) activeColor else unselectedColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                if (badgeCount != null && badgeCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) activeColor else (if (isDark) Color(0xFF48484A) else Color(0xFF8E8E93)),
                        modifier = Modifier
                            .offset(x = 6.dp, y = (-2).dp)
                            .size(13.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) activeColor else (if (isDark) Color(0xFF48484A) else Color(0xFF8E8E93)),
                        modifier = Modifier
                            .offset(x = 6.dp, y = (-2).dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 6.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.5.dp)
                        )
                    }
                }
            }

            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) activeColor else unselectedColor,
                letterSpacing = 0.1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
