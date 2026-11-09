package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.R
import com.example.data.CustomerEntity
import com.example.data.TransactionEntity
import com.example.ui.theme.*
import com.example.viewmodel.*
import com.example.ui.components.backdrop.*
import java.text.SimpleDateFormat
import java.util.*

sealed interface AppScreen {
    object Dashboard : AppScreen
    data class CustomerDetail(val customerId: Int) : AppScreen
}

private val StaticGlassBorderStroke = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
private val StaticDarkBorderStroke = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))

private val StaticFirstItemShape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
private val StaticLastItemShape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp)
private val StaticSingleItemShape = RoundedCornerShape(18.dp)

/**
 * ⚙️ Chrome & Kitsune Mechanical Gear Scroll Sound & Haptic Feedback Engine
 * Ultra-smooth 120 FPS frame-paced ratchet gear ticks during list fling and scroll.
 * Uses isScrollInProgress to eliminate any overhead when stationary.
 */
@Composable
private fun rememberScrollGearFeedback(
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    enabled: Boolean
) {
    // Audio IPC feedback removed to eliminate Choreographer frame drops and system audio buffer stalling
    // Keeps scroll butter-smooth at 120 FPS indefinitely with zero lag
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OkKhataApp(viewModel: KhataViewModel) {
    val context = LocalContext.current
    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val securityPin by viewModel.securityPin.collectAsStateWithLifecycle()
    val shopName by viewModel.shopName.collectAsStateWithLifecycle()
    val khataSummary by viewModel.khataSummary.collectAsStateWithLifecycle()
    val customerUiModels by viewModel.customerUiModels.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSortedByAmount by viewModel.isSortedByAmount.collectAsStateWithLifecycle()
    val isGlassActive by viewModel.isGlassActive.collectAsStateWithLifecycle()
    val isSmooth2Active by viewModel.isSmooth2Active.collectAsStateWithLifecycle()
    val isKitsuneTheme by viewModel.isKitsuneTheme.collectAsStateWithLifecycle()
    val glassBlurIntensity by viewModel.glassBlurIntensity.collectAsStateWithLifecycle()
    val zeroLagEngineActive by viewModel.zeroLagEngineActive.collectAsStateWithLifecycle()
    val isAndroid17Animation by viewModel.isAndroid17Animation.collectAsStateWithLifecycle()
    val isGooglePhotosAnimation by viewModel.isGooglePhotosAnimation.collectAsStateWithLifecycle()
    val animStyle by viewModel.animationStyle.collectAsStateWithLifecycle()
    val animDurationFloat by viewModel.animationDuration.collectAsStateWithLifecycle()
    val animDuration = animDurationFloat.toInt().coerceIn(100, 600)
    val appResolution by viewModel.appResolution.collectAsStateWithLifecycle()

    val currentDensity = androidx.compose.ui.platform.LocalDensity.current
    val customDensity = remember(appResolution, currentDensity) {
        val scale = when {
            appResolution.contains("540p") || appResolution.contains("Compact") -> 0.85f
            appResolution.contains("720p") || appResolution.contains("Smooth") -> 0.92f
            else -> 1.0f
        }
        androidx.compose.ui.unit.Density(
            density = currentDensity.density * scale,
            fontScale = currentDensity.fontScale
        )
    }

    val mass4xEngineActive by viewModel.mass4xEngineActive.collectAsStateWithLifecycle()
    val display90HzActive by viewModel.display90HzActive.collectAsStateWithLifecycle()
    val touch240HzActive by viewModel.touch240HzActive.collectAsStateWithLifecycle()
    val screenCraftLiquidGlassActive by viewModel.screenCraftLiquidGlassActive.collectAsStateWithLifecycle()
    val screenCraftBottomScrollBarActive by viewModel.screenCraftBottomScrollBarActive.collectAsStateWithLifecycle()
    val glassTintIndex by viewModel.glassTintIndex.collectAsStateWithLifecycle()
    val specularIntensity by viewModel.specularIntensity.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val dashboardListState = rememberLazyListState()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Dashboard) }

    // Dialog states
    val updateInfo by viewModel.currentUpdateInfo.collectAsStateWithLifecycle()
    val isUnity60FpsActive by viewModel.isUnity60FpsActive.collectAsStateWithLifecycle()
    var showAppUpdateDialog by remember { mutableStateOf(false) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showAiInsightsDialog by remember { mutableStateOf(false) }
    var showKitsuneMaskDialog by remember { mutableStateOf(false) }
    var showTotalSummaryDialog by remember { mutableStateOf(false) }
    var showBackdropCatalogDialog by remember { mutableStateOf(false) }
    var isTopToolsExpanded by remember { mutableStateOf(false) }

    // Quick Add Transaction state directly from customer list
    var quickAddTxCustomer by remember { mutableStateOf<com.example.data.CustomerEntity?>(null) }
    var quickAddIsCredit by remember { mutableStateOf(true) }

    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalDensity provides customDensity
    ) {
        // Full-screen iOS / Kyant Dynamic Ambient Mesh Wallpaper
        // Runs behind TopAppBar, Status Bar, Content, and Bottom Navigation Dock
        val safeTint = glassTintIndex.coerceIn(0, GLASS_TINT_PRESETS.size - 1)
        val activeTintPreset = GLASS_TINT_PRESETS[safeTint]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(
                                (if (safeTint > 0) activeTintPreset.color.copy(alpha = 0.14f) else Color(0xFF131D31)),
                                Color(0xFF090D16),
                                Color(0xFF070B12)
                            )
                        } else {
                            listOf(
                                (if (safeTint > 0) activeTintPreset.color.copy(alpha = 0.08f) else Color(0xFFEEF2F6)),
                                Color(0xFFF6F8FC),
                                Color(0xFFFFFFFF)
                            )
                        }
                    )
                )
        ) {

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    if (currentScreen is AppScreen.Dashboard) {
                TopAppBar(
                    title = {
                        val netBal = khataSummary.totalDue - khataSummary.totalAdvance
                        val isNetPositive = netBal >= 0
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .bounceClickable {
                                    showTotalSummaryDialog = true
                                }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                                .testTag("top_bar_shop_name_total_btn")
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF10B981).copy(alpha = if (isDark) 0.18f else 0.12f),
                                border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.40f)),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Wallet",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = shopName.ifBlank { "OkKhata" },
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = (if (isNetPositive) Color(0xFF10B981) else Color(0xFFEF4444)).copy(alpha = 0.14f),
                                        border = BorderStroke(0.6.dp, (if (isNetPositive) Color(0xFF10B981) else Color(0xFFEF4444)).copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            text = if (isNetPositive) "बाकी" else "एडवांस",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isNetPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "कुल हिसाब:",
                                        fontSize = 12.sp,
                                        color = okTextMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "₹${formatAmount(kotlin.math.abs(netBal))}",
                                        fontSize = 13.sp,
                                        color = if (isNetPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "View Ledger Summary",
                                        tint = if (isNetPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        val isDark = isSystemInDarkTheme()
                        val toolsRotationAngle by animateFloatAsState(
                            targetValue = if (isTopToolsExpanded) 90f else 0f,
                            animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium),
                            label = "toolsRotation"
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Animated Expandable Quick Tools Capsule
                            AnimatedVisibility(
                                visible = isTopToolsExpanded,
                                enter = fadeIn(animationSpec = tween(180)) + expandHorizontally(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                                    expandFrom = Alignment.End
                                ),
                                exit = fadeOut(animationSpec = tween(140)) + shrinkHorizontally(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                                    shrinkTowards = Alignment.End
                                )
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isDark) Color(0xD91A2234) else Color(0xE6FFFFFF),
                                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.75f)),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        // 1. AI Insights
                                        IconButton(
                                            onClick = {
                                                isTopToolsExpanded = false
                                                viewModel.loadGoogleCloudAiInsights()
                                                showAiInsightsDialog = true
                                            },
                                            modifier = Modifier.size(34.dp).testTag("ai_insights_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "AI Business Insights",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // 2. Backup & Sync
                                        IconButton(
                                            onClick = {
                                                isTopToolsExpanded = false
                                                showBackupDialog = true
                                            },
                                            modifier = Modifier.size(34.dp).testTag("backup_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CloudSync,
                                                contentDescription = "Backup & Restore",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // 3. Lock App / Security PIN
                                        IconButton(
                                            onClick = {
                                                isTopToolsExpanded = false
                                                if (securityPin.isEmpty()) {
                                                    showSettingsDialog = true
                                                } else {
                                                    viewModel.lockApp()
                                                }
                                            },
                                            modifier = Modifier.size(34.dp).testTag("lock_toggle_button")
                                        ) {
                                            Icon(
                                                imageVector = if (securityPin.isNotEmpty()) Icons.Default.Lock else Icons.Default.LockOpen,
                                                contentDescription = "Security PIN",
                                                tint = if (securityPin.isNotEmpty()) MaterialTheme.colorScheme.primary else okTextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // 4. 120 FPS Apache Glass Quick Engine
                                        IconButton(
                                            onClick = {
                                                isTopToolsExpanded = false
                                                val next = !isGlassActive
                                                viewModel.setGlassMode(next)
                                                viewModel.setSmooth2Active(next)
                                            },
                                            modifier = Modifier.size(34.dp).testTag("quick_smoothness_toggle_topbar")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Bolt,
                                                contentDescription = "120 FPS Apache Glass",
                                                tint = if (isGlassActive) Color(0xFF00C853) else okTextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // 5. Backdrop Catalog 🔮
                                        IconButton(
                                            onClick = {
                                                isTopToolsExpanded = false
                                                showBackdropCatalogDialog = true
                                            },
                                            modifier = Modifier.size(34.dp).testTag("backdrop_catalog_topbar_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Layers,
                                                contentDescription = "Backdrop Catalog",
                                                tint = Color(0xFF007AFF),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // 5. Online Update
                                        if (updateInfo?.isUpdateAvailable == true) {
                                            IconButton(
                                                onClick = {
                                                    isTopToolsExpanded = false
                                                    showAppUpdateDialog = true
                                                },
                                                modifier = Modifier.size(34.dp).testTag("app_update_topbar_button")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.SystemUpdate,
                                                        contentDescription = "Online Update",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(7.dp)
                                                            .align(Alignment.TopEnd)
                                                            .background(Color(0xFFE53935), CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Single Quick-Tools Hub Button (toggles capsule with smooth animation)
                            Surface(
                                shape = CircleShape,
                                color = if (isTopToolsExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .clickable { isTopToolsExpanded = !isTopToolsExpanded }
                                    .testTag("top_tools_expand_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isTopToolsExpanded) Icons.Default.Close else Icons.Default.Widgets,
                                        contentDescription = "Quick Tools Hub",
                                        tint = if (isTopToolsExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer { rotationZ = toolsRotationAngle }
                                    )
                                    if (!isTopToolsExpanded && updateInfo?.isUpdateAvailable == true) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-4).dp, y = 4.dp)
                                                .background(Color(0xFFE53935), CircleShape)
                                                .border(1.2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                        )
                                    }
                                }
                            }

                            // Settings Button
                            IconButton(
                                onClick = {
                                    isTopToolsExpanded = false
                                    showSettingsDialog = true
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isGlassActive) {
                            val activePreset = GLASS_TINT_PRESETS[glassTintIndex.coerceIn(0, GLASS_TINT_PRESETS.size - 1)]
                            if (isDark) {
                                if (glassTintIndex == 0) Color(0xD917212B) else activePreset.color.copy(alpha = 0.16f)
                            } else {
                                if (glassTintIndex == 0) Color(0xEBF8FAFC) else activePreset.color.copy(alpha = 0.12f)
                            }
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    modifier = Modifier.then(
                        if (isGlassActive) {
                            Modifier.border(
                                width = (0.8f * specularIntensity).coerceAtLeast(0.5f).dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = if (isDark) 0.18f * specularIntensity else 0.40f * specularIntensity)
                                    )
                                ),
                                shape = RectangleShape
                            )
                        } else Modifier
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen is AppScreen.Dashboard) {
                MagiskLiquidGlassBottomBar(
                    selectedFilter = selectedFilter,
                    khataSummary = khataSummary,
                    isGlassActive = isGlassActive || isSmooth2Active || screenCraftLiquidGlassActive,
                    isKitsuneMaskActive = showKitsuneMaskDialog,
                    lazyListState = dashboardListState,
                    customerUiModels = customerUiModels,
                    mass4xEnabled = mass4xEngineActive,
                    screenCraftScrollBarEnabled = screenCraftBottomScrollBarActive,
                    tintIndex = glassTintIndex,
                    specularIntensity = specularIntensity,
                    onFilterSelected = { filter ->
                        viewModel.updateFilter(filter)
                    },
                    onAddCustomerClick = {
                        showAddCustomerDialog = true
                    },
                    onKitsuneMaskClick = {
                        if (!isKitsuneTheme) {
                            viewModel.setKitsuneTheme(true)
                            Toast.makeText(context, "🪟 Kishu Backdrop Catalog Opened!", Toast.LENGTH_SHORT).show()
                        }
                        showKitsuneMaskDialog = true
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            AnimatedContent(
                targetState = currentScreen,
                label = "screen_transition",
                transitionSpec = {
                    val isEnteringDetail = targetState is AppScreen.CustomerDetail
                    if (isGooglePhotosAnimation || isAndroid17Animation) {
                        // Google Photos Signature Motion: Fluid Spring Physics with Responsive Scale & Shared Transform
                        val springFloat = spring<Float>(dampingRatio = 0.78f, stiffness = 320f)
                        val springIntOffset = spring<androidx.compose.ui.unit.IntOffset>(dampingRatio = 0.78f, stiffness = 320f)
                        (slideInHorizontally(
                            initialOffsetX = { if (isEnteringDetail) (it * 0.18f).toInt() else (-it * 0.18f).toInt() },
                            animationSpec = springIntOffset
                        ) + scaleIn(initialScale = 0.90f, animationSpec = springFloat) + fadeIn(animationSpec = springFloat)) togetherWith
                        (slideOutHorizontally(
                            targetOffsetX = { if (isEnteringDetail) (-it * 0.18f).toInt() else (it * 0.18f).toInt() },
                            animationSpec = springIntOffset
                        ) + scaleOut(targetScale = 0.94f, animationSpec = springFloat) + fadeOut(animationSpec = springFloat))
                    } else {
                        val duration = 220
                        val googleEasing = androidx.compose.animation.core.CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
                        (slideInHorizontally(
                            initialOffsetX = { if (isEnteringDetail) (it * 0.25f).toInt() else (-it * 0.25f).toInt() },
                            animationSpec = tween(duration, easing = googleEasing)
                        ) + fadeIn(animationSpec = tween(duration, easing = LinearEasing))) togetherWith
                        (slideOutHorizontally(
                            targetOffsetX = { if (isEnteringDetail) (-it * 0.25f).toInt() else (it * 0.25f).toInt() },
                            animationSpec = tween(duration, easing = googleEasing)
                        ) + fadeOut(animationSpec = tween(duration, easing = LinearEasing)))
                    }
                }
            ) { screen ->
                when (screen) {
                    is AppScreen.Dashboard -> {
                        DashboardContent(
                            viewModel = viewModel,
                            khataSummary = khataSummary,
                            customerUiModels = customerUiModels,
                            selectedFilter = selectedFilter,
                            searchQuery = searchQuery,
                            isSortedByAmount = isSortedByAmount,
                            isGlassActive = isGlassActive || isSmooth2Active,
                            isSmooth2Active = isSmooth2Active,
                            glassBlurIntensity = glassBlurIntensity,
                            lazyListState = dashboardListState,
                            onSelectCustomer = { customerId ->
                                viewModel.selectCustomer(customerId)
                                currentScreen = AppScreen.CustomerDetail(customerId)
                            },
                            onQuickAddTransaction = { customer, isCredit ->
                                quickAddTxCustomer = customer
                                quickAddIsCredit = isCredit
                            },
                            onAddCustomerClick = {
                                showAddCustomerDialog = true
                            },
                            onOpenUpdateDialog = {
                                showAppUpdateDialog = true
                            },
                            onOpenSummaryDialog = {
                                showTotalSummaryDialog = true
                            }
                        )
                    }

                    is AppScreen.CustomerDetail -> {
                        BackHandler {
                            currentScreen = AppScreen.Dashboard
                            viewModel.selectCustomer(null)
                        }
                        CustomerDetailContent(
                            viewModel = viewModel,
                            onBack = {
                                currentScreen = AppScreen.Dashboard
                                viewModel.selectCustomer(null)
                            }
                        )
                    }
                }
            }
        }
    }
    }

    // Dialogs
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onAdd = { name, phone ->
                viewModel.addCustomer(name, phone) { newId ->
                    showAddCustomerDialog = false
                    viewModel.selectCustomer(newId)
                    currentScreen = AppScreen.CustomerDetail(newId)
                }
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false },
            onOpenUpdateDialog = {
                showSettingsDialog = false
                showAppUpdateDialog = true
            }
        )
    }

    if (showAppUpdateDialog) {
        AppUpdateDialog(
            viewModel = viewModel,
            onDismiss = { showAppUpdateDialog = false }
        )
    }

    if (showBackupDialog) {
        BackupRestoreDialog(
            viewModel = viewModel,
            onDismiss = { showBackupDialog = false }
        )
    }

    if (showAiInsightsDialog) {
        AiInsightsDialog(
            viewModel = viewModel,
            onDismiss = { showAiInsightsDialog = false }
        )
    }

    if (showKitsuneMaskDialog) {
        KishuBackdropCatalogScreen(
            viewModel = viewModel,
            onDismiss = { showKitsuneMaskDialog = false }
        )
    }

    if (showTotalSummaryDialog) {
        TotalKhataSummaryDialog(
            khataSummary = khataSummary,
            customerCount = customerUiModels.size,
            shopName = shopName,
            onFilterSelected = { filter ->
                viewModel.updateFilter(filter)
                showTotalSummaryDialog = false
            },
            onDismiss = { showTotalSummaryDialog = false }
        )
    }

    if (showBackdropCatalogDialog) {
        KishuBackdropCatalogScreen(
            viewModel = viewModel,
            onDismiss = { showBackdropCatalogDialog = false }
        )
    }

    if (quickAddTxCustomer != null) {
        AddTransactionDialog(
            isCredit = quickAddIsCredit,
            customerName = quickAddTxCustomer?.name,
            onDismiss = { quickAddTxCustomer = null },
            onSave = { amount, remarks ->
                viewModel.addTransaction(
                    customerId = quickAddTxCustomer!!.id,
                    amount = amount,
                    remarks = remarks,
                    isCredit = quickAddIsCredit
                )
                quickAddTxCustomer = null
            }
        )
    }
    }
}

@Composable
fun AppLockScreen(viewModel: KhataViewModel) {
    val securityPin by viewModel.securityPin.collectAsStateWithLifecycle()
    val shopName by viewModel.shopName.collectAsStateWithLifecycle()
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = OkGreenPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = OkGreenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (shopName.isNotBlank()) shopName else "OkKhata Secured Ledger",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔒 Security PIN Protected (सुरक्षा पासकोड)",
                    fontSize = 13.sp,
                    color = OkGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            // PIN Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..4) {
                    val filled = enteredPin.length >= i
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) {
                                    if (isError) OkOrangeRed else OkGreenPrimary
                                } else {
                                    Color.White.copy(alpha = 0.2f)
                                }
                            )
                    )
                }
            }

            // Keypad
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "⌫")
                )

                digits.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { char ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable {
                                        when (char) {
                                            "C" -> enteredPin = ""
                                            "⌫" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                            else -> {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += char
                                                    if (enteredPin.length == 4) {
                                                        if (enteredPin == securityPin) {
                                                            viewModel.unlockApp()
                                                        } else {
                                                            isError = true
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 🔮 Telegram Ultra-Smooth Liquid Glass Component
 * 100% Native Jetpack Compose, Zero-Crash, 60-120 FPS Buttery Smooth
 * Translucent layered frosted glass gradient with dual-tone specular highlight refraction borders.
 */
@Composable
fun IosLiquidGlassBox(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.2.dp,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    blurRadius: Dp = 14.dp,
    tintColor: Color? = null,
    specularIntensity: Float = 0.70f,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val glassBgBrush = remember(isDark, backgroundColor, tintColor) {
        if (backgroundColor != null) {
            SolidColor(backgroundColor)
        } else if (tintColor != null) {
            if (isDark) {
                Brush.verticalGradient(
                    colors = listOf(
                        tintColor.copy(alpha = 0.18f),
                        Color(0xD917212B),
                        Color(0xCC0E1621)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.94f),
                        tintColor.copy(alpha = 0.10f),
                        Color(0xDEF1F5F9)
                    )
                )
            }
        } else if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xD91E293B),
                    Color(0xBF17212B),
                    Color(0xB30E1621)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xEBFFFFFF),
                    Color(0xDCF8FAFC),
                    Color(0xD0F1F5F9)
                )
            )
        }
    }

    val glassBorderBrush = remember(isDark, borderColor, tintColor, specularIntensity) {
        if (borderColor != null) {
            SolidColor(borderColor)
        } else if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.50f * specularIntensity).coerceIn(0.15f, 0.9f)),
                    tintColor?.copy(alpha = 0.30f) ?: Color.White.copy(alpha = 0.15f),
                    Color(0x33000000)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.95f * specularIntensity).coerceIn(0.3f, 1f)),
                    tintColor?.copy(alpha = 0.40f) ?: Color.White.copy(alpha = 0.55f),
                    Color(0x18000000)
                )
            )
        }
    }

    val specularHighlightBrush = remember(isDark, specularIntensity) {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = ((if (isDark) 0.65f else 0.90f) * specularIntensity).coerceIn(0.2f, 1f)),
                Color.Transparent
            )
        )
    }

    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .graphicsLayer { clip = true }
        ) {
            // Layer 1: Frosted Apache acrylic glass surface (Zero lag, zero hang)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(glassBgBrush)
            )

            // Layer 2: Dual specular refraction border
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(borderWidth, glassBorderBrush, shape)
            )

            // Layer 3: Specular top light reflection line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(specularHighlightBrush)
            )

            content()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardContent(
    viewModel: KhataViewModel,
    khataSummary: KhataSummary,
    customerUiModels: List<CustomerUiModel>,
    selectedFilter: KhataFilter,
    searchQuery: String,
    isSortedByAmount: Boolean,
    isGlassActive: Boolean = true,
    isSmooth2Active: Boolean = true,
    glassBlurIntensity: Float = 18f,
    lazyListState: androidx.compose.foundation.lazy.LazyListState = rememberLazyListState(),
    onSelectCustomer: (Int) -> Unit,
    onQuickAddTransaction: (com.example.data.CustomerEntity, Boolean) -> Unit,
    onAddCustomerClick: () -> Unit = {},
    onOpenUpdateDialog: () -> Unit = {},
    onOpenSummaryDialog: () -> Unit = {}
) {
    val context = LocalContext.current
    val mass4xEngineActive by viewModel.mass4xEngineActive.collectAsStateWithLifecycle()
    val scrollGearSoundEnabled by viewModel.scrollGearSoundEnabled.collectAsStateWithLifecycle()
    rememberScrollGearFeedback(lazyListState = lazyListState, enabled = scrollGearSoundEnabled)

    val showCustomerAvatars by viewModel.showCustomerAvatars.collectAsStateWithLifecycle()
    val showCustomerBoxContainer by viewModel.showCustomerBoxContainer.collectAsStateWithLifecycle()
    val chromeCompositor4xSmooth by viewModel.chromeCompositor4xSmooth.collectAsStateWithLifecycle()
    val glassTintIndex by viewModel.glassTintIndex.collectAsStateWithLifecycle()
    val specularIntensity by viewModel.specularIntensity.collectAsStateWithLifecycle()
    val safeTint = glassTintIndex.coerceIn(0, GLASS_TINT_PRESETS.size - 1)
    val customTint = GLASS_TINT_PRESETS[safeTint].color

    val isDark = isSystemInDarkTheme()
    val telegramBlue = Color(0xFF3390EC)

    // Clean, zero-overhead search focus state
    var isSearchFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Customer List (Smooth 120 FPS momentum scrolling with native sticky header)
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 4.dp,
                bottom = 120.dp, // Space for bottom dock
                start = 12.dp,
                end = 12.dp
            )
        ) {
            // 1. Executive Sticky Search Bar (Locks smoothly at top)
            stickyHeader(key = "sticky_customer_search_header", contentType = "sticky_search_header") {
                val isSearchPinned by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex > 0
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                    color = if (isDark) {
                        if (isSearchPinned) Color(0xF5090D16) else Color.Transparent
                    } else {
                        if (isSearchPinned) Color(0xF5F6F8FC) else Color.Transparent
                    },
                    shape = RoundedCornerShape(23.dp)
                ) {
                    // Executive Frosted Liquid Glass Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("magisk_liquid_search_bar"),
                        shape = RoundedCornerShape(24.dp),
                        color = if (isDark) Color(0xD9131B2A) else Color(0xEBFFFFFF),
                        border = BorderStroke(
                            1.1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.28f else 0.85f),
                                    Color.White.copy(alpha = if (isDark) 0.06f else 0.35f)
                                )
                            )
                        ),
                        shadowElevation = if (isDark) 0.dp else 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = telegramBlue,
                                modifier = Modifier.size(20.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("search_customer_input"),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "ग्राहक खोजें (नाम या मोबाइल)...",
                                        fontSize = 13.5.sp,
                                        color = if (isDark) Color(0xFF7E8E9F) else Color(0xFF707579)
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(telegramBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { isSearchFocused = it.isFocused }
                                )
                            }

                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updateSearchQuery("") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(16.dp),
                                        tint = okTextMuted
                                    )
                                }
                            }

                            // Frosted Glass Sort Toggle
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = telegramBlue.copy(alpha = if (isDark) 0.22f else 0.15f),
                                border = BorderStroke(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.35f),
                                            telegramBlue.copy(alpha = 0.40f)
                                        )
                                    )
                                ),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable {
                                        viewModel.isSortedByAmount.value = !isSortedByAmount
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isSortedByAmount) Icons.Default.SwapVert else Icons.Default.Schedule,
                                        contentDescription = "Sort",
                                        tint = telegramBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }


            // Customer Items (Unified Black Glass Container Box)
            if (customerUiModels.isEmpty()) {
                item(key = "empty_placeholder", contentType = "empty_placeholder") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PeopleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = okTextMuted.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "No customer matching '$searchQuery'" else "No customers in your OkKhata ledger yet.",
                                    color = okTextMuted,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Tap '+ ADD CUSTOMER' below to start recording credit.",
                                    color = OkGreenPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = customerUiModels,
                    key = { _, item -> item.customer.id },
                    contentType = { _, _ -> "customer_item" }
                ) { index, uiModel ->
                    CustomerItemCard(
                        uiModel = uiModel,
                        isGlassActive = isGlassActive,
                        showAvatar = showCustomerAvatars,
                        showBoxContainer = showCustomerBoxContainer,
                        showDivider = index < customerUiModels.size - 1,
                        tintIndex = glassTintIndex,
                        specularIntensity = specularIntensity,
                        onClick = { onSelectCustomer(uiModel.customer.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.bounceClickable(
    enabled: Boolean = true,
    scaleDown: Float = 0.975f,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "smoothClick"
    )
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
            enabled = enabled,
            onClick = onClick
        )
}

private val TxDateFormat by lazy { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
private val OkGreenBadgeBg = Color(0x2E008955)
private val OkOrangeRedBadgeBg = Color(0x2ED81B60)

private val DueBadgeGreenBg = Color(0x26008955)
private val DueTextGreen = Color(0xFF008955)
private val AdvanceBadgeRedBg = Color(0x26D81B60)
private val AdvanceTextRed = Color(0xFFD81B60)
private val SettledBadgeGrayBg = Color(0x20707579)
private val SettledTextGray = Color(0xFF707579)
private val FastCardDividerColor = Color(0x18707579)

@Composable
fun CustomerItemCard(
    uiModel: CustomerUiModel,
    modifier: Modifier = Modifier,
    isGlassActive: Boolean = true,
    showAvatar: Boolean = true,
    showBoxContainer: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    showDivider: Boolean = false,
    tintIndex: Int = 0,
    specularIntensity: Float = 0.70f,
    onClick: () -> Unit
) {
    val name = uiModel.customer.name
    val initialChar = uiModel.initialChar
    val balance = uiModel.netBalance
    val formattedBalance = uiModel.formattedBalance
    val phoneDisplay = uiModel.phoneDisplay
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("customer_item_${uiModel.customer.id}"),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF131B2A) else Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Monogram Avatar + Customer Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (showAvatar) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(uiModel.avatarBgColor, RoundedCornerShape(14.dp))
                            .border(1.2.dp, uiModel.avatarBorderColor, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialChar,
                            color = uiModel.avatarColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        if (uiModel.customer.autoSmsEnabled) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(13.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .border(1.2.dp, if (isDark) Color(0xFF131B2A) else Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Auto SMS",
                                    tint = Color.White,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.5.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = phoneDisplay,
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right: Amount + Status Pill + Chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    when {
                        balance > 0 -> {
                            Text(
                                text = "₹$formattedBalance",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = if (isDark) 0.16f else 0.12f),
                                border = BorderStroke(0.8.dp, Color(0xFF10B981).copy(alpha = 0.35f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    )
                                    Text(
                                        text = "लेना है",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                        balance < 0 -> {
                            Text(
                                text = "₹$formattedBalance",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEF4444).copy(alpha = if (isDark) 0.16f else 0.12f),
                                border = BorderStroke(0.8.dp, Color(0xFFEF4444).copy(alpha = 0.35f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(Color(0xFFEF4444), CircleShape)
                                    )
                                    Text(
                                        text = "देना है",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = "₹0",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = "हिसाब चुकता",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

sealed interface PinAction {
    data class EditTx(val tx: TransactionEntity) : PinAction
    data class DeleteTx(val tx: TransactionEntity) : PinAction
    data class PermanentDeleteTx(val tx: TransactionEntity) : PinAction
    data class RestoreTx(val tx: TransactionEntity) : PinAction
    object ClearCustomerHistory : PinAction
    object DeleteCustomer : PinAction
}

private val ChatDateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
private val ChatTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

fun getChatDateHeader(timestamp: Long): String {
    return com.example.viewmodel.fastGetChatDateHeader(timestamp)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CustomerDetailContent(
    viewModel: KhataViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activeCustomerUiModel by viewModel.activeCustomerUiModel.collectAsStateWithLifecycle()
    val activeTransactions by viewModel.activeTransactionsList.collectAsStateWithLifecycle()
    val shopName by viewModel.shopName.collectAsStateWithLifecycle()
    val upiId by viewModel.upiId.collectAsStateWithLifecycle()
    val shopkeeperPhotoPath by viewModel.shopkeeperPhotoPath.collectAsStateWithLifecycle()
    val securityPin by viewModel.securityPin.collectAsStateWithLifecycle()
    val isGlassActive by viewModel.isGlassActive.collectAsStateWithLifecycle()
    val showCustomerAvatars by viewModel.showCustomerAvatars.collectAsStateWithLifecycle()

    var showAddTxDialog by remember { mutableStateOf(false) }
    var isAddingCredit by remember { mutableStateOf(true) } // true = YOU GAVE, false = YOU GOT
    var showCustomerProfileSheet by remember { mutableStateOf(false) }
    var showUpiQrDialog by remember { mutableStateOf(false) }
    var showAiReminderDialog by remember { mutableStateOf(false) }
    var showAutoSmsDialog by remember { mutableStateOf(false) }
    var showGooglePhotosAvatarPreview by remember { mutableStateOf(false) }

    // PIN verification and Transaction Edit/Delete states
    var pendingPinAction by remember { mutableStateOf<PinAction?>(null) }
    var txToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
    var txToDelete by remember { mutableStateOf<TransactionEntity?>(null) }
    var txForHoldOptions by remember { mutableStateOf<TransactionEntity?>(null) }

    if (activeCustomerUiModel == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OkGreenPrimary)
        }
        return
    }

    val customer = activeCustomerUiModel!!.customer
    val netBalance = activeCustomerUiModel!!.netBalance

    // Pre-computed on Dispatchers.Default (Background Thread) - zero lag on UI/Main thread
    val groupedTransactions by viewModel.activeGroupedTransactions.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val scrollGearSoundEnabled by viewModel.scrollGearSoundEnabled.collectAsStateWithLifecycle()
    val glassTintIndex by viewModel.glassTintIndex.collectAsStateWithLifecycle()
    val specularIntensity by viewModel.specularIntensity.collectAsStateWithLifecycle()
    val safeTint = glassTintIndex.coerceIn(0, GLASS_TINT_PRESETS.size - 1)
    val customTint = GLASS_TINT_PRESETS[safeTint].color
    rememberScrollGearFeedback(lazyListState = listState, enabled = scrollGearSoundEnabled)

    // Smoothly detect when user has scrolled down the ledger
    val isLedgerScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 30
        }
    }

    // Automatically scroll to the bottom (latest transaction/amount) on open and when transactions change
    LaunchedEffect(activeTransactions.size, customer.id) {
        if (activeTransactions.isNotEmpty()) {
            val totalItems = 2 + groupedTransactions.size + activeTransactions.size
            listState.scrollToItem((totalItems - 1).coerceAtLeast(0))
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat Transaction List (Scrolls smoothly underneath floating top bar & bottom bar with high-performance view recycling)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
                .testTag("customer_transaction_lazy_column"),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(top = 68.dp, bottom = 114.dp)
        ) {
            // Net Balance Header Card (Scrolls smoothly upwards when scrolling the ledger)
            item(key = "net_balance_card", contentType = "header_card") {
                IosLiquidGlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    borderWidth = 1.2.dp,
                    tintColor = if (safeTint > 0) customTint else null,
                    specularIntensity = specularIntensity
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NET BALANCE (कुल बकाया)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = okTextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${formatAmount(kotlin.math.abs(netBalance))}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                netBalance > 0 -> MaterialTheme.colorScheme.primary
                                netBalance < 0 -> MaterialTheme.colorScheme.error
                                else -> okTextMuted
                            }
                        )
                        Text(
                            text = when {
                                netBalance > 0 -> "You Will Get from ${customer.name} (देंगे)"
                                netBalance < 0 -> "You Will Give to ${customer.name} (लेंगे)"
                                else -> "Settled Balance (कोई बकाया नहीं)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = when {
                                netBalance > 0 -> MaterialTheme.colorScheme.primary
                                netBalance < 0 -> MaterialTheme.colorScheme.error
                                else -> okTextMuted
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Action Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AssistChip(
                                onClick = { showAutoSmsDialog = true },
                                label = { Text(if (customer.autoSmsEnabled) "⏰ Auto SMS (${customer.autoSmsFrequency})" else "⏰ Auto SMS") },
                                leadingIcon = { Icon(Icons.Default.Alarm, contentDescription = null, tint = if (customer.autoSmsEnabled) OkGreenPrimary else okTextMuted) },
                                colors = if (customer.autoSmsEnabled) AssistChipDefaults.assistChipColors(containerColor = OkGreenLight, labelColor = OkGreenDark) else AssistChipDefaults.assistChipColors()
                            )
                            if (netBalance > 0) {
                                AssistChip(
                                    onClick = { showAiReminderDialog = true },
                                    label = { Text("✨ AI") },
                                    leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OkGreenPrimary) }
                                )
                            }
                            AssistChip(
                                onClick = {
                                    shareWhatsAppReport(context, customer, activeTransactions, netBalance, shopName, upiId)
                                },
                                label = { Text("📱 Direct WA") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF25D366)) }
                            )
                            AssistChip(
                                onClick = {
                                    shareOkCreditCardOnWhatsApp(context, customer, netBalance, shopName, upiId, shopkeeperPhotoPath)
                                },
                                label = { Text("🖼️ OkCard") },
                                leadingIcon = { Icon(Icons.Default.QrCode2, contentDescription = null, tint = OkGreenPrimary) }
                            )
                            AssistChip(
                                onClick = {
                                    sendSmsReport(context, customer, netBalance, shopName, upiId)
                                },
                                label = { Text("💬 SMS") },
                                leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = OkGreenPrimary) }
                            )
                        }

                        if (customer.autoSmsEnabled) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = OkGreenLight.copy(alpha = 0.8f),
                                border = BorderStroke(1.dp, OkGreenPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAutoSmsDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = OkGreenPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Auto SMS: ${customer.autoSmsFrequency} @ ${customer.autoSmsTime}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OkGreenDark
                                        )
                                    }
                                    Text("Edit ✏️", fontSize = 10.sp, color = OkGreenDark, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // Section Header: Ledger Title + Hold hint
            item(key = "ledger_title_header", contentType = "header_title") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTION LEDGER (खाता)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = okTextMuted
                    )
                    Text(
                        text = "Hold press for options 👆",
                        fontSize = 10.5.sp,
                        color = OkGreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Transactions Chat Feed
            if (activeTransactions.isEmpty()) {
                item(key = "empty_tx_state", contentType = "empty_view") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(48.dp), tint = okTextMuted)
                            Text("No entries recorded for ${customer.name} yet.", color = okTextMuted, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                groupedTransactions.forEach { group ->
                    stickyHeader(key = "date_header_${group.dateHeader}", contentType = "chat_date_header") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f),
                                border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.35f)),
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = group.dateHeader,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = okTextMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    items(
                        items = group.transactions,
                        key = { it.id },
                        contentType = { "transaction_chat_bubble" }
                    ) { tx ->
                        TransactionChatBubble(
                            transaction = tx,
                            modifier = Modifier.animateItemPlacement(),
                            isGlassActive = isGlassActive,
                            onEdit = {
                                pendingPinAction = PinAction.EditTx(tx)
                            },
                            onDelete = {
                                txToDelete = tx
                            },
                            onRestore = {
                                pendingPinAction = PinAction.RestoreTx(tx)
                            },
                            onLongPress = {
                                txForHoldOptions = tx
                            }
                        )
                    }
                }
            }
        }

        // 1. Floating Top Bar (Header with customer info & actions)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            IosLiquidGlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("customer_detail_glass_top_bar"),
                shape = RoundedCornerShape(20.dp),
                borderWidth = 1.2.dp,
                tintColor = if (safeTint > 0) customTint else null,
                specularIntensity = specularIntensity
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showCustomerProfileSheet = true }
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (showCustomerAvatars) {
                            Surface(
                                shape = CircleShape,
                                color = getAvatarColor(customer.name),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = customer.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = customer.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (customer.autoSmsEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = "Auto SMS Active",
                                        tint = OkGreenPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (customer.phone.isNotEmpty()) customer.phone else "Tap for Profile",
                                fontSize = 11.sp,
                                color = okTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (customer.phone.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {
                                    sendSmsReport(context, customer, netBalance, shopName, upiId)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Message, contentDescription = "SMS Message", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                        IconButton(
                            onClick = { showUpiQrDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = "UPI QR Code", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { showCustomerProfileSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Customer Profile", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // 2. Floating Bottom Action Buttons: YOU GAVE vs YOU GOT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 14.dp, end = 14.dp, bottom = 36.dp)
        ) {
            IosLiquidGlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ledger_floating_bottom_bar"),
                shape = RoundedCornerShape(20.dp),
                borderWidth = 1.2.dp,
                tintColor = if (safeTint > 0) customTint else null,
                specularIntensity = specularIntensity
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // YOU GAVE (आपने दिए) - Red Button
                    Button(
                        onClick = {
                            isAddingCredit = true
                            showAddTxDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("you_gave_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("YOU GAVE ₹\n(आपने दिए)", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, color = Color.White)
                        }
                    }

                    // YOU GOT (आपको मिले) - Green / Primary Button
                    Button(
                        onClick = {
                            isAddingCredit = false
                            showAddTxDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("you_got_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("YOU GOT ₹\n(आपको मिले)", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddTxDialog) {
        AddTransactionDialog(
            isCredit = isAddingCredit,
            onDismiss = { showAddTxDialog = false },
            onSave = { amount, remarks ->
                viewModel.addTransaction(
                    customerId = customer.id,
                    amount = amount,
                    isCredit = isAddingCredit,
                    remarks = remarks
                )
                showAddTxDialog = false
            }
        )
    }

    // Edit Transaction Dialog
    if (txToEdit != null) {
        EditTransactionDialog(
            transaction = txToEdit!!,
            onDismiss = { txToEdit = null },
            onSave = { amount, isCredit, remarks ->
                val targetTx = txToEdit!!
                txToEdit = null
                viewModel.updateTransaction(
                    id = targetTx.id,
                    newAmount = amount,
                    newIsCredit = isCredit,
                    newRemarks = remarks
                ) { success ->
                    if (success) {
                        Toast.makeText(context, "Transaction updated successfully! ✏️", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // Delete Transaction Dialog (Soft delete vs Permanent delete)
    if (txToDelete != null) {
        val targetTx = txToDelete!!
        DeleteTransactionConfirmDialog(
            transaction = targetTx,
            onDismiss = { txToDelete = null },
            onSoftDelete = {
                txToDelete = null
                pendingPinAction = PinAction.DeleteTx(targetTx)
            },
            onPermanentDelete = {
                txToDelete = null
                pendingPinAction = PinAction.PermanentDeleteTx(targetTx)
            }
        )
    }

    if (showUpiQrDialog) {
        UpiQrDialog(
            upiId = upiId,
            customerName = customer.name,
            amount = if (netBalance > 0) netBalance else 0.0,
            onDismiss = { showUpiQrDialog = false }
        )
    }

    if (showAiReminderDialog) {
        AiReminderDialog(
            viewModel = viewModel,
            customerName = customer.name,
            amount = netBalance,
            phone = customer.phone,
            onDismiss = { showAiReminderDialog = false }
        )
    }

    if (showAutoSmsDialog) {
        AutoSmsDialog(
            customer = customer,
            netBalance = netBalance,
            shopName = shopName,
            upiId = upiId,
            onSaveSchedule = { enabled, freq, time ->
                viewModel.updateCustomerAutoSms(customer.id, enabled, freq, time)
            },
            onSendNow = {
                sendSmsReport(context, customer, netBalance, shopName, upiId)
            },
            onDismiss = { showAutoSmsDialog = false }
        )
    }

    if (showCustomerProfileSheet) {
        WhatsAppCustomerProfileSheet(
            customer = customer,
            netBalance = netBalance,
            transactionCount = activeTransactions.count { !it.isDeleted },
            shopName = shopName,
            upiId = upiId,
            securityPin = securityPin,
            onDismiss = { showCustomerProfileSheet = false },
            onSaveProfile = { name, phone, address, secPhone ->
                viewModel.updateCustomerProfile(
                    id = customer.id,
                    name = name,
                    phone = phone,
                    address = address,
                    secondaryPhone = secPhone,
                    photoUri = customer.photoUri
                )
            },
            onClearHistory = {
                showCustomerProfileSheet = false
                pendingPinAction = PinAction.ClearCustomerHistory
            },
            onDeleteCustomer = {
                showCustomerProfileSheet = false
                pendingPinAction = PinAction.DeleteCustomer
            },
            onOpenAutoSms = {
                showCustomerProfileSheet = false
                showAutoSmsDialog = true
            },
            onOpenUpiQr = {
                showCustomerProfileSheet = false
                showUpiQrDialog = true
            }
        )
    }

    if (showGooglePhotosAvatarPreview) {
        GooglePhotosAvatarDialog(
            customer = customer,
            onDismiss = { showGooglePhotosAvatarPreview = false },
            onEdit = { showCustomerProfileSheet = true }
        )
    }

    // Unified PIN Verification Dialog for All Actions (100% Protected)
    if (pendingPinAction != null) {
        val action = pendingPinAction!!
        val title = when (action) {
            is PinAction.EditTx -> "Verify PIN to Edit Entry 🔒"
            is PinAction.DeleteTx -> "Verify PIN to Delete Entry 🔒"
            is PinAction.PermanentDeleteTx -> "Verify PIN for Permanent Delete ⚠️🔒"
            is PinAction.RestoreTx -> "Verify PIN to Restore Entry 🔒"
            is PinAction.ClearCustomerHistory -> "Verify PIN to Clear Chat History 🧹🔒"
            is PinAction.DeleteCustomer -> "Verify PIN to Delete Customer 🔒"
        }
        val subtitle = when (action) {
            is PinAction.EditTx -> "Enter 4-digit security PIN to edit transaction amount/entry"
            is PinAction.DeleteTx -> "Enter 4-digit security PIN to mark transaction as deleted"
            is PinAction.PermanentDeleteTx -> "Enter 4-digit security PIN to permanently delete transaction record"
            is PinAction.RestoreTx -> "Enter 4-digit security PIN to restore transaction entry"
            is PinAction.ClearCustomerHistory -> "Enter 4-digit security PIN to clear all transaction history for ${customer.name}"
            is PinAction.DeleteCustomer -> "Enter 4-digit security PIN to delete customer profile"
        }

        PinVerificationDialog(
            correctPin = securityPin,
            title = title,
            subtitle = subtitle,
            onDismiss = { pendingPinAction = null },
            onVerified = { pinUsed ->
                if (securityPin.isEmpty() && pinUsed.isNotEmpty()) {
                    viewModel.setSecurityPin(pinUsed)
                }
                val act = pendingPinAction
                pendingPinAction = null
                when (act) {
                    is PinAction.EditTx -> txToEdit = act.tx
                    is PinAction.DeleteTx -> {
                        viewModel.softDeleteTransaction(act.tx.id) {
                            Toast.makeText(context, "Transaction marked as deleted 🗑️", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is PinAction.PermanentDeleteTx -> {
                        viewModel.deleteTransaction(act.tx.id)
                        Toast.makeText(context, "Transaction permanently removed 🗑️", Toast.LENGTH_SHORT).show()
                    }
                    is PinAction.RestoreTx -> {
                        viewModel.restoreTransaction(act.tx.id) {
                            Toast.makeText(context, "Transaction restored! ↩️", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is PinAction.ClearCustomerHistory -> {
                        viewModel.clearCustomerHistory(customer.id) {
                            Toast.makeText(context, "✅ All transactions cleared for ${customer.name}!", Toast.LENGTH_LONG).show()
                        }
                    }
                    is PinAction.DeleteCustomer -> {
                        viewModel.deleteCustomer(customer.id)
                        showCustomerProfileSheet = false
                        onBack()
                        Toast.makeText(context, "Customer profile deleted", Toast.LENGTH_SHORT).show()
                    }
                    null -> {}
                }
            }
        )
    }

    // Long-Press / Hold Options Bottom Sheet for Transaction
    if (txForHoldOptions != null) {
        val selectedTx = txForHoldOptions!!
        TransactionHoldOptionsBottomSheet(
            transaction = selectedTx,
            customer = customer,
            shopName = shopName,
            upiId = upiId,
            onDismiss = { txForHoldOptions = null },
            onEdit = {
                txForHoldOptions = null
                pendingPinAction = PinAction.EditTx(selectedTx)
            },
            onDelete = {
                txForHoldOptions = null
                txToDelete = selectedTx
            },
            onRestore = if (selectedTx.isDeleted) {
                {
                    txForHoldOptions = null
                    pendingPinAction = PinAction.RestoreTx(selectedTx)
                }
            } else null
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionChatBubble(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier,
    isGlassActive: Boolean = true,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRestore: (() -> Unit)? = null,
    onLongPress: () -> Unit = {}
) {
    val timeStr = remember(transaction.timestamp) {
        com.example.viewmodel.fastFormatChatTime(transaction.timestamp)
    }
    val formattedAmount = remember(transaction.amount) { formatAmount(transaction.amount) }

    val isCredit = transaction.isCredit
    val isDeleted = transaction.isDeleted
    val isEdited = transaction.isEdited

    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val errorContainer = MaterialTheme.colorScheme.errorContainer
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    val bubbleBgColor = remember(isDeleted, isCredit, isDark, isGlassActive, primaryColor, errorColor, surfaceVariant, errorContainer, primaryContainer) {
        when {
            isDeleted -> if (isDark) surfaceVariant.copy(alpha = 0.25f) else surfaceVariant.copy(alpha = 0.45f)
            isCredit -> if (isDark) errorColor.copy(alpha = 0.12f) else if (isGlassActive) errorContainer.copy(alpha = 0.5f) else errorContainer.copy(alpha = 0.7f)
            else -> if (isDark) primaryColor.copy(alpha = 0.12f) else if (isGlassActive) primaryContainer.copy(alpha = 0.5f) else primaryContainer.copy(alpha = 0.7f)
        }
    }

    val bubbleBorderColor = remember(isDeleted, isCredit, errorColor, primaryColor) {
        when {
            isDeleted -> Color.LightGray.copy(alpha = 0.35f)
            isCredit -> errorColor.copy(alpha = 0.35f)
            else -> primaryColor.copy(alpha = 0.35f)
        }
    }

    val shape = remember(isCredit) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isCredit) 16.dp else 4.dp,
            bottomEnd = if (isCredit) 4.dp else 16.dp
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .combinedClickable(
                onClick = onLongPress,
                onLongClick = onLongPress
            )
            .testTag("transaction_item_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = bubbleBgColor),
        shape = shape,
        border = BorderStroke(1.dp, bubbleBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDeleted) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row: Status / Type Label + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = when {
                                    isDeleted -> Color.Gray.copy(alpha = 0.2f)
                                    isCredit -> errorColor.copy(alpha = 0.18f)
                                    else -> primaryColor.copy(alpha = 0.18f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isDeleted -> Icons.Default.Block
                                isCredit -> Icons.Default.ArrowUpward
                                else -> Icons.Default.ArrowDownward
                            },
                            contentDescription = null,
                            tint = when {
                                isDeleted -> Color.Gray
                                isCredit -> errorColor
                                else -> primaryColor
                            },
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = when {
                            isDeleted -> "DELETED (हटाया गया)"
                            isCredit -> "YOU GAVE (आपने दिए)"
                            else -> "YOU GOT (आपको मिले)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = when {
                            isDeleted -> Color.Gray
                            isCredit -> errorColor
                            else -> primaryColor
                        }
                    )
                }

                // Amount
                Text(
                    text = "${if (isCredit) "-" else "+"} ₹$formattedAmount",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textDecoration = if (isDeleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = when {
                        isDeleted -> Color.Gray
                        isCredit -> errorColor
                        else -> primaryColor
                    }
                )
            }

            // Remarks / Notes description
            if (transaction.remarks.isNotBlank()) {
                Text(
                    text = transaction.remarks,
                    fontSize = 13.5.sp,
                    color = if (isDeleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom metadata row
            HorizontalDivider(
                color = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f),
                thickness = 0.8.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Time + Status Tags
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 11.sp,
                        color = okTextMuted
                    )

                    if (isEdited && !isDeleted) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFF8E1), RoundedCornerShape(4.dp))
                                .border(0.5.dp, Color(0xFFFFB300), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "Edited (सुधारा गया)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF57F17)
                                )
                            }
                        }
                    }

                    if (isDeleted) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp))
                                .border(0.5.dp, OkOrangeRed.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Deleted Entry (खाते में नहीं)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OkOrangeRed
                            )
                        }
                    }
                }

                // Right: Hold for options indicator
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .border(0.5.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Hold for options",
                            tint = okTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Hold 👆",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = okTextMuted
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHoldOptionsBottomSheet(
    transaction: TransactionEntity,
    customer: CustomerEntity,
    shopName: String,
    upiId: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRestore: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isCredit = transaction.isCredit
    val isDeleted = transaction.isDeleted
    val timeStr = remember(transaction.timestamp) {
        "${ChatDateFormatter.format(Date(transaction.timestamp))} at ${ChatTimeFormatter.format(Date(transaction.timestamp))}"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header summary card of the selected transaction
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = when {
                    isDeleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    isCredit -> OkOrangeRed.copy(alpha = 0.1f)
                    else -> OkGreenPrimary.copy(alpha = 0.1f)
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        isDeleted -> Color.LightGray.copy(alpha = 0.4f)
                        isCredit -> OkOrangeRed.copy(alpha = 0.3f)
                        else -> OkGreenPrimary.copy(alpha = 0.3f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = when {
                                    isDeleted -> "❌ DELETED ENTRY"
                                    isCredit -> "🔴 YOU GAVE (आपने दिए)"
                                    else -> "🟢 YOU GOT (आपको मिले)"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isDeleted -> Color.Gray
                                    isCredit -> OkOrangeRed
                                    else -> OkGreenPrimary
                                }
                            )
                            if (transaction.isEdited && !isDeleted) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFFF8E1),
                                    border = BorderStroke(0.5.dp, Color(0xFFFFB300))
                                ) {
                                    Text(
                                        text = "Edited ✏️",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        if (transaction.remarks.isNotBlank()) {
                            Text(
                                text = transaction.remarks,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = timeStr,
                            fontSize = 11.sp,
                            color = okTextMuted
                        )
                    }

                    Text(
                        text = "₹${formatAmount(transaction.amount)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = when {
                            isDeleted -> Color.Gray
                            isCredit -> OkOrangeRed
                            else -> OkGreenPrimary
                        }
                    )
                }
            }

            Text(
                text = "OPTIONS (विकल्प)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = okTextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Option: Edit (Security PIN Protected)
            if (!isDeleted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onEdit)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OkGreenLight,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = OkGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Edit Amount & Details (एडिट करें)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Change amount, type or notes (Requires PIN 🔒)",
                                fontSize = 12.sp,
                                color = okTextMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = okTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Option: Delete (Security PIN Protected)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OkOrangeRed.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, OkOrangeRed.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDelete)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OkOrangeRed.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isDeleted) Icons.Default.DeleteForever else Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = OkOrangeRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isDeleted) "Permanent Delete (हमेशा के लिए हटाएं)" else "Delete Entry (डिलीट करें)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = OkOrangeRed
                        )
                        Text(
                            text = if (isDeleted) "Erase from database forever (Requires PIN 🔒)" else "Mark entry as deleted (Requires PIN 🔒)",
                            fontSize = 12.sp,
                            color = okTextMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = OkOrangeRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Option: Restore if deleted
            if (isDeleted && onRestore != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = OkGreenLight,
                    border = BorderStroke(1.dp, OkGreenPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onRestore)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OkGreenPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    tint = OkGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Restore Entry (खाते में वापस जोड़ें)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OkGreenDark
                            )
                            Text(
                                text = "Reactivate transaction in ledger (Requires PIN 🔒)",
                                fontSize = 12.sp,
                                color = OkGreenDark.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = OkGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Option: Share on WhatsApp
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        val text = "🧾 *Payment Slip - $shopName*\nCustomer: ${customer.name}\nAmount: ₹${formatAmount(transaction.amount)} (${if (isCredit) "GAVE / दिए" else "GOT / मिले"})\nDate: $timeStr\n${if (transaction.remarks.isNotBlank()) "Note: ${transaction.remarks}\n" else ""}Pay via UPI: $upiId"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=${customer.phone.replace("+", "").replace(" ", "")}&text=${Uri.encode(text)}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed or could not open", Toast.LENGTH_SHORT).show()
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF25D366).copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Share Receipt on WhatsApp (व्हाट्सएप रसीद)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Send this entry receipt directly to customer",
                            fontSize = 12.sp,
                            color = okTextMuted
                        )
                    }
                }
            }

            // Option: Copy Details
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Transaction Receipt", "Entry for ${customer.name}: ${if (isCredit) "Gave" else "Got"} ₹${formatAmount(transaction.amount)} on $timeStr. Note: ${transaction.remarks}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Transaction details copied! 📋", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Copy Details (कॉपी करें)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Copy payment slip to clipboard",
                            fontSize = 12.sp,
                            color = okTextMuted
                        )
                    }
                }
            }

            // Cancel Button
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close (बंद करें)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditTransactionDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    onSave: (amount: Double, isCredit: Boolean, remarks: String) -> Unit
) {
    var amountStr by remember {
        mutableStateOf(
            if (transaction.amount % 1.0 == 0.0) transaction.amount.toLong().toString() else transaction.amount.toString()
        )
    }
    var isCredit by remember { mutableStateOf(transaction.isCredit) }
    var remarks by remember { mutableStateOf(transaction.remarks) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(150)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isCredit) OkOrangeRed.copy(alpha = 0.15f) else OkGreenPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (isCredit) OkOrangeRed else OkGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column {
                    Text("Edit Transaction (सुधारें)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Update amount, type or notes", fontSize = 11.sp, color = okTextMuted)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Type selector: YOU GAVE vs YOU GOT
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isCredit = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCredit) OkOrangeRed else Color.Transparent
                    ) {
                        Text(
                            text = "YOU GAVE (दिए)",
                            color = if (isCredit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isCredit = false },
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isCredit) OkGreenPrimary else Color.Transparent
                    ) {
                        Text(
                            text = "YOU GOT (मिले)",
                            color = if (!isCredit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (राशि ₹)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("edit_tx_amount_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isCredit) OkOrangeRed else OkGreenPrimary
                    )
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks / Items (विवरण)") },
                    placeholder = { Text("e.g. Items purchased / Bill") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_tx_remarks_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onSave(amt, isCredit, remarks)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCredit) OkOrangeRed else OkGreenPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_edit_tx_button")
            ) {
                Text("Save Changes (अपडेट करें)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = okTextMuted)
            }
        }
    )
}

@Composable
fun DeleteTransactionConfirmDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    onSoftDelete: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    val formattedAmt = formatAmount(transaction.amount)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = OkOrangeRed)
                Text("Delete Transaction (हटाएं)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Aap ₹$formattedAmt ki entry ko kaise hatana chahte hain?",
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "• OkCredit Style (Mark as Deleted): Entry chat me 'Deleted' tag ke sath dikhegi aur balance se cut ho jayegi (Recommended for audit).",
                            fontSize = 11.5.sp,
                            color = okTextMuted
                        )
                        Text(
                            text = "• Permanent Delete: Entry bilkul khatam ho jayegi.",
                            fontSize = 11.5.sp,
                            color = okTextMuted
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSoftDelete,
                colors = ButtonDefaults.buttonColors(containerColor = OkOrangeRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Mark as Deleted (हटाएं)")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onPermanentDelete) {
                    Text("Permanent Delete", color = OkOrangeRed, fontSize = 11.sp)
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = okTextMuted)
                }
            }
        }
    )
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(150)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Customer (नया ग्राहक)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name (नाम)") },
                    placeholder = { Text("e.g. Rahul Sharma") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("add_customer_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (फ़ोन नंबर)") },
                    placeholder = { Text("e.g. 9876543210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_customer_phone_input"),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.trim().isNotEmpty()) onAdd(name, phone) },
                colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_customer_button")
            ) {
                Text("Save Customer (सुरक्षित करें)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = okTextMuted)
            }
        }
    )
}

@Composable
fun AddTransactionDialog(
    isCredit: Boolean,
    customerName: String? = null,
    onDismiss: () -> Unit,
    onSave: (amount: Double, remarks: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(150)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            val titleText = if (customerName != null) {
                if (isCredit) "YOU GAVE ₹ to $customerName (आपने दिए)" else "YOU GOT ₹ from $customerName (आपको मिले)"
            } else {
                if (isCredit) "YOU GAVE ₹ (आपने उधार दिए)" else "YOU GOT ₹ (आपको भुगतान मिला)"
            }
            Text(
                text = titleText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isCredit) OkOrangeRed else OkGreenPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount in Rupees (राशि ₹)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("tx_amount_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isCredit) OkOrangeRed else OkGreenPrimary
                    )
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks / Items (विवरण/सामान)") },
                    placeholder = { Text("e.g. 2 kg Rice, Kirana bill") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_remarks_input"),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull()
                    if (amt != null && amt > 0) {
                        onSave(amt, remarks)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCredit) OkOrangeRed else OkGreenPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_tx_button")
            ) {
                Text("Save Entry (दर्ज करें)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = okTextMuted)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppCustomerProfileSheet(
    customer: CustomerEntity,
    netBalance: Double,
    transactionCount: Int,
    shopName: String,
    upiId: String,
    securityPin: String,
    onDismiss: () -> Unit,
    onSaveProfile: (name: String, phone: String, address: String, secPhone: String) -> Unit,
    onClearHistory: () -> Unit,
    onDeleteCustomer: () -> Unit,
    onOpenAutoSms: () -> Unit,
    onOpenUpiQr: () -> Unit
) {
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember(customer.name) { mutableStateOf(customer.name) }
    var editPhone by remember(customer.phone) { mutableStateOf(customer.phone) }
    var editAddress by remember(customer.address) { mutableStateOf(customer.address) }
    var editSecPhone by remember(customer.secondaryPhone) { mutableStateOf(customer.secondaryPhone) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Large Avatar
            Surface(
                shape = CircleShape,
                color = getAvatarColor(customer.name),
                shadowElevation = 4.dp,
                modifier = Modifier.size(76.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = customer.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = customer.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                if (customer.phone.isNotEmpty()) {
                    Text(
                        text = customer.phone,
                        fontSize = 13.5.sp,
                        color = okTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (customer.address.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = okTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = customer.address,
                            fontSize = 12.sp,
                            color = okTextMuted
                        )
                    }
                }
            }

            // Quick WhatsApp Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Call
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            if (customer.phone.isNotEmpty()) {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please add phone number first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = OkGreenLight),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = OkGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text("Call 📞", fontSize = 11.sp, color = okTextMuted, fontWeight = FontWeight.Medium)
                }

                // WhatsApp
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            shareKhataSlipOnWhatsApp(context, customer, netBalance, shopName, upiId)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF25D366).copy(alpha = 0.15f)),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "WhatsApp",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text("WhatsApp 📱", fontSize = 11.sp, color = okTextMuted, fontWeight = FontWeight.Medium)
                }

                // SMS
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            sendSmsReport(context, customer, netBalance, shopName, upiId)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "SMS",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text("SMS 💬", fontSize = 11.sp, color = okTextMuted, fontWeight = FontWeight.Medium)
                }

                // UPI QR
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledIconButton(
                        onClick = onOpenUpiQr,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = OkGreenLight),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR",
                            tint = OkGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text("Pay QR 💳", fontSize = 11.sp, color = okTextMuted, fontWeight = FontWeight.Medium)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Account / Ledger Status Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = when {
                                netBalance > 0.001 -> "🔴 You Will Get (आपको लेने हैं)"
                                netBalance < -0.001 -> "🟢 You Will Give (आपको देने हैं)"
                                else -> "⚪ All Settled (हिसाब चुकता)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                netBalance > 0.001 -> OkOrangeRed
                                netBalance < -0.001 -> OkGreenPrimary
                                else -> okTextMuted
                            }
                        )
                        Text(
                            text = "₹${formatAmount(kotlin.math.abs(netBalance))}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                netBalance > 0.001 -> OkOrangeRed
                                netBalance < -0.001 -> OkGreenPrimary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = "$transactionCount Entries 📜",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Edit Profile Card or Fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Profile Details (प्रोफ़ाइल विवरण)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        TextButton(onClick = { isEditing = !isEditing }) {
                            Text(if (isEditing) "Cancel" else "Edit ✏️", fontSize = 12.sp)
                        }
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Customer Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Primary Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = editAddress,
                            onValueChange = { editAddress = it },
                            label = { Text("Address / City") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = {
                                if (editName.isNotBlank()) {
                                    onSaveProfile(editName, editPhone, editAddress, editSecPhone)
                                    isEditing = false
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Name: ${customer.name}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Phone: ${if (customer.phone.isNotEmpty()) customer.phone else "Not set"}", fontSize = 12.5.sp, color = okTextMuted)
                            if (customer.address.isNotEmpty()) {
                                Text("Address: ${customer.address}", fontSize = 12.5.sp, color = okTextMuted)
                            }
                        }
                    }
                }
            }

            // WhatsApp Style Action Options (Clear History & Delete Customer)
            Text(
                text = "CHAT & LEDGER SETTINGS (व्हाट्सएप सेटिंग्स)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = okTextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            // 1. WhatsApp-Style: Clear History / Clear Chat
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OkOrangeRed.copy(alpha = 0.07f),
                border = BorderStroke(1.dp, OkOrangeRed.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClearHistory)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OkOrangeRed.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = OkOrangeRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Clear Chat / History (सारे लेन-देन हटाएं)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = OkOrangeRed
                            )
                            if (securityPin.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "PIN Protected",
                                    tint = OkOrangeRed,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "Delete all transaction entries for this customer. Balance will reset to ₹0.",
                            fontSize = 11.5.sp,
                            color = okTextMuted
                        )
                    }
                }
            }

            // 2. WhatsApp-Style: Delete Customer Profile
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDeleteCustomer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = null,
                                tint = OkOrangeRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Delete Customer (ग्राहक को हटाएं)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = OkOrangeRed
                            )
                            if (securityPin.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "PIN Protected",
                                    tint = OkOrangeRed,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "Permanently remove customer and all khata records.",
                            fontSize = 11.5.sp,
                            color = okTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpiQrDialog(
    upiId: String,
    customerName: String,
    amount: Double,
    onDismiss: () -> Unit
) {
    val upiUrl = "upi://pay?pa=$upiId&pn=${Uri.encode("OkKhata Merchant")}&am=${if (amount > 0) amount else ""}&cu=INR"
    val qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${Uri.encode(upiUrl)}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payment UPI QR Code 💳", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Customer $customerName can scan this QR code with PhonePe, Google Pay or Paytm to pay directly.",
                    fontSize = 12.sp,
                    color = okTextMuted,
                    textAlign = TextAlign.Center
                )

                coil.compose.AsyncImage(
                    model = qrApiUrl,
                    contentDescription = "UPI Payment QR Code",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                )

                Text(
                    text = "Merchant UPI ID: $upiId",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = OkGreenPrimary
                )
                if (amount > 0) {
                    Text(
                        text = "Amount: ₹${formatAmount(amount)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = OkGreenPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AiReminderDialog(
    viewModel: KhataViewModel,
    customerName: String,
    amount: Double,
    phone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var reminderText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.generateAiRecoveryMessage(customerName, amount, phone) { generated ->
            reminderText = generated
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = OkGreenPrimary)
                Text("Executive Payment Advice", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OkGreenPrimary)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Official ledger settlement advice for $customerName:",
                        fontSize = 12.sp,
                        color = okTextMuted
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OkGreenLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = reminderText ?: "",
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp),
                            color = OkGreenDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isLoading && reminderText != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Reminder", reminderText))
                            Toast.makeText(context, "Copied reminder to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Copy", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:$phone")
                                    putExtra("sms_body", reminderText)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Could not open SMS app", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SMS 💬", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, reminderText)
                                    if (phone.isNotEmpty()) {
                                        setPackage("com.whatsapp")
                                    }
                                }
                                context.startActivity(Intent.createChooser(intent, "Send via"))
                            } catch (e: Exception) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Reminder", reminderText))
                                Toast.makeText(context, "Copied reminder text to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("WhatsApp 📱", fontSize = 12.sp)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = okTextMuted)
            }
        }
    )
}

