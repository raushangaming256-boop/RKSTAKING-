package com.example.ui.components.backdrop

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.KhataViewModel

/**
 * Backdrop Catalog Glass Tint Presets
 */
data class GlassTintPreset(
    val name: String,
    val color: Color,
    val description: String
)

val GLASS_TINT_PRESETS = listOf(
    GlassTintPreset("iOS Frost", Color(0xFFFFFFFF), "Pristine clear acrylic"),
    GlassTintPreset("Neon Cyan", Color(0xFF00C7BE), "Vibrant liquid cyber"),
    GlassTintPreset("Emerald", Color(0xFF34C759), "iOS mint green glass"),
    GlassTintPreset("Sunset Amber", Color(0xFFFF9500), "Warm golden refraction"),
    GlassTintPreset("Rose Quartz", Color(0xFFFF2D55), "Neon ruby specular"),
    GlassTintPreset("Amethyst", Color(0xFFAF52DE), "Royal liquid purple")
)

/**
 * Official Apple iOS Glass Materials
 * Exactly matching Kyant0 / Backdrop Material specifications
 */
data class IosGlassMaterial(
    val title: String,
    val subtitle: String,
    val blurDp: Float,
    val specular: Float,
    val iconDescription: String
)

val IOS_GLASS_MATERIALS = listOf(
    IosGlassMaterial("Ultra-Thin Glass", "Minimal dispersion, max clarity", 6f, 0.40f, "Very light acrylic layer"),
    IosGlassMaterial("Thin Glass", "Subtle frosted depth & crisp border", 14f, 0.55f, "iOS Sidebar & widget surface"),
    IosGlassMaterial("Regular Glass", "Standard iOS acrylic blur & shine", 24f, 0.75f, "Default iOS dock & card material"),
    IosGlassMaterial("Thick Glass", "Deep milky frost, elevated surface", 42f, 0.90f, "Control Center & sheets"),
    IosGlassMaterial("Chrome Specular", "Dual specular reflection with glow", 56f, 1.0f, "High-refraction metallic shine")
)

/**
 * Full-Screen Backdrop Catalog (Pure Kyant Backdrop Showcase)
 * - Zero App Settings / Clean Catalog UI
 * - Dedicated 3-tab liquid bottom navigation dock (Visuals FX, Controls UI, Materials)
 * - Live real-time backdrop blur, specular refraction, chromatic glow & tint customization
 * - All visual changes apply live across the full app like iOS
 */