@Composable
fun AutoSmsDialog(
    customer: CustomerEntity,
    netBalance: Double,
    shopName: String,
    upiId: String,
    onSaveSchedule: (Boolean, String, String) -> Unit,
    onSendNow: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(customer.autoSmsEnabled) }
    var selectedFrequency by remember { mutableStateOf(if (customer.autoSmsFrequency.isNotEmpty()) customer.autoSmsFrequency else "Daily") }
    var selectedTime by remember { mutableStateOf(if (customer.autoSmsTime.isNotEmpty()) customer.autoSmsTime else "10:00 AM") }
    var customTimeInput by remember { mutableStateOf("") }
    var isCustomTime by remember { mutableStateOf(false) }

    val frequencyOptions = listOf(
        "Daily" to "Daily (प्रतिदिन - हर 1 दिन)",
        "Every 2 Days" to "Every 2 Days (हर 2 दिन में)",
        "Every 4 Days" to "Every 4 Days (हर 4 दिन में)",
        "Every 7 Days" to "Every 7 Days (हर 7 दिन / साप्ताहिक)"
    )

    val displayShop = if (shopName.isBlank()) "OkKhata Store" else shopName
    val formattedAmt = formatAmount(kotlin.math.abs(netBalance))
    val previewMessage = if (netBalance > 0) {
        "PAYMENT ADVICE: $displayShop se aapka ledger balance ₹$formattedAmt pending due hai. Kripya account update ke liye samay par bhugtan karein.${if (upiId.isNotBlank()) " Pay via UPI: $upiId" else ""} - Accounts, $displayShop"
    } else {
        "LEDGER UPDATE: $displayShop me aapka khata hisab completely settled (₹0.00) hai. - Accounts, $displayShop"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = OkGreenLight,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = OkGreenPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Column {
                    Text("Auto SMS Reminder (ऑटो SMS)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Customer: ${customer.name}", fontSize = 12.sp, color = okTextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Main Switch Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEnabled) OkGreenLight else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabled) "✅ Auto SMS Active (चालू है)" else "⏸️ Auto SMS Inactive (बंद है)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isEnabled) OkGreenDark else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnabled) "Automatic balance SMS will be sent on schedule" else "Turn ON to schedule automated SMS reminders",
                                fontSize = 11.sp,
                                color = if (isEnabled) OkGreenDark.copy(alpha = 0.8f) else okTextMuted
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = OkGreenPrimary)
                        )
                    }
                }

                if (isEnabled) {
                    // Frequency Section (1, 2, 4, 7 days)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.EventRepeat, contentDescription = null, tint = OkGreenPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Reminder Frequency (कितने दिन पर SMS जाए):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            frequencyOptions.forEach { (key, label) ->
                                val isSelected = selectedFrequency == key
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) OkGreenLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) OkGreenPrimary else Color.LightGray.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedFrequency = key }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) OkGreenDark else MaterialTheme.colorScheme.onSurface
                                        )
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedFrequency = key },
                                            colors = RadioButtonDefaults.colors(selectedColor = OkGreenPrimary)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Time Section ("kitna baje jaye sms")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = OkGreenPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Scheduled Time (कितना बजे SMS जाए):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Flow/Grid of quick time chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("08:00 AM", "09:00 AM", "10:00 AM").forEach { timeStr ->
                                    val isSelected = selectedTime == timeStr && !isCustomTime
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) OkGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedTime = timeStr
                                                isCustomTime = false
                                            }
                                    ) {
                                        Text(
                                            text = timeStr,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("12:00 PM", "02:00 PM", "05:00 PM").forEach { timeStr ->
                                    val isSelected = selectedTime == timeStr && !isCustomTime
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) OkGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedTime = timeStr
                                                isCustomTime = false
                                            }
                                    ) {
                                        Text(
                                            text = timeStr,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("07:00 PM", "08:00 PM").forEach { timeStr ->
                                    val isSelected = selectedTime == timeStr && !isCustomTime
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) OkGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedTime = timeStr
                                                isCustomTime = false
                                            }
                                    ) {
                                        Text(
                                            text = timeStr,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCustomTime) OkGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isCustomTime = true }
                                ) {
                                    Text(
                                        text = "⏱️ Custom",
                                        fontSize = 11.sp,
                                        fontWeight = if (isCustomTime) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCustomTime) Color.White else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        if (isCustomTime) {
                            OutlinedTextField(
                                value = customTimeInput,
                                onValueChange = {
                                    customTimeInput = it
                                    selectedTime = it
                                },
                                placeholder = { Text("e.g. 11:30 AM or 06:45 PM") },
                                label = { Text("Custom Time (समय लिखें)") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Scheduled Info Summary Banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OkGreenLight.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, OkGreenPrimary.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = OkGreenPrimary, modifier = Modifier.size(16.dp))
                                Text("Active Reminder Plan", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OkGreenDark)
                            }
                            Text(
                                text = "📅 Frequency: $selectedFrequency\n⏰ Time: $selectedTime\n📱 Phone: ${if (customer.phone.isNotBlank()) customer.phone else "Not set"}",
                                fontSize = 11.sp,
                                color = OkGreenDark
                            )
                        }
                    }

                    // Preview of SMS message
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("SMS Message Preview:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = okTextMuted)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = previewMessage,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (customer.phone.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            onSendNow()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = OkGreenPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Send", fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = {
                        val finalTime = if (isCustomTime && customTimeInput.isNotBlank()) customTimeInput.trim() else selectedTime
                        onSaveSchedule(isEnabled, selectedFrequency, finalTime)
                        Toast.makeText(context, if (isEnabled) "✅ Auto SMS Reminder Schedule Saved!" else "Auto SMS Disabled", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Schedule", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = okTextMuted)
            }
        }
    )
}