@Composable
fun KishuBackdropCatalogScreen(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    val view = LocalView.current
    val isDark = isSystemInDarkTheme()

    // Persistent state collected from KhataViewModel
    val isGlassActive by viewModel.isGlassActive.collectAsStateWithLifecycle()
    val blurIntensity by viewModel.glassBlurIntensity.collectAsStateWithLifecycle()
    val tintIndex by viewModel.glassTintIndex.collectAsStateWithLifecycle()
    val specularIntensity by viewModel.specularIntensity.collectAsStateWithLifecycle()
    val chromaticGlowEnabled by viewModel.chromaticGlowEnabled.collectAsStateWithLifecycle()
    val grainOverlayEnabled by viewModel.grainOverlayEnabled.collectAsStateWithLifecycle()

    val mass4xActive by viewModel.mass4xEngineActive.collectAsStateWithLifecycle()
    val display90HzActive by viewModel.display90HzActive.collectAsStateWithLifecycle()
    val touch240HzActive by viewModel.touch240HzActive.collectAsStateWithLifecycle()
    val scrollGearSound by viewModel.scrollGearSoundEnabled.collectAsStateWithLifecycle()
    val soundboxAlert by viewModel.soundboxAlertEnabled.collectAsStateWithLifecycle()

    // Internal navigation tab: 0 = Visuals FX, 1 = Controls UI, 2 = Materials
    var selectedTab by remember { mutableIntStateOf(0) }

    // Active tint color preset
    val safeTintIndex = tintIndex.coerceIn(0, GLASS_TINT_PRESETS.size - 1)
    val activeTintPreset = GLASS_TINT_PRESETS[safeTintIndex]

    // Demo control state for Tab 1
    var demoVolume by remember { mutableFloatStateOf(0.75f) }
    var demoBrightness by remember { mutableFloatStateOf(0.60f) }
    var demoSegmentedIndex by remember { mutableIntStateOf(1) }
    var showDemoDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0D0D11) else Color(0xFFF6F6F9))
    ) {
        // Ambient Liquid Mesh Wallpaper (Dynamic Apple / Kyant Backdrop aesthetic)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            activeTintPreset.color.copy(alpha = if (isDark) 0.16f else 0.10f),
                            Color(0xFF007AFF).copy(alpha = if (isDark) 0.10f else 0.06f),
                            Color(0xFFAF52DE).copy(alpha = if (isDark) 0.08f else 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )
        // Dynamic Glowing Blur Orbs
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activeTintPreset.color.copy(alpha = if (isDark) 0.32f else 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFAF52DE).copy(alpha = if (isDark) 0.25f else 0.16f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00C7BE).copy(alpha = if (isDark) 0.22f else 0.14f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ==========================================
            // TOP BAR: Brand, FPS Pill, Close Button
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        activeTintPreset.color.copy(alpha = 0.35f),
                                        activeTintPreset.color.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                activeTintPreset.color.copy(alpha = 0.6f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Backdrop Catalog Icon",
                            tint = if (safeTintIndex == 0) {
                                if (isDark) Color.White else Color(0xFF1C1C1E)
                            } else {
                                activeTintPreset.color
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Backdrop Catalog",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Engine badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF34C759).copy(alpha = 0.18f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (display90HzActive) "90Hz" else "60Hz",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34C759)
                                )
                            }
                        }
                        Text(
                            text = "iOS Liquid Glass Engine • com.kyant.backdrop",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFF98989D) else Color(0xFF6E6E73)
                        )
                    }
                }

                // Liquid Spring Close Button
                LiquidButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onDismiss()
                    },
                    shape = CircleShape,
                    accentColor = Color(0xFF8E8E93),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Backdrop Catalog",
                        tint = if (isDark) Color.White else Color(0xFF1C1C1E),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ==========================================
            // MAIN SCROLLABLE CONTENT BODY
            // ==========================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    // TAB 0: Visuals FX Studio
                    0 -> VisualsFxTabContent(
                        viewModel = viewModel,
                        isGlassActive = isGlassActive,
                        blurIntensity = blurIntensity,
                        tintIndex = safeTintIndex,
                        activeTintPreset = activeTintPreset,
                        specularIntensity = specularIntensity,
                        chromaticGlowEnabled = chromaticGlowEnabled,
                        grainOverlayEnabled = grainOverlayEnabled,
                        mass4xActive = mass4xActive,
                        display90HzActive = display90HzActive,
                        touch240HzActive = touch240HzActive,
                        isDark = isDark
                    )

                    // TAB 1: Controls UI Showcase
                    1 -> ControlsUiTabContent(
                        viewModel = viewModel,
                        activeColor = if (safeTintIndex == 0) Color(0xFF007AFF) else activeTintPreset.color,
                        volume = demoVolume,
                        onVolumeChange = { demoVolume = it },
                        brightness = demoBrightness,
                        onBrightnessChange = { demoBrightness = it },
                        segmentedIndex = demoSegmentedIndex,
                        onSegmentedIndexChange = { demoSegmentedIndex = it },
                        scrollGearSound = scrollGearSound,
                        soundboxAlert = soundboxAlert,
                        onOpenDemoDialog = { showDemoDialog = true },
                        isDark = isDark
                    )

                    // TAB 2: Materials Showcase (Signature iOS Glass Shaders)
                    2 -> MaterialsTabContent(
                        viewModel = viewModel,
                        currentBlur = blurIntensity,
                        currentSpecular = specularIntensity,
                        activeColor = if (safeTintIndex == 0) Color(0xFF007AFF) else activeTintPreset.color,
                        isDark = isDark
                    )
                }
            }

            // ==========================================
            // DEDICATED BOTTOM DOCK: 3 TABS (NO SETTINGS)
            // Visuals FX | Controls UI | Materials 🪟
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                LiquidBottomTabs(
                    selectedTabIndex = { selectedTab },
                    tabsCount = 3,
                    activeHighlightColor = if (safeTintIndex == 0) Color(0xFF007AFF) else activeTintPreset.color,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidBottomTab(
                        title = "Visuals (दृश्य)",
                        icon = Icons.Default.AutoAwesome,
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        },
                        activeColor = if (safeTintIndex == 0) Color(0xFF007AFF) else activeTintPreset.color
                    )

                    LiquidBottomTab(
                        title = "Controls (कंट्रोल)",
                        icon = Icons.Default.TouchApp,
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        },
                        activeColor = if (safeTintIndex == 0) Color(0xFF007AFF) else activeTintPreset.color
                    )

                    LiquidBottomTab(
                        title = "Materials (शेडर्स)",
                        icon = Icons.Default.Layers,
                        isSelected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        },
                        activeColor = if (safeTintIndex == 0) Color(0xFF007AFF) else activeTintPreset.color
                    )
                }
            }
        }
    }

    if (showDemoDialog) {
        BackdropCatalogDialog(
            viewModel = viewModel,
            onDismiss = { showDemoDialog = false }
        )
    }
}