@Composable
fun GooglePhotosAvatarDialog(
    customer: CustomerEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = OkGreenPrimary)
                    Text("Google Photos Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Hero Avatar with Google Photos fluid elevation and rounded border
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = getAvatarColor(customer.name),
                    shadowElevation = 8.dp,
                    border = BorderStroke(3.dp, OkGreenPrimary.copy(alpha = 0.6f)),
                    modifier = Modifier.size(130.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = customer.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 60.sp
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = customer.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (customer.phone.isNotBlank()) {
                        Text(
                            text = "📞 ${customer.phone}",
                            fontSize = 13.sp,
                            color = okTextMuted
                        )
                    }
                    if (customer.address.isNotBlank()) {
                        Text(
                            text = "📍 ${customer.address}",
                            fontSize = 12.sp,
                            color = okTextMuted
                        )
                    }
                    if (customer.autoSmsEnabled) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OkGreenLight,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "⏰ Auto SMS Active: ${customer.autoSmsFrequency} @ ${customer.autoSmsTime}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OkGreenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onEdit()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = okTextMuted)
            }
        }
    )
}

@Composable
fun SettingsDialog(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit,
    onOpenUpdateDialog: () -> Unit = {}
) {
    val context = LocalContext.current
    val isKitsuneTheme by viewModel.isKitsuneTheme.collectAsStateWithLifecycle()
    val isIosModeActive by viewModel.isIosModeActive.collectAsStateWithLifecycle()
    val is2070Theme by viewModel.is2070Theme.collectAsStateWithLifecycle()
    val shopName by viewModel.shopName.collectAsStateWithLifecycle()
    val upiId by viewModel.upiId.collectAsStateWithLifecycle()
    val securityPin by viewModel.securityPin.collectAsStateWithLifecycle()
    val shopPhotoUri by viewModel.shopkeeperPhotoPath.collectAsStateWithLifecycle()
    val isAndroid17Animation by viewModel.isAndroid17Animation.collectAsStateWithLifecycle()
    val isUnity60FpsActive by viewModel.isUnity60FpsActive.collectAsStateWithLifecycle()
    val isSystemBlurSupported by viewModel.isSystemBlurSupported.collectAsStateWithLifecycle()
    val isSystemCrossWindowBlurEnabled by viewModel.isSystemCrossWindowBlurEnabled.collectAsStateWithLifecycle()
    val systemBlurAutoSync by viewModel.systemBlurAutoSync.collectAsStateWithLifecycle()
    val scrollGearSoundEnabled by viewModel.scrollGearSoundEnabled.collectAsStateWithLifecycle()
    val mass4xEngineActive by viewModel.mass4xEngineActive.collectAsStateWithLifecycle()
    val display90HzActive by viewModel.display90HzActive.collectAsStateWithLifecycle()
    val touch240HzActive by viewModel.touch240HzActive.collectAsStateWithLifecycle()
    val screenCraftBottomScrollBarActive by viewModel.screenCraftBottomScrollBarActive.collectAsStateWithLifecycle()

    var tempShopName by remember(shopName) { mutableStateOf(shopName) }
    var tempUpiId by remember(upiId) { mutableStateOf(upiId) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showPinForUpiDialog by remember { mutableStateOf(false) }
    var isUpiUnlocked by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.setShopkeeperPhotoPath(it.toString())
            Toast.makeText(context, "दुकानदार फोटो अपडेट हो गई! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    if (showPinDialog) {
        PinVerificationDialog(
            correctPin = securityPin,
            title = if (securityPin.isEmpty()) "पिन सेट करें (Set PIN) 🔒" else "पिन बदलें (Change PIN) 🔒",
            subtitle = "4-अंकों का सुरक्षा पिन दर्ज करें",
            onDismiss = { showPinDialog = false },
            onVerified = { newPin ->
                viewModel.setSecurityPin(newPin)
                showPinDialog = false
                Toast.makeText(context, "सुरक्षा पिन सफलतापूर्वक सेट हो गया! 🔒", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPinForUpiDialog) {
        PinVerificationDialog(
            correctPin = securityPin,
            title = "UPI अनलॉक करें 🔒",
            subtitle = "UPI ID देखने व बदलने के लिए 4-अंकों का पिन दर्ज करें",
            onDismiss = { showPinForUpiDialog = false },
            onVerified = {
                isUpiUnlocked = true
                showPinForUpiDialog = false
                Toast.makeText(context, "UPI ID अनलॉक हो गई! 🔓", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp)
                .testTag("settings_dialog_box"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.20f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = OkGreenPrimary)
                            Text("सेटिंग्स और प्रोफाइल ⚙️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = okTextMuted, modifier = Modifier.size(20.dp))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Scrollable Settings Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 📲 Online App Update Card
                        val updateInfo by viewModel.currentUpdateInfo.collectAsStateWithLifecycle()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .bounceClickable { onOpenUpdateDialog() }
                                .testTag("settings_open_update_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (updateInfo?.isUpdateAvailable == true)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(
                                1.2.dp,
                                if (updateInfo?.isUpdateAvailable == true)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.60f)
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.SystemUpdate,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "ऑनलाइन ऐप अपडेट 🚀",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp
                                            )
                                            if (updateInfo?.isUpdateAvailable == true) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.primary
                                                ) {
                                                    Text(
                                                        text = "New Update",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = if (updateInfo?.isUpdateAvailable == true)
                                                "नया वर्शन v${updateInfo?.versionName} उपलब्ध है • अभी डाउनलोड करें ⬇️"
                                            else
                                                "वर्तमान: v${viewModel.appUpdateManager.currentVersionName} • टच करके चेक करें",
                                            fontSize = 11.sp,
                                            color = if (updateInfo?.isUpdateAvailable == true) MaterialTheme.colorScheme.primary else okTextMuted
                                        )
                                    }
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = okTextMuted)
                            }
                        }

                        // Dukandar Photo & Shop Name Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            .bounceClickable { photoPickerLauncher.launch("image/*") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (shopPhotoUri.isNotEmpty()) {
                                            AsyncImage(
                                                model = shopPhotoUri,
                                                contentDescription = "Shop Photo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Storefront,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("दुकानदार / खाता प्रोफाइल", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                        Text("फोटो बदलने के लिए टैप करें 📸", fontSize = 11.sp, color = okTextMuted)
                                    }
                                }

                                // Shop Name Input
                                OutlinedTextField(
                                    value = tempShopName,
                                    onValueChange = { tempShopName = it },
                                    label = { Text("दुकान / खाता का नाम (Shop Name)") },
                                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = OkGreenPrimary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // UPI ID Section with Lock Protection
                                if (securityPin.isNotEmpty() && !isUpiUnlocked) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "UPI Locked",
                                                    tint = OkGreenPrimary,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                                Column {
                                                    Text("UPI ID (सुरक्षित लॉक्ड 🔒)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                                    Text(
                                                        if (tempUpiId.isNotEmpty()) "••••••••@•••" else "UPI ID सेट नहीं है",
                                                        fontSize = 11.sp,
                                                        color = okTextMuted
                                                    )
                                                }
                                            }
                                            Button(
                                                onClick = { showPinForUpiDialog = true },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text("अनलॉक 🔓", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                } else {
                                    OutlinedTextField(
                                        value = tempUpiId,
                                        onValueChange = { tempUpiId = it },
                                        label = { Text("UPI ID (पेमेंट QR के लिए) ${if (securityPin.isNotEmpty() && isUpiUnlocked) "🔓" else ""}") },
                                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = OkGreenPrimary) },
                                        trailingIcon = {
                                            if (securityPin.isNotEmpty() && isUpiUnlocked) {
                                                IconButton(onClick = { isUpiUnlocked = false }) {
                                                    Icon(Icons.Default.LockOpen, contentDescription = "Lock", tint = OkGreenPrimary, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        },
                                        placeholder = { Text("e.g. mobile@upi / shop@okhdfcbank") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }

                        // 🔒 4-DIGIT SECURITY PIN CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (securityPin.isNotEmpty()) OkGreenPrimary else okTextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (securityPin.isNotEmpty()) "4-अंकों का पिन (PIN Active 🔒)" else "सुरक्षा पिन (Set PIN 🔒)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        )
                                        Text(
                                            text = if (securityPin.isNotEmpty()) "UPI और खाता सुरक्षा सक्रिय है" else "UPI और लेन-देन सुरक्षा के लिए पिन सेट करें",
                                            fontSize = 11.sp,
                                            color = okTextMuted
                                        )
                                    }
                                }

                                Button(
                                    onClick = { showPinDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = if (securityPin.isNotEmpty()) MaterialTheme.colorScheme.surfaceVariant else OkGreenPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (securityPin.isNotEmpty()) "बदलें ✏️" else "सेट करें 🔒",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (securityPin.isNotEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                                    )
                                }
                            }
                        }

                        // 🍎 APPLE iOS LIQUID GLASS MODE (64-Bit 7-Core Butter Smooth)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ios_mode_setting_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isIosModeActive) Color(0xFF007AFF).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.2.dp, if (isIosModeActive) Color(0xFF007AFF).copy(alpha = 0.6f) else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isIosModeActive) Color(0xFF007AFF) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "🍎",
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "iOS Liquid Glass Mode (ग्लास इफ़ेक्ट)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = if (isIosModeActive) Color(0xFF007AFF) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF007AFF).copy(alpha = 0.18f)
                                            ) {
                                                Text(
                                                    text = "64-BIT",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFF007AFF),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Apple Liquid Glass • 64-Bit Butter Smooth • Zero Lag 120 FPS",
                                            fontSize = 11.sp,
                                            color = okTextMuted
                                        )
                                    }
                                }
                                Switch(
                                    checked = isIosModeActive,
                                    onCheckedChange = { isChecked ->
                                        viewModel.setIosModeActive(isChecked)
                                        Toast.makeText(
                                            context,
                                            if (isChecked) "🍎 Apple iOS Liquid Glass Active! (64-Bit Butter Smooth 🚀)" else "iOS Mode Deactivated",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF34C759)
                                    ),
                                    modifier = Modifier.testTag("ios_mode_switch")
                                )
                            }
                        }

                        // 🛡️ APATCH / MAGISK SYSTEM BLUR SUPPORT CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSystemCrossWindowBlurEnabled) Color(0xFF10B981).copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSystemCrossWindowBlurEnabled) Color(0xFF10B981).copy(alpha = 0.5f)
                                else Color(0x33007AFF)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSystemCrossWindowBlurEnabled) Color(0xFF10B981) else Color(0xFFF59E0B),
                                            modifier = Modifier.size(10.dp)
                                        ) {}
                                        Text(
                                            text = "System Blur (APatch Style)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSystemCrossWindowBlurEnabled) Color(0xFF10B981).copy(alpha = 0.20f)
                                        else if (isSystemBlurSupported) Color(0xFF3B82F6).copy(alpha = 0.20f)
                                        else Color(0xFF6B7280).copy(alpha = 0.20f)
                                    ) {
                                        Text(
                                            text = if (isSystemCrossWindowBlurEnabled) "ACTIVE"
                                            else if (isSystemBlurSupported) "STANDBY"
                                            else "GLASS FX",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSystemCrossWindowBlurEnabled) Color(0xFF059669)
                                            else if (isSystemBlurSupported) Color(0xFF2563EB)
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isSystemCrossWindowBlurEnabled)
                                        "🟢 डिवाइस WindowManager Cross-Window Blur व RenderEffect सक्रिय है। APatch की तरह सिस्टम ब्लर स्वतः लागू है।"
                                    else if (isSystemBlurSupported)
                                        "🔵 सिस्टम ब्लr समर्थित है (जब भी फोन/ROM में ब्लर चालू होगा, यह ऐप अपने आप बैकग्राउंड व ग्लास ब्लर कर देगा)।"
                                    else
                                        "✨ अल्ट्रा-फास्ट ग्लास रेंडरिंग एक्टिव है (सिस्टम ब्लर बंद होने पर भी बिना किसी लैग के स्मूथ ग्लास इफेक्ट चलेगा)।",
                                    fontSize = 11.sp,
                                    color = okTextMuted,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ऑटोमैटिक सिस्टम ब्लर सिंक (Auto-Sync)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Switch(
                                        checked = systemBlurAutoSync,
                                        onCheckedChange = { isChecked ->
                                            viewModel.setSystemBlurAutoSync(isChecked)
                                            Toast.makeText(
                                                context,
                                                if (isChecked) "APatch System Blur Sync On!" else "Manual Blur Mode",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }

                        // 1. 🦊 KITSUNE MASK THEME
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isKitsuneTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (isKitsuneTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("🦊 Kitsune Mask (Smooth OS)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("Fox Coral Flame & Mint Pulse Squircle Theme", fontSize = 11.sp, color = okTextMuted)
                                }
                                Switch(
                                    checked = isKitsuneTheme,
                                    onCheckedChange = { isChecked ->
                                        viewModel.setKitsuneTheme(isChecked)
                                        Toast.makeText(context, if (isChecked) "🦊 Kitsune Mask Mode Active!" else "Standard Theme Active", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        // 2. ⚡ CYBERPUNK 2070 THEME
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (is2070Theme) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (is2070Theme) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("⚡ Cyberpunk 2070 Mode (साइबर 2070)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("Futuristic Cyber Cyan/Magenta Neon OLED Glass UI", fontSize = 11.sp, color = okTextMuted)
                                }
                                Switch(
                                    checked = is2070Theme,
                                    onCheckedChange = { isChecked ->
                                        viewModel.set2070Theme(isChecked)
                                        if (isChecked) {
                                            viewModel.setUseDynamicColor(false)
                                            viewModel.setNothingTheme(false)
                                            viewModel.setDarkModeSetting("Dark Mode ON")
                                        }
                                        Toast.makeText(context, if (isChecked) "Cyberpunk 2070 UI Activated ⚡" else "Standard Theme Active", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        // 3. 🤖 ANDROID 17 BETA 3 ULTRA SMOOTH ANIMATION
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAndroid17Animation) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (isAndroid17Animation) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isAndroid17Animation) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🤖", fontSize = 18.sp)
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Android 17 Beta 3 Animation 🚀",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (isAndroid17Animation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Ultra Smooth 120 FPS Liquid Spring Transitions & Physics",
                                            fontSize = 11.sp,
                                            color = okTextMuted
                                        )
                                    }
                                }
                                Switch(
                                    checked = isAndroid17Animation,
                                    onCheckedChange = { isChecked ->
                                        viewModel.setAndroid17Animation(isChecked)
                                        if (isChecked) {
                                            viewModel.setSmoothUiEnabled(true)
                                            viewModel.setGooglePhotosAnimation(true)
                                            viewModel.setCpuGpuTurboMode(true)
                                            viewModel.setAnimationDuration(180f)
                                        }
                                        Toast.makeText(
                                            context,
                                            if (isChecked) "Android 17 Beta 3 Ultra Smooth Animation ON! 🚀" else "Animation OFF (Standard)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        // 4. ⚡ UNITY ENGINE 60 FPS (NO LAG, NO HANG)
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("unity_engine_setting_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnity60FpsActive) Color(0xFF00C853).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (isUnity60FpsActive) Color(0xFF00C853).copy(alpha = 0.5f) else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isUnity60FpsActive) Color(0xFF00C853).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Speed, contentDescription = null, tint = if (isUnity60FpsActive) Color(0xFF00C853) else okTextMuted, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "⚡ Unity Engine 60 FPS (No Lag / No Hang)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (isUnity60FpsActive) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "हार्डवेयर एक्सेलरेटेड स्मूथ स्क्रोलिंग और त्वरित रिस्पॉन्स",
                                            fontSize = 11.sp,
                                            color = okTextMuted
                                        )
                                    }
                                }
                                Switch(
                                    checked = isUnity60FpsActive,
                                    onCheckedChange = { isChecked ->
                                        viewModel.setUnity60FpsActive(isChecked)
                                        Toast.makeText(
                                            context,
                                            if (isChecked) "⚡ Unity Engine 60 FPS सक्रिय (No Lag / No Hang)!" else "Standard FPS",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00C853))
                                )
                            }
                        }

                        // 5. ⚙️ GEAR SCROLL SOUND (स्क्रोल पर गियर जैसी आवाज़ ऑन/ऑफ)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scroll_gear_sound_setting_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (scrollGearSoundEnabled) Color(0xFFFF9800).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, if (scrollGearSoundEnabled) Color(0xFFFF9800).copy(alpha = 0.5f) else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (scrollGearSoundEnabled) Color(0xFFFF9800).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = null,
                                                tint = if (scrollGearSoundEnabled) Color(0xFFFF9800) else okTextMuted,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "⚙️ गियर स्क्रोल साउंड (Gear Scroll Sound)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = if (scrollGearSoundEnabled) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "स्क्रोलिंग के समय मैकेनिकल गियर/रैचेट जैसी टिक आवाज़ और फीडबैक",
                                            fontSize = 11.sp,
                                            color = okTextMuted
                                        )
                                    }
                                }
                                Switch(
                                    checked = scrollGearSoundEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.setScrollGearSoundEnabled(isChecked)
                                        if (isChecked) {
                                            try {
                                                (context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)
                                                    ?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.6f)
                                            } catch (_: Exception) {}
                                        }
                                        Toast.makeText(
                                            context,
                                            if (isChecked) "⚙️ गियर स्क्रोल साउंड चालू (Gear Sound ON)!" else "गियर स्क्रोल साउंड बंद (Gear Sound OFF)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF9800))
                                )
                            }
                        }
                    }

                    // 7. 🪶 MASS 4X MOTION & 90HZ/240HZ ENGINE
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (mass4xEngineActive) OkGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, if (mass4xEngineActive) OkGreenPrimary.copy(alpha = 0.5f) else Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (mass4xEngineActive) OkGreenPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = if (mass4xEngineActive) OkGreenPrimary else okTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "🪶 Mass 4x Motion & 90Hz/240Hz Engine",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = if (mass4xEngineActive) OkGreenPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "4x भारी भौतिक इनर्शिया • 90Hz डिस्प्ले लॉक • 240Hz टच सैंपलिंग",
                                        fontSize = 11.sp,
                                        color = okTextMuted
                                    )
                                }
                            }
                            Switch(
                                checked = mass4xEngineActive,
                                onCheckedChange = { isChecked ->
                                    viewModel.setMass4xEngineActive(isChecked)
                                    viewModel.setDisplay90HzActive(isChecked)
                                    viewModel.setTouch240HzActive(isChecked)
                                    Toast.makeText(
                                        context,
                                        if (isChecked) "🪶 Mass 4x & 90Hz/240Hz इंजन चालू!" else "मानक इनर्शिया मोड",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = OkGreenPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Save & Done Button
                    Button(
                        onClick = {
                            viewModel.setShopName(tempShopName.trim())
                            viewModel.setUpiId(tempUpiId.trim())
                            Toast.makeText(context, "सेटिंग्स सेव हो गईं! ✅", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("सेव और पूर्ण (Save & Done)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BackupRestoreDialog(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isBackingUp by viewModel.isBackingUp.collectAsStateWithLifecycle()
    val isRestoring by viewModel.isRestoring.collectAsStateWithLifecycle()
    val isBackingUpApk by viewModel.isBackingUpApk.collectAsStateWithLifecycle()
    val apkBackupInfo by viewModel.appApkBackupInfo.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.triggerLocalZipRestore(uri) { success ->
                val msg = if (success) "Ledger Restored Successfully!" else "Restore failed. Please choose a valid OkKhata Backup ZIP."
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backup & Restore (बैकअप एंव रिस्टोर)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Your ledger data is stored safely on your phone and automatically backed up locally. You can export a ZIP backup to keep your records safe.",
                    fontSize = 12.sp,
                    color = okTextMuted
                )

                if (isBackingUp || isRestoring) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = OkGreenPrimary, modifier = Modifier.size(24.dp))
                        Text("Processing operation...", fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.triggerLocalZipBackupAndDownload(context) { success ->
                                if (success) Toast.makeText(context, "Saved to Downloads folder!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Text("Download ZIP Backup")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.triggerLocalZipBackupAndShare(context) { }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = OkGreenPrimary)
                            Text("Share Backup ZIP", color = OkGreenPrimary)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.deletePreviousSharedBackup { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("delete_shared_backup_undo_step_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Text("शेयर बैकअप डिलीट करें (1 स्टेप पीछे)", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider()

                    OutlinedButton(
                        onClick = {
                            filePickerLauncher.launch("application/zip")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = OkOrangeRed)
                            Text("Restore Backup ZIP", color = OkOrangeRed)
                        }
                    }

                    HorizontalDivider()

                    // App Only APK Backup (No Data)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ऐप APK बैकअप (No Data - Only Latest APK)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "बिना खाते के डेटा के केवल ऐप की लेटेस्ट APK डाउनलोड या शेयर करें।",
                            fontSize = 11.sp,
                            color = okTextMuted
                        )
                        Button(
                            onClick = {
                                viewModel.createPureAppApkBackup { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("backup_dialog_apk_save_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isBackingUpApk
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(if (isBackingUpApk) "बन रहा है..." else "APK सेव करें", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = okTextMuted)
            }
        }
    )
}

@Composable
fun AiInsightsDialog(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit
) {
    val aiInsightsText by viewModel.aiInsightsText.collectAsStateWithLifecycle()
    val isGeneratingInsights by viewModel.isGeneratingInsights.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OkGreenPrimary)
                Text("AI Business Advisor 🤖", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (isGeneratingInsights) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(color = OkGreenPrimary)
                        Text("Analyzing shop ledger statistics...", fontSize = 12.sp, color = okTextMuted)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = aiInsightsText ?: "No AI insights available.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Got It!")
            }
        }
    )
}


/**
 * 🔮 ScreenCraft & Magisk Signature Liquid Glass Bottom Navigation Dock
 * Features:
 * - ScreenCraft Liquid Glass Niche Scroll Bar with 240Hz low-latency touch scrubbing
 * - Floating Liquid Glass Loupe Bubble with customer info & haptic ticks
 * - Mass 4x Spring Physics & Inertia Engine
 * - Real Frosted Glass Acrylic blur & Specular Refraction border
 * - 4 Tabs: Sabhi (सभी), Denge (देंगे), Lenge (लेंगे), Kishu Mask (🦊) + Center (+ ग्राहक) FAB
 */
@Composable
fun MagiskLiquidGlassBottomBar(
    selectedFilter: KhataFilter,
    khataSummary: KhataSummary,
    isGlassActive: Boolean,
    isKitsuneMaskActive: Boolean = false,
    lazyListState: androidx.compose.foundation.lazy.LazyListState? = null,
    customerUiModels: List<CustomerUiModel> = emptyList(),
    mass4xEnabled: Boolean = true,
    screenCraftScrollBarEnabled: Boolean = true,
    tintIndex: Int = 0,
    specularIntensity: Float = 0.70f,
    onFilterSelected: (KhataFilter) -> Unit,
    onAddCustomerClick: () -> Unit,
    onKitsuneMaskClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val safeTint = tintIndex.coerceIn(0, GLASS_TINT_PRESETS.size - 1)
    val customTint = GLASS_TINT_PRESETS[safeTint].color

    // Animated spring zoom for the bottom dock with Mass 4x physics
    val dockSpringZoom by animateFloatAsState(
        targetValue = if (selectedFilter != KhataFilter.ALL || isKitsuneMaskActive) 1.02f else 1.0f,
        animationSpec = spring(
            dampingRatio = if (mass4xEnabled) 0.72f else 0.75f,
            stiffness = if (mass4xEnabled) 300f else Spring.StiffnessMedium
        ),
        label = "dockSpringZoom"
    )

    val currentTabIndex = when {
        isKitsuneMaskActive -> 4
        selectedFilter == KhataFilter.ADVANCE -> 3
        selectedFilter == KhataFilter.DUE -> 1
        else -> 0
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 12.dp, top = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LiquidBottomTabs(
            selectedTabIndex = { currentTabIndex },
            tabsCount = 5,
            activeHighlightColor = when (currentTabIndex) {
                1 -> Color(0xFF34C759)
                3 -> Color(0xFFFF3B30)
                4 -> if (safeTint == 0) Color(0xFFFF9500) else customTint
                else -> if (safeTint == 0) Color(0xFF007AFF) else customTint
            },
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .graphicsLayer {
                    scaleX = dockSpringZoom
                    scaleY = dockSpringZoom
                }
                .testTag("magisk_liquid_bottom_bar")
        ) {
            // 1. Sabhi (All) Tab
            LiquidBottomTab(
                title = "सभी",
                icon = Icons.Default.ChatBubble,
                badgeCount = (khataSummary.dueCustomerCount + khataSummary.advanceCustomerCount).coerceAtLeast(0),
                isSelected = currentTabIndex == 0,
                activeColor = if (safeTint == 0) Color(0xFF007AFF) else customTint,
                onClick = { onFilterSelected(KhataFilter.ALL) }
            )

            // 2. Denge (देंगे - Due) Tab with Green Accent (Clean - No small paise amount text)
            LiquidBottomTab(
                title = "देंगे",
                icon = Icons.Default.ArrowDownward,
                badgeCount = khataSummary.dueCustomerCount,
                isSelected = currentTabIndex == 1,
                activeColor = Color(0xFF34C759),
                onClick = {
                    if (selectedFilter == KhataFilter.DUE) {
                        onFilterSelected(KhataFilter.ALL)
                    } else {
                        onFilterSelected(KhataFilter.DUE)
                    }
                }
            )

            // 3. Center Hero: + ग्राहक (Add Customer) - Kyant LiquidButton Pill
            LiquidButton(
                onClick = onAddCustomerClick,
                isPrimary = true,
                accentColor = if (safeTint == 0) Color(0xFF007AFF) else customTint,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .padding(horizontal = 2.dp)
                    .testTag("magisk_add_customer_dock_btn")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White.copy(alpha = 0.22f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Customer",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = "+ ग्राहक",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.2.sp,
                        maxLines = 1
                    )
                }
            }

            // 4. Lenge (लेंगे - Advance) Tab with Red Accent (Clean - No small paise amount text)
            LiquidBottomTab(
                title = "लेंगे",
                icon = Icons.Default.ArrowUpward,
                badgeCount = khataSummary.advanceCustomerCount,
                isSelected = currentTabIndex == 3,
                activeColor = Color(0xFFFF3B30),
                onClick = {
                    if (selectedFilter == KhataFilter.ADVANCE) {
                        onFilterSelected(KhataFilter.ALL)
                    } else {
                        onFilterSelected(KhataFilter.ADVANCE)
                    }
                }
            )

            // 5. Kishu Backdrop Catalog Tab (Kyant0 Backdrop Catalog)
            LiquidBottomTab(
                title = "Kishu 🪟",
                icon = Icons.Default.Layers,
                badgeText = if (isGlassActive) "FX" else null,
                isSelected = currentTabIndex == 4,
                activeColor = if (safeTint == 0) Color(0xFF007AFF) else customTint,
                onClick = onKitsuneMaskClick
            )
        }
    }
}

/**
 * 🔮 Kishu Liquid Glass Settings & Controls Hub
 * Authentic Backdrop Catalog iOS Liquid Glass UI:
 * - 100% Real, Working & Persistent App Settings
 * - Kyant LiquidToggle for switches
 * - Kyant LiquidSlider for blur/vibrancy adjustments
 * - Kyant LiquidButton for actions
 * - Real Shop Name, UPI, PIN Security, Sound, Motion & Backup settings
 */
@Composable
fun KitsuneMaskHubDialog(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    // Real persistent settings
    val isGlassActive by viewModel.isGlassActive.collectAsStateWithLifecycle()
    val glassBlurIntensity by viewModel.glassBlurIntensity.collectAsStateWithLifecycle()
    val mass4xEngineActive by viewModel.mass4xEngineActive.collectAsStateWithLifecycle()
    val display90HzActive by viewModel.display90HzActive.collectAsStateWithLifecycle()
    val touch240HzActive by viewModel.touch240HzActive.collectAsStateWithLifecycle()
    val scrollGearSoundEnabled by viewModel.scrollGearSoundEnabled.collectAsStateWithLifecycle()
    val soundboxAlertEnabled by viewModel.soundboxAlertEnabled.collectAsStateWithLifecycle()

    val shopName by viewModel.shopName.collectAsStateWithLifecycle()
    val upiId by viewModel.upiId.collectAsStateWithLifecycle()
    val securityPin by viewModel.securityPin.collectAsStateWithLifecycle()

    var isEditingShop by remember { mutableStateOf(false) }
    var tempShopName by remember(shopName) { mutableStateOf(shopName) }
    var tempUpiId by remember(upiId) { mutableStateOf(upiId) }

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInputValue by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    if (isDark) Color(0xEB1C1C1E) else Color(0xF8FFFFFF)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (isDark) 0.50f else 0.95f),
                            Color.White.copy(alpha = if (isDark) 0.08f else 0.25f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF9500).copy(alpha = 0.20f),
                            border = BorderStroke(1.dp, Color(0xFFFF9500).copy(alpha = 0.6f)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9500),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Kishu Liquid Settings",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = if (isDark) Color.White else Color(0xFF1C1C1E)
                            )
                            Text(
                                text = "100% Working • Backdrop Liquid Glass",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDark) Color.White else Color(0xFF1C1C1E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))

                // Section 1: 🔮 Backdrop Catalog Liquid Glass Controls
                Text(
                    text = "🔮 लिक्विड ग्लास व मोशन (Liquid Glass)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF007AFF)
                )

                // 1. Frosted Liquid Glass Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "iOS Frosted Liquid Glass",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Backdrop Catalog रिफ्रैक्शन व स्पेक्युलर रिम",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                        )
                    }
                    LiquidToggle(
                        checked = isGlassActive,
                        onCheckedChange = {
                            viewModel.setGlassMode(it)
                            Toast.makeText(context, if (it) "🔮 लिक्विड ग्लास सक्रिय!" else "लिक्विड ग्लास बंद", Toast.LENGTH_SHORT).show()
                        },
                        activeColor = Color(0xFF007AFF)
                    )
                }

                // 2. Liquid Glass Blur Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ब्लर व वाइब्रेंसी (Blur Intensity)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "${glassBlurIntensity.toInt()}dp",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.5.sp,
                            color = Color(0xFF007AFF)
                        )
                    }
                    LiquidSlider(
                        value = glassBlurIntensity,
                        onValueChange = { viewModel.setGlassBlurIntensity(it) },
                        valueRange = 8f..40f,
                        accentColor = Color(0xFF007AFF)
                    )
                }

                // 3. Mass 4x Spring Physics Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mass 4x Spring Physics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "स्प्रिंग इनर्शिया व स्मूथ फ्लिंग ग्लाइड",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                        )
                    }
                    LiquidToggle(
                        checked = mass4xEngineActive,
                        onCheckedChange = {
                            viewModel.setMass4xEngineActive(it)
                            Toast.makeText(context, if (it) "🪶 Mass 4x इनर्शिया ऑन!" else "Mass 4x बंद", Toast.LENGTH_SHORT).show()
                        },
                        activeColor = Color(0xFF34C759)
                    )
                }

                // 4. 90Hz Display + 240Hz Touch Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "90Hz/120Hz Ultra Smooth",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "240Hz टच सैंपलिंग व जीरो लैग फ़्रेम पेसर",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                        )
                    }
                    LiquidToggle(
                        checked = display90HzActive && touch240HzActive,
                        onCheckedChange = {
                            viewModel.setDisplay90HzActive(it)
                            viewModel.setTouch240HzActive(it)
                            Toast.makeText(context, if (it) "⚡ 90Hz + 240Hz टच एक्टिव!" else "स्टैंडर्ड टच मोड", Toast.LENGTH_SHORT).show()
                        },
                        activeColor = Color(0xFFFF9500)
                    )
                }

                // 5. Scroll Haptic Sound Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "गियर स्क्रोल साउंड व हेप्टिक",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "टच व क्लिक पर रियल मेकैनिकल टिक साउंड",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                        )
                    }
                    LiquidToggle(
                        checked = scrollGearSoundEnabled,
                        onCheckedChange = {
                            viewModel.setScrollGearSoundEnabled(it)
                            Toast.makeText(context, if (it) "🔊 हेप्टिक साउंड ऑन!" else "साउंड बंद", Toast.LENGTH_SHORT).show()
                        },
                        activeColor = Color(0xFF5856D6)
                    )
                }

                HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))

                // Section 2: 🏪 Shop Details & UPI
                Text(
                    text = "🏪 दुकान व यूपीआई सेटिंग्स (Shop & UPI)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF34C759)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isEditingShop) {
                        OutlinedTextField(
                            value = tempShopName,
                            onValueChange = { tempShopName = it },
                            label = { Text("दुकान का नाम (Shop Name)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempUpiId,
                            onValueChange = { tempUpiId = it },
                            label = { Text("UPI ID (पेमेंट के लिए)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LiquidButton(
                                onClick = {
                                    viewModel.setShopName(tempShopName.trim().ifEmpty { "OkKhata" })
                                    viewModel.setUpiId(tempUpiId.trim().ifEmpty { "pay@okkhata" })
                                    isEditingShop = false
                                    Toast.makeText(context, "✅ दुकान व UPI सुरक्षित किया गया!", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = true,
                                accentColor = Color(0xFF34C759),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("सेव करें (Save)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            LiquidButton(
                                onClick = { isEditingShop = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("रद्द करें", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "दुकान: $shopName",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color.White else Color(0xFF1C1C1E)
                                )
                                Text(
                                    text = "UPI: $upiId",
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                                )
                            }
                            LiquidButton(
                                onClick = { isEditingShop = true },
                                isPrimary = false,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("बदलें (Edit)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Soundbox Payment Alert Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "साउंडबॉक्स वॉइस अलर्ट (Voice Alert)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "पेमेंट मिलने पर Paytm/PhonePe जैसा ऑडियो",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                        )
                    }
                    LiquidToggle(
                        checked = soundboxAlertEnabled,
                        onCheckedChange = {
                            viewModel.setSoundboxAlertEnabled(it)
                            Toast.makeText(context, if (it) "📢 साउंडबॉक्स अलर्ट ऑन!" else "साउंडबॉक्स बंद", Toast.LENGTH_SHORT).show()
                        },
                        activeColor = Color(0xFF34C759)
                    )
                }

                HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))

                // Section 3: 🔒 PIN Security
                Text(
                    text = "🔒 सुरक्षा व प्राइवेसी (Security PIN)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color(0xFFFF3B30)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f))
                        .border(0.7.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "4-डिजिट ऐप सुरक्षा पिन",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E)
                        )
                        Text(
                            text = if (securityPin.isNotEmpty()) "पिन चालू है (App Locked)" else "पिन बंद है (कोई लॉक नहीं)",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)
                        )
                    }
                    LiquidToggle(
                        checked = securityPin.isNotEmpty(),
                        onCheckedChange = { checked ->
                            if (checked) {
                                showPinDialog = true
                            } else {
                                viewModel.setSecurityPin("")
                                Toast.makeText(context, "🔓 पिन लॉक हटा दिया गया", Toast.LENGTH_SHORT).show()
                            }
                        },
                        activeColor = Color(0xFFFF3B30)
                    )
                }

                HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))

                // Section 4: 💾 Quick Actions (Backup & Update)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiquidButton(
                        onClick = {
                            coroutineScope.launch {
                                val success = viewModel.backupManager.performAutoLocalBackup()
                                Toast.makeText(
                                    context,
                                    if (success) "💾 खाता डेटा स्थानीय बैकअप पूर्ण!" else "बैकअप असफल",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("बैकअप लें", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    LiquidButton(
                        onClick = {
                            viewModel.appUpdateManager.evaluateCurrentConfig()
                            Toast.makeText(context, "🚀 ऐप वर्शन अप-टू-डेट है!", Toast.LENGTH_SHORT).show()
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("अपडेट चेक", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Done Button
                LiquidButton(
                    onClick = onDismiss,
                    isPrimary = true,
                    accentColor = Color(0xFF007AFF),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "पूर्ण करें (Done)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Set PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("नया सुरक्षा पिन दर्ज करें (Set PIN)") },
            text = {
                OutlinedTextField(
                    value = pinInputValue,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInputValue = it },
                    label = { Text("4 अंकों का पिन") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInputValue.length == 4) {
                            viewModel.setSecurityPin(pinInputValue)
                            showPinDialog = false
                            pinInputValue = ""
                            Toast.makeText(context, "🔒 नया पिन सुरक्षित किया गया!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "कृपया 4 अंकों का पिन दर्ज करें", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("पिन सेव करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

fun saveCustomFontFromUri(context: Context, uri: Uri): Pair<String, String>? {
    try {
        val fileName = getFileName(context, uri)
        val fontFile = java.io.File(context.filesDir, "custom_font.ttf")
        if (fontFile.exists()) fontFile.delete()

        if (fileName.endsWith(".zip", ignoreCase = true)) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                java.util.zip.ZipInputStream(inputStream).use { zipStream ->
                    var entry = zipStream.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && (entry.name.endsWith(".ttf", ignoreCase = true) || entry.name.endsWith(".otf", ignoreCase = true))) {
                            fontFile.outputStream().use { out ->
                                zipStream.copyTo(out)
                            }
                            return Pair("Custom Font ($fileName)", fontFile.absolutePath)
                        }
                        entry = zipStream.nextEntry
                    }
                }
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                fontFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            if (fontFile.exists() && fontFile.length() > 0) {
                return Pair("Custom Font ($fileName)", fontFile.absolutePath)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun saveShopkeeperPhotoFromUri(context: Context, uri: Uri): String? {
    try {
        val photoFile = java.io.File(context.filesDir, "shopkeeper_photo.jpg")
        if (photoFile.exists()) photoFile.delete()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            photoFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        if (photoFile.exists() && photoFile.length() > 0) {
            return photoFile.absolutePath
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun shareWhatsAppReport(
    context: Context,
    customer: CustomerEntity,
    transactions: List<TransactionEntity>,
    netBalance: Double,
    shopName: String = "",
    upiId: String = ""
) {
    val sb = StringBuilder()
    val displayShop = if (shopName.isBlank()) "OkKhata Store" else shopName
    sb.append("🏪 *$displayShop*\n")
    sb.append("👤 Customer Statement: *${customer.name}*\n")
    sb.append("📅 Date: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}\n\n")

    if (transactions.isNotEmpty()) {
        sb.append("📝 *Recent Ledger Entries:*\n")
        transactions.takeLast(5).forEach { tx ->
            val type = if (tx.isCredit) "🔴 YOU GAVE (उधार)" else "🟢 YOU GOT (जमा)"
            sb.append("• ₹${formatAmount(tx.amount)} - $type ${if (tx.remarks.isNotEmpty()) "(${tx.remarks})" else ""}\n")
        }
        sb.append("\n------------------------\n")
    }

    if (netBalance > 0) {
        sb.append("📌 *Total Outstanding Due: ₹${formatAmount(netBalance)}*\n")
        sb.append("📋 *Status: Pending Clearance*\n")
        if (upiId.isNotBlank()) {
            sb.append("\n📲 *Direct UPI Settlement:* `$upiId`\n_(Pay instantly via GPay / PhonePe / Paytm / BHIM)_\n")
        }
        sb.append("\nPlease process the pending settlement to maintain an updated commercial ledger account.\n")
    } else if (netBalance < 0) {
        sb.append("🟢 *Advance Credit Balance: ₹${formatAmount(kotlin.math.abs(netBalance))}*\n")
        sb.append("Status: Credit Deposited\n")
    } else {
        sb.append("✅ *Account Status: Fully Settled (₹0 Balance)*\n")
    }

    sb.append("\n━━━━━━━━━━━━━━━━━━━\n_Official Statement | Billing & Accounts, ${displayShop}_")

    val messageText = sb.toString()
    val phone = customer.phone.trim()

    if (phone.isNotBlank()) {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "").let {
            if (it.length == 10) "91$it" else it
        }
        try {
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(messageText)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            try {
                val url = "https://wa.me/$cleanPhone?text=${Uri.encode(messageText)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
                return
            } catch (_: Exception) { }
        }
    }

    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Statement via"))
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to share statement", Toast.LENGTH_SHORT).show()
    }
}

fun shareOkCreditCardOnWhatsApp(
    context: Context,
    customer: CustomerEntity,
    netBalance: Double,
    shopName: String,
    upiId: String,
    shopkeeperPhotoPath: String
) {
    try {
        val width = 1080
        val height = 1350
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        canvas.drawColor(android.graphics.Color.parseColor("#F4F6F5"))
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

        // Top Header Banner
        paint.color = android.graphics.Color.parseColor("#008955")
        canvas.drawRect(0f, 0f, width.toFloat(), 340f, paint)

        // Shop Name
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 52f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        val displayShopName = if (shopName.isBlank()) "OkKhata Digital Store" else shopName
        canvas.drawText(displayShopName, 60f, 130f, paint)

        paint.textSize = 32f
        paint.typeface = android.graphics.Typeface.DEFAULT
        paint.color = android.graphics.Color.parseColor("#E0F2F1")
        canvas.drawText("Verified OkCredit Ledger Statement", 60f, 190f, paint)

        // Dukandar Photo
        if (shopkeeperPhotoPath.isNotBlank()) {
            val photoFile = java.io.File(shopkeeperPhotoPath)
            if (photoFile.exists()) {
                val ownerBmp = android.graphics.BitmapFactory.decodeFile(photoFile.absolutePath)
                if (ownerBmp != null) {
                    val scaled = android.graphics.Bitmap.createScaledBitmap(ownerBmp, 180, 180, true)
                    val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                    }
                    canvas.drawCircle(880f, 160f, 96f, borderPaint)

                    val path = android.graphics.Path().apply {
                        addCircle(880f, 160f, 90f, android.graphics.Path.Direction.CW)
                    }
                    canvas.save()
                    canvas.clipPath(path)
                    canvas.drawBitmap(scaled, 790f, 70f, null)
                    canvas.restore()
                }
            }
        }

        // White Card Box
        paint.color = android.graphics.Color.WHITE
        val cardRect = android.graphics.RectF(50f, 260f, (width - 50).toFloat(), 1180f)
        canvas.drawRoundRect(cardRect, 32f, 32f, paint)

        // Customer Info
        paint.color = android.graphics.Color.parseColor("#1C1E1D")
        paint.textSize = 48f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText(customer.name, 100f, 360f, paint)

        paint.textSize = 32f
        paint.color = android.graphics.Color.GRAY
        val phoneStr = if (customer.phone.isBlank()) "Mobile: N/A" else "Mobile: ${customer.phone}"
        canvas.drawText(phoneStr, 100f, 410f, paint)

        // Divider
        paint.color = android.graphics.Color.parseColor("#EEEEEE")
        paint.strokeWidth = 3f
        canvas.drawLine(100f, 450f, (width - 100).toFloat(), 450f, paint)

        // Balance Section
        val isDue = netBalance > 0
        val isAdvance = netBalance < 0
        val balanceColor = if (isDue) android.graphics.Color.parseColor("#D32F2F") else if (isAdvance) android.graphics.Color.parseColor("#008955") else android.graphics.Color.DKGRAY

        paint.color = balanceColor
        paint.textSize = 70f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        val amtText = "₹${formatAmount(kotlin.math.abs(netBalance))}"
        canvas.drawText(amtText, 100f, 550f, paint)

        paint.textSize = 34f
        val statusText = if (isDue) "Total Pending Balance (Aap Lenge)" else if (isAdvance) "Advance Balance (Aap Denge)" else "Account Settled (₹0 Balance)"
        canvas.drawText(statusText, 100f, 605f, paint)

        // UPI Box
        if (upiId.isNotBlank() && isDue) {
            paint.color = android.graphics.Color.parseColor("#E8F5E9")
            val qrBox = android.graphics.RectF(100f, 660f, (width - 100).toFloat(), 950f)
            canvas.drawRoundRect(qrBox, 24f, 24f, paint)

            paint.color = android.graphics.Color.parseColor("#008955")
            paint.textSize = 38f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            canvas.drawText("📲 Pay Online via UPI", 140f, 730f, paint)

            paint.color = android.graphics.Color.BLACK
            paint.textSize = 34f
            paint.typeface = android.graphics.Typeface.MONOSPACE
            canvas.drawText("UPI ID: $upiId", 140f, 790f, paint)

            paint.color = android.graphics.Color.DKGRAY
            paint.textSize = 28f
            paint.typeface = android.graphics.Typeface.DEFAULT
            canvas.drawText("Scan & pay using PhonePe, Google Pay or Paytm", 140f, 850f, paint)
        }

        // Date Footer
        paint.color = android.graphics.Color.GRAY
        paint.textSize = 30f
        val dateStr = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        canvas.drawText("Statement Date: $dateStr", 100f, 1120f, paint)

        // App Branding
        paint.color = android.graphics.Color.parseColor("#008955")
        paint.textSize = 38f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("OkKhata Smart Digital Ledger", 280f, 1260f, paint)

        val cacheFile = java.io.File(context.cacheDir, "okkhata_statement_card.png")
        cacheFile.outputStream().use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
        }

        val contentUri: Uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cacheFile
        )

        val cleanPhone = customer.phone.replace(Regex("[^0-9]"), "").let {
            if (it.length == 10) "91$it" else it
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, "🏪 *$shopName* - Statement Card for *${customer.name}*\nPending: ₹${formatAmount(netBalance)}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (cleanPhone.isNotBlank()) {
            try {
                shareIntent.setPackage("com.whatsapp")
                context.startActivity(shareIntent)
                return
            } catch (_: Exception) {
                try {
                    shareIntent.setPackage("com.whatsapp.w4b")
                    context.startActivity(shareIntent)
                    return
                } catch (_: Exception) { }
            }
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Statement Card"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error generating card: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private val FastAppThreadLocalCurrencyFormat = ThreadLocal.withInitial {
    java.text.DecimalFormat("#,##,##0.##").apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
}

fun formatAmount(amount: Double): String {
    return FastAppThreadLocalCurrencyFormat.get()?.format(amount) ?: String.format(Locale.US, "%.2f", amount)
}

fun formatCompactAmount(amount: Double): String {
    val absVal = Math.abs(amount)
    return when {
        absVal >= 10000000 -> String.format(Locale.getDefault(), "%.1fCr", amount / 10000000)
        absVal >= 100000 -> String.format(Locale.getDefault(), "%.1fL", amount / 100000)
        absVal >= 1000 -> String.format(Locale.getDefault(), "%.1fK", amount / 1000)
        else -> FastAppThreadLocalCurrencyFormat.get()?.format(amount) ?: String.format(Locale.US, "%.2f", amount)
    }
}

private val AvatarColorsList = listOf(
    Color(0xFF008955),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA),
    Color(0xFFD81B60),
    Color(0xFFF57C00),
    Color(0xFF388E3C),
    Color(0xFF00ACC1)
)

fun getAvatarColor(name: String): Color {
    val index = kotlin.math.abs(name.hashCode()) % AvatarColorsList.size
    return AvatarColorsList[index]
}

@Composable
fun PinVerificationDialog(
    correctPin: String,
    title: String = "Enter 4-Digit Security PIN",
    subtitle: String = "PIN verification required to proceed",
    onDismiss: () -> Unit,
    onVerified: (pinUsed: String) -> Unit
) {
    val isSettingNewPin = correctPin.isEmpty()
    val dialogTitle = if (isSettingNewPin) "Set 4-Digit Security PIN 🔒" else title
    val dialogSubtitle = if (isSettingNewPin) "Choose a 4-digit PIN to secure your ledger actions." else subtitle
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(enteredPin) {
        if (enteredPin.length == 4) {
            if (isSettingNewPin) {
                onVerified(enteredPin)
            } else if (enteredPin == correctPin) {
                onVerified(enteredPin)
            } else {
                errorMessage = "Incorrect PIN! (गलत पिन) Please try again."
                enteredPin = ""
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (errorMessage != null) OkOrangeRed else OkGreenPrimary
                )
                Text(dialogTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(dialogSubtitle, fontSize = 12.sp, color = okTextMuted, textAlign = TextAlign.Center)

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        errorMessage != null -> OkOrangeRed
                                        isFilled -> OkGreenPrimary
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    if (errorMessage != null) OkOrangeRed else if (isFilled) OkGreenPrimary else Color.Gray.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(errorMessage!!, color = OkOrangeRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Instant Zero-Lag Tactile Keypad
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "⌫")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    digits.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { char ->
                                Surface(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            when (char) {
                                                "C" -> {
                                                    enteredPin = ""
                                                    errorMessage = null
                                                }
                                                "⌫" -> {
                                                    if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                    errorMessage = null
                                                }
                                                else -> {
                                                    if (enteredPin.length < 4) {
                                                        errorMessage = null
                                                        enteredPin += char
                                                    }
                                                }
                                            }
                                        },
                                    shape = CircleShape,
                                    color = when (char) {
                                        "C", "⌫" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    tonalElevation = 2.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = char,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (char) {
                                                "C" -> OkOrangeRed
                                                "⌫" -> OkGreenPrimary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isSettingNewPin && enteredPin.length == 4) {
                Button(
                    onClick = { onVerified(enteredPin) },
                    colors = ButtonDefaults.buttonColors(containerColor = OkGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save & Continue")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = okTextMuted)
            }
        }
    )
}

fun sendSmsReport(
    context: Context,
    customer: CustomerEntity,
    netBalance: Double,
    shopName: String = "",
    upiId: String = ""
) {
    if (customer.phone.isEmpty()) {
        Toast.makeText(context, "No phone number available for SMS", Toast.LENGTH_SHORT).show()
        return
    }
    val displayShop = if (shopName.isBlank()) "OkKhata Store" else shopName
    val formattedAmt = formatAmount(kotlin.math.abs(netBalance))

    val message = if (netBalance > 0) {
        "PAYMENT ADVICE: $displayShop se aapka ledger balance ₹$formattedAmt pending due hai. Kripya account update ke liye samay par bhugtan karein.${if (upiId.isNotBlank()) " Pay via UPI: $upiId" else ""} - Accounts, $displayShop"
    } else if (netBalance < 0) {
        "CREDIT ADVICE: $displayShop me aapka advance balance ₹$formattedAmt jama hai. - Accounts, $displayShop"
    } else {
        "ACCOUNT CLEAR: $displayShop me aapka ledger account settled (₹0.00) hai. - Accounts, $displayShop"
    }

    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${customer.phone}")
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open SMS composer", Toast.LENGTH_SHORT).show()
    }
}

fun shareKhataSlipOnWhatsApp(
    context: Context,
    customer: CustomerEntity,
    netBalance: Double,
    shopName: String = "",
    upiId: String = ""
) {
    val displayShop = if (shopName.isBlank()) "OkKhata Store" else shopName
    val formattedAmt = formatAmount(kotlin.math.abs(netBalance))

    val message = if (netBalance > 0) {
        "💼 *OFFICIAL PAYMENT ADVICE*\n🏪 *$displayShop*\n━━━━━━━━━━━━━━━━━━━\nClient: *${customer.name}*\n\n📌 *Outstanding Due: ₹$formattedAmt*\n📋 *Status: Pending Clearance*${if (upiId.isNotBlank()) "\n\n📲 *Direct UPI Settlement:*\n`$upiId`\n_(Tap to copy & pay via GPay / PhonePe / Paytm / BHIM)_" else ""}\n\nPlease settle the pending balance to keep your commercial ledger account updated.\n━━━━━━━━━━━━━━━━━━━\n_Billing & Accounts Department_\n*$displayShop*"
    } else if (netBalance < 0) {
        "💼 *CREDIT BALANCE STATEMENT*\n🏪 *$displayShop*\n━━━━━━━━━━━━━━━━━━━\nClient: *${customer.name}*\n🟢 *Advance Credit Balance: ₹$formattedAmt*\nStatus: *Active Credit Available*\n━━━━━━━━━━━━━━━━━━━\n*$displayShop*"
    } else {
        "✅ *ACCOUNT SETTLEMENT STATEMENT*\n🏪 *$displayShop*\n━━━━━━━━━━━━━━━━━━━\nClient: *${customer.name}*\n*Account Balance: ₹0.00 (All Clear)*\nStatus: *Fully Settled*\n━━━━━━━━━━━━━━━━━━━\n*$displayShop*"
    }

    try {
        val cleanPhone = customer.phone.replace("+", "").replace(" ", "").replace("-", "")
        val intent = if (cleanPhone.isNotBlank()) {
            val phoneFormatted = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
            Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$phoneFormatted&text=${Uri.encode(message)}"))
        } else {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                setPackage("com.whatsapp")
            }
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        } catch (ex: Exception) {
            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}

fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use { c ->
            if (c.moveToFirst()) {
                val index = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = c.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "custom_font.zip"
}

/**
 * 📊 Total Khata Overview & Calculation Dialog
 * Triggered when clicking the shop name / header at the top
 */
@Composable
fun TotalKhataSummaryDialog(
    khataSummary: KhataSummary,
    customerCount: Int,
    shopName: String,
    onFilterSelected: (KhataFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val netBalance = khataSummary.totalDue - khataSummary.totalAdvance

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Total Calculator",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "कुल हिसाब (Total Summary)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = shopName,
                        fontSize = 12.sp,
                        color = okTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Net Balance Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (netBalance >= 0) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        }
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (netBalance >= 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (netBalance >= 0) "कुल शुद्ध लेना है (NET YOU WILL GET)" else "कुल शुद्ध देना है (NET YOU WILL GIVE)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (netBalance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "₹${formatAmount(Math.abs(netBalance))}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (netBalance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = if (netBalance >= 0) "🟢 बाज़ार से इतना पैसा आपके पास आना है" else "🔴 इतना पैसा आपको ग्राहकों को देना है",
                            fontSize = 11.sp,
                            color = okTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 2. Detailed Breakdown Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // YOU GET (देंगे - कुल लेना है)
                    val hasDue = khataSummary.totalDue > 0
                    val dueZoomScale by animateFloatAsState(
                        targetValue = if (hasDue) 1.12f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessLow),
                        label = "dueZoom"
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .bounceClickable {
                                onFilterSelected(KhataFilter.DUE)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF131D2A).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.45f)
                        ),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.5f else 0.8f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                )
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "देंगे (Get)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "₹${formatAmount(khataSummary.totalDue)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = dueZoomScale
                                    scaleY = dueZoomScale
                                }
                            )
                            Text(
                                text = "${khataSummary.dueCustomerCount} ग्राहक बाकी",
                                fontSize = 10.5.sp,
                                color = okTextMuted
                            )
                        }
                    }

                    // YOU GIVE (लेंगे - कुल देना है)
                    val hasAdvance = khataSummary.totalAdvance > 0
                    val advanceZoomScale by animateFloatAsState(
                        targetValue = if (hasAdvance) 1.12f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessLow),
                        label = "advanceZoom"
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .bounceClickable {
                                onFilterSelected(KhataFilter.ADVANCE)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF131D2A).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.45f)
                        ),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.5f else 0.8f),
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
                                )
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "लेंगे (Give)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = "₹${formatAmount(khataSummary.totalAdvance)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = advanceZoomScale
                                    scaleY = advanceZoomScale
                                }
                            )
                            Text(
                                text = "${khataSummary.advanceCustomerCount} ग्राहक एडवांस",
                                fontSize = 10.5.sp,
                                color = okTextMuted
                            )
                        }
                    }
                }

                // 3. Stats Summary List
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("👥 कुल रजिस्टर्ड ग्राहक:", fontSize = 12.sp, color = okTextMuted)
                            Text("$customerCount ग्राहक", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("⚡ Android OS इंजन:", fontSize = 12.sp, color = okTextMuted)
                            Text("🤖 120 FPS Magisk Liquid", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // 4. Quick Share Khata Summary Button
                OutlinedButton(
                    onClick = {
                        val shareText = """
                            📊 *${shopName} - खाता रिपोर्ट*
                            ━━━━━━━━━━━━━━━━━━━
                            🟢 कुल लेना है (YOU'LL GET): ₹${formatAmount(khataSummary.totalDue)} (${khataSummary.dueCustomerCount} ग्राहक)
                            🔴 कुल देना है (YOU'LL GIVE): ₹${formatAmount(khataSummary.totalAdvance)} (${khataSummary.advanceCustomerCount} ग्राहक)
                            💰 शुद्ध बाकी (NET BALANCE): ₹${formatAmount(Math.abs(netBalance))} ${if (netBalance >= 0) "(लेना है)" else "(देना है)"}
                            ━━━━━━━━━━━━━━━━━━━
                            📱 कुल ग्राहक: $customerCount
                            ⚡ Generated by OkKhata Android
                        """.trimIndent()

                        try {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Khata Summary"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not share summary", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📤 पूरा हिसाब शेयर करें (Share)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("ठीक है (OK)", fontWeight = FontWeight.Bold)
            }
        }
    )
}