/**
 * TAB 0: Visuals FX Content
 * Focus on Backdrop visual effects like blur, refraction, and chromatic glow.
 */
@Composable
private fun VisualsFxTabContent(
    viewModel: KhataViewModel,
    isGlassActive: Boolean,
    blurIntensity: Float,
    tintIndex: Int,
    activeTintPreset: GlassTintPreset,
    specularIntensity: Float,
    chromaticGlowEnabled: Boolean,
    grainOverlayEnabled: Boolean,
    mass4xActive: Boolean,
    display90HzActive: Boolean,
    touch240HzActive: Boolean,
    isDark: Boolean
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ==========================================
        // 1. LIVE BACKDROP LENS PREVIEW CARD
        // ==========================================
        Text(
            text = "LIVE BACKDROP LENS PREVIEW",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFF98989D) else Color(0xFF6E6E73),
            letterSpacing = 1.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activeTintPreset.color.copy(alpha = if (isDark) 0.45f else 0.70f),
                            Color(0xFF007AFF).copy(alpha = 0.35f),
                            if (isDark) Color(0xFF121214) else Color(0xFFE5E5EA)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // Simulated ambient elements underneath (Backdrop Refraction Test Objects)
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .offset(x = (-10).dp, y = (-10).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF2D55), Color(0xFFFF375F))
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-20).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF9500), Color(0xFFFFCC00))
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 15.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00C7BE), Color(0xFF34C759))
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 30.dp, y = 10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFAF52DE), Color(0xFF5856D6))
                            )
                        )
                )
            }

            // The Floating Frosted Glass Lens Card
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = (1.2f * specularIntensity).coerceAtLeast(0.6f).dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = (0.90f * specularIntensity).coerceIn(0.2f, 1f)),
                                activeTintPreset.color.copy(alpha = 0.30f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                color = if (isDark) {
                    Color(0x751C1C1E)
                } else {
                    Color(0x8AFFFFFF)
                },
                shadowElevation = (8 * specularIntensity).dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (chromaticGlowEnabled) Color(0xFF34C759) else Color(0xFF8E8E93)
                                    )
                            )
                            Text(
                                text = "Backdrop Lens: ${activeTintPreset.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(activeTintPreset.color.copy(alpha = 0.20f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${blurIntensity.toInt()}dp Blur",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tintIndex == 0) {
                                    if (isDark) Color.White else Color(0xFF007AFF)
                                } else {
                                    activeTintPreset.color
                                }
                            )
                        }
                    }

                    Text(
                        text = "Real-time acrylic glass refraction with dual-gradient specular top shine and spring inertia physics.",
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFD1D1D6) else Color(0xFF3A3A3C),
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Specular: ${(specularIntensity * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFF98989D) else Color(0xFF6E6E73)
                        )
                        Text(
                            text = if (grainOverlayEnabled) "Film Grain: ON" else "Film Grain: OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFF98989D) else Color(0xFF6E6E73)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. BACKDROP BLUR INTENSITY SLIDER
        // ==========================================
        GlassCard(isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Backdrop Blur Intensity",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Background frosted dispersion (4dp - 64dp)",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF007AFF).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${blurIntensity.toInt()} dp",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007AFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LiquidSlider(
                    value = blurIntensity,
                    onValueChange = { viewModel.setGlassBlurIntensity(it) },
                    valueRange = 4f..64f,
                    accentColor = Color(0xFF007AFF),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ==========================================
        // 3. SPECULAR REFRACTION SHINE SLIDER
        // ==========================================
        GlassCard(isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Specular Refraction Shine",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Hairline border reflection opacity (0% - 100%)",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF34C759).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${(specularIntensity * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34C759)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LiquidSlider(
                    value = specularIntensity,
                    onValueChange = { viewModel.setSpecularIntensity(it) },
                    valueRange = 0f..1f,
                    accentColor = Color(0xFF34C759),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ==========================================
        // 4. GLASS TINT PALETTE SELECTOR
        // ==========================================
        GlassCard(isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Glass Tint & Chromatic Tone",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF1C1C1E)
                )
                Text(
                    text = "Select acrylic color wash applied across the full app",
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GLASS_TINT_PRESETS.forEachIndexed { index, preset ->
                        val isSelected = tintIndex == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.setGlassTintIndex(index)
                                }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(preset.color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) {
                                            if (isDark) Color.White else Color(0xFF1C1C1E)
                                        } else {
                                            Color.White.copy(alpha = 0.5f)
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (index == 0) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.name.take(6),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 5. MASTER GLASS, GLOW & FILM GRAIN
        // ==========================================
        GlassCard(isDark = isDark) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Master Glass Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Liquid Glass Engine Active",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Enable system-wide frosted glass across all screens",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = isGlassActive,
                        onCheckedChange = { viewModel.setGlassMode(it) },
                        activeColor = Color(0xFF007AFF)
                    )
                }

                // Chromatic Glow Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chromatic Edge Glow",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Prism refraction dispersion along borders",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = chromaticGlowEnabled,
                        onCheckedChange = { viewModel.setChromaticGlowEnabled(it) },
                        activeColor = activeTintPreset.color
                    )
                }

                // Film Grain Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Film Grain Texture",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Organic subtle grain overlay on frosted acrylic",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = grainOverlayEnabled,
                        onCheckedChange = { viewModel.setGrainOverlayEnabled(it) },
                        activeColor = Color(0xFFFF9500)
                    )
                }
            }
        }

        // ==========================================
        // 6. HARDWARE PACER & SPRING ENGINE
        // ==========================================
        GlassCard(isDark = isDark) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mass 4x Engine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mass 4x Spring Physics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Natural spring inertia on all buttons and toggles",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = mass4xActive,
                        onCheckedChange = { viewModel.setMass4xEngineActive(it) },
                        activeColor = Color(0xFF5856D6)
                    )
                }

                // 90Hz / 120Hz Pacer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "High Refresh Pacer (90Hz / 120Hz)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Unlock high FPS compositor pipeline",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = display90HzActive,
                        onCheckedChange = { viewModel.setDisplay90HzActive(it) },
                        activeColor = Color(0xFF34C759)
                    )
                }

                // 240Hz Touch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "240Hz Touch Sampling Boost",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Ultra-low gesture latency on swipe and fling",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = touch240HzActive,
                        onCheckedChange = { viewModel.setTouch240HzActive(it) },
                        activeColor = Color(0xFFFF2D55)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

/**
 * TAB 1: Controls UI Content
 * Complete interactive showcase of Liquid Sliders, Toggles, Push Buttons, and Spring Haptics.
 */
@Composable
private fun ControlsUiTabContent(
    viewModel: KhataViewModel,
    activeColor: Color,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    segmentedIndex: Int,
    onSegmentedIndexChange: (Int) -> Unit,
    scrollGearSound: Boolean,
    soundboxAlert: Boolean,
    onOpenDemoDialog: () -> Unit,
    isDark: Boolean
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "INTERACTIVE LIQUID CONTROLS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFF98989D) else Color(0xFF6E6E73),
            letterSpacing = 1.sp
        )

        // 0. Liquid Segmented Control (Signature Kyant0 / Apple iOS Style)
        GlassCard(isDark = isDark) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        text = "Liquid Segmented Control",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF1C1C1E)
                    )
                    Text(
                        text = "iOS frosted sliding pill indicator with spring physics",
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                    )
                }

                LiquidSegmentedControl(
                    items = listOf("Standard", "Frosted", "Liquid Glass"),
                    selectedIndex = segmentedIndex,
                    onItemSelected = onSegmentedIndexChange,
                    activeColor = activeColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 1. Interactive Liquid Sliders
        GlassCard(isDark = isDark) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Slider 1: Volume
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = activeColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Liquid Volume Slider",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                        }
                        Text(
                            text = "${(volume * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LiquidSlider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        accentColor = activeColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Slider 2: Brightness
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brightness6,
                                contentDescription = null,
                                tint = Color(0xFFFF9500),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Liquid Vibrancy Slider",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                        }
                        Text(
                            text = "${(brightness * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9500)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LiquidSlider(
                        value = brightness,
                        onValueChange = onBrightnessChange,
                        accentColor = Color(0xFFFF9500),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2. Interactive Liquid Push Buttons (Spring Compression)
        GlassCard(isDark = isDark) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Spring Compression Buttons",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF1C1C1E)
                )
                Text(
                    text = "Tap buttons below to experience tactile scale-down physics",
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiquidButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            Toast.makeText(context, "Primary Action Tapped", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        accentColor = Color(0xFF007AFF)
                    ) {
                        Text("+ Primary", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    LiquidButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            Toast.makeText(context, "देंगे (Success) Tapped", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        accentColor = Color(0xFF34C759)
                    ) {
                        Text("देंगे (Due)", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    LiquidButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            Toast.makeText(context, "लेंगे (Advance) Tapped", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        accentColor = Color(0xFFFF3B30)
                    ) {
                        Text("लेंगे (Adv)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Showcase Backdrop Modal Dialog Button
                LiquidButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onOpenDemoDialog()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = false,
                    accentColor = activeColor,
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = if (isDark) Color.White else Color(0xFF1C1C1E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Backdrop Modal Dialog (डायलॉग)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                    }
                }
            }
        }

        // 3. Audio & Haptics Toggles
        GlassCard(isDark = isDark) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Scroll gear sound
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scroll Gear Click Sound",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Mechanical rotary click audio while scrolling",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = scrollGearSound,
                        onCheckedChange = { viewModel.setScrollGearSoundEnabled(it) },
                        activeColor = Color(0xFF007AFF)
                    )
                }

                // Soundbox Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Soundbox Payment Alerts",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Voice speech feedback on ledger transactions",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                    }
                    LiquidToggle(
                        checked = soundboxAlert,
                        onCheckedChange = { viewModel.setSoundboxAlertEnabled(it) },
                        activeColor = Color(0xFF34C759)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

/**
 * TAB 2: Materials Showcase (Signature iOS Glass Shaders)
 * Kyant Backdrop signature materials: Ultra-Thin, Thin, Regular, Thick, Chrome
 */
@Composable
private fun MaterialsTabContent(
    viewModel: KhataViewModel,
    currentBlur: Float,
    currentSpecular: Float,
    activeColor: Color,
    isDark: Boolean
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SIGNATURE APPLE GLASS MATERIALS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFF98989D) else Color(0xFF6E6E73),
            letterSpacing = 1.sp
        )

        Text(
            text = "Tap any material to apply its authentic HIG blur & specular refraction preset across the full app:",
            fontSize = 13.sp,
            color = if (isDark) Color(0xFFD1D1D6) else Color(0xFF3A3A3C)
        )

        IOS_GLASS_MATERIALS.forEach { material ->
            val isCurrent = (currentBlur.toInt() == material.blurDp.toInt())

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = if (isCurrent) 2.dp else 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                if (isCurrent) activeColor else Color.White.copy(alpha = if (isDark) 0.35f else 0.85f),
                                if (isCurrent) activeColor.copy(alpha = 0.4f) else Color.White.copy(alpha = if (isDark) 0.08f else 0.20f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        viewModel.setGlassBlurIntensity(material.blurDp)
                        viewModel.setSpecularIntensity(material.specular)
                        Toast.makeText(context, "${material.title} लागू किया गया!", Toast.LENGTH_SHORT).show()
                    },
                color = if (isDark) Color(0xEB1C1C1E) else Color(0xF8FFFFFF),
                shadowElevation = if (isCurrent) 8.dp else 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = material.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(activeColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Active",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = activeColor
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = material.subtitle,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Blur: ${material.blurDp.toInt()}dp",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = activeColor
                            )
                            Text(
                                text = "Specular: ${(material.specular * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF34C759)
                            )
                        }
                    }

                    LiquidButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            viewModel.setGlassBlurIntensity(material.blurDp)
                            viewModel.setSpecularIntensity(material.specular)
                            Toast.makeText(context, "${material.title} लागू किया गया!", Toast.LENGTH_SHORT).show()
                        },
                        isPrimary = isCurrent,
                        accentColor = activeColor,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isCurrent) "लागू है" else "लागू करें",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

/**
 * Reusable Glassmorphic Card Container with Specular Hairline
 */
@Composable
private fun GlassCard(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.35f else 0.85f),
                        Color.White.copy(alpha = if (isDark) 0.08f else 0.20f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        color = if (isDark) Color(0xEB1C1C1E) else Color(0xF8FFFFFF),
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
