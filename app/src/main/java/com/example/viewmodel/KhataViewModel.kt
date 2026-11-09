package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CustomerEntity
import com.example.data.KhataRepository
import com.example.data.TransactionEntity
import com.example.data.AppUpdateManager
import com.example.data.AppUpdateInfo
import com.example.data.UpdateStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentHashMap

private val ThreadLocalCurrencyFormatter = ThreadLocal.withInitial {
    DecimalFormat("#,##,##0.##").apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
}

private fun fastFormatAmount(amount: Double): String {
    return ThreadLocalCurrencyFormatter.get()?.format(amount) ?: String.format(Locale.US, "%.2f", amount)
}

private val threadLocalChatDateFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
}

private val threadLocalChatTimeFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("hh:mm a", Locale.getDefault())
}

private val chatDateHeaderCache = ConcurrentHashMap<Long, String>()
private val chatTimeCache = ConcurrentHashMap<Long, String>()

fun fastGetChatDateHeader(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val offset = TimeZone.getDefault().getOffset(timestamp)
    val dayKey = (timestamp + offset) / 86_400_000L

    val nowOffset = TimeZone.getDefault().getOffset(now)
    val todayDayKey = (now + nowOffset) / 86_400_000L
    val diff = todayDayKey - dayKey

    if (diff == 0L) return "Today (आज)"
    if (diff == 1L) return "Yesterday (कल)"

    return chatDateHeaderCache.computeIfAbsent(dayKey) {
        threadLocalChatDateFormatter.get()?.format(Date(timestamp)) ?: "Ledger Entry"
    }
}

fun fastFormatChatTime(timestamp: Long): String {
    return chatTimeCache.computeIfAbsent(timestamp) {
        threadLocalChatTimeFormatter.get()?.format(Date(timestamp)) ?: ""
    }
}

@Immutable
data class TransactionGroup(
    val dateHeader: String,
    val transactions: List<TransactionEntity>
)

enum class KhataFilter {
    ALL, DUE, ADVANCE
}

private val QuickAvatarColors = listOf(
    0xFF008955L,
    0xFF1E88E5L,
    0xFF8E24AAL,
    0xFFD81B60L,
    0xFFF57C00L,
    0xFF388E3CL,
    0xFF00ACC1L
)

private val PrecomputedAvatarColors = QuickAvatarColors.map { Color(it) }
private val PrecomputedAvatarBgColors = QuickAvatarColors.map { Color(it).copy(alpha = 0.18f) }
private val PrecomputedAvatarBorderColors = QuickAvatarColors.map { Color(it).copy(alpha = 0.65f) }

@Immutable
data class CustomerUiModel(
    val customer: CustomerEntity,
    val netBalance: Double, // positive = customer owes merchant (Due), negative = merchant owes customer (Advance)
    val formattedBalance: String = "",
    val initialChar: String = "",
    val avatarColorHex: Long = 0xFF008955L,
    val phoneDisplay: String = "",
    val lastTransactionTime: Long? = null,
    val avatarColor: Color = Color(avatarColorHex),
    val avatarBgColor: Color = Color(avatarColorHex).copy(alpha = 0.18f),
    val avatarBorderColor: Color = Color(avatarColorHex).copy(alpha = 0.65f)
)

@Immutable
data class KhataSummary(
    val totalDue: Double = 0.0,         // total customers owe us (receivable)
    val totalAdvance: Double = 0.0,     // total we owe customers (advance)
    val dueCustomerCount: Int = 0,
    val advanceCustomerCount: Int = 0
)

class KhataViewModel(
    private val repository: KhataRepository,
    val backupManager: com.example.data.CloudBackupManager,
    private val context: android.content.Context
) : ViewModel() {

    init {
        // Run automatic local backup check lazily in background (zero impact on app open)
        viewModelScope.launch(Dispatchers.IO) {
            kotlinx.coroutines.delay(20_000)
            try {
                backupManager.performAutoLocalBackup()
                backupManager.scheduleDailyMidnightBackup()
            } catch (e: Exception) {
                // Silently ignore
            }
        }
    }

    private val prefs = context.getSharedPreferences("okkhata_settings", android.content.Context.MODE_PRIVATE)

    val securityPin = MutableStateFlow(prefs.getString("security_pin", "") ?: "")
    val isLocked = MutableStateFlow((prefs.getString("security_pin", "") ?: "").isNotEmpty())
    val upiId = MutableStateFlow(prefs.getString("upi_id", "pay@okkhata") ?: "pay@okkhata")
    val shopName = MutableStateFlow(prefs.getString("shop_name", "OkKhata") ?: "OkKhata")
    val showBackupHubDialogOnStart = MutableStateFlow(false)

    // Animation & UI Customization State
    val useDynamicColor = MutableStateFlow(prefs.getBoolean("use_dynamic_color", true))
    val animationStyle = MutableStateFlow(prefs.getString("animation_style", "Springy Fade") ?: "Springy Fade")
    val animationDuration = MutableStateFlow(prefs.getFloat("animation_duration", 240f))
    val isGlassActive = MutableStateFlow(prefs.getBoolean("glass_active", true))
    val isSmooth2Active = MutableStateFlow(prefs.getBoolean("smooth_2_active", true))
    val glassBlurIntensity = MutableStateFlow(prefs.getFloat("glass_blur_intensity", 20f))
    val glassTintIndex = MutableStateFlow(prefs.getInt("glass_tint_index", 0))
    val specularIntensity = MutableStateFlow(prefs.getFloat("specular_intensity", 0.70f))
    val chromaticGlowEnabled = MutableStateFlow(prefs.getBoolean("chromatic_glow_enabled", true))
    val grainOverlayEnabled = MutableStateFlow(prefs.getBoolean("grain_overlay_enabled", false))
    val isSystemBlurSupported = MutableStateFlow(true)
    val isSystemCrossWindowBlurEnabled = MutableStateFlow(true)
    val systemBlurAutoSync = MutableStateFlow(prefs.getBoolean("system_blur_auto_sync", true))
    val zeroLagEngineActive = MutableStateFlow(prefs.getBoolean("zero_lag_engine_active", true))
    val isKitsuneTheme = MutableStateFlow(prefs.getBoolean("kitsune_theme_active", true))
    val isIosModeActive = MutableStateFlow(prefs.getBoolean("ios_mode_active", true))
    val isNothingTheme = MutableStateFlow(prefs.getBoolean("nothing_theme_active", false))
    val is2070Theme = MutableStateFlow(prefs.getBoolean("cyber_2070_theme_active", false))
    val smoothUiEnabled = MutableStateFlow(prefs.getBoolean("smooth_ui_enabled", true))
    val cpuGpuTurboMode = MutableStateFlow(prefs.getBoolean("cpu_gpu_turbo_mode", true))
    val soundboxAlertEnabled = MutableStateFlow(prefs.getBoolean("soundbox_alert_enabled", true))
    val scrollGearSoundEnabled = MutableStateFlow(prefs.getBoolean("scroll_gear_sound_enabled", true))
    val iconifyStyle = MutableStateFlow(prefs.getString("iconify_style", "WhatsApp Emerald") ?: "WhatsApp Emerald")
    val avatarShape = MutableStateFlow(prefs.getString("avatar_shape", "Circle") ?: "Circle")
    val showCustomerAvatars = MutableStateFlow(prefs.getBoolean("show_customer_avatars", true))
    val showCustomerBoxContainer = MutableStateFlow(prefs.getBoolean("show_customer_box_container", true))
    val isUnity60FpsActive = MutableStateFlow(prefs.getBoolean("unity_60fps_active", true))
    val isAndroid17Animation = MutableStateFlow(prefs.getBoolean("android17_animation_active", true))
    val isGooglePhotosAnimation = MutableStateFlow(prefs.getBoolean("google_photos_animation_active", true))
    val appResolution = MutableStateFlow(prefs.getString("app_resolution", "1080p Ultra HD (Default)") ?: "1080p Ultra HD (Default)")
    val darkModeSetting = MutableStateFlow(prefs.getString("dark_mode_setting", "Dark Mode OFF") ?: "Dark Mode OFF")
    val selectedFont = MutableStateFlow(prefs.getString("selected_font", "Google Inter (Clean Sans)") ?: "Google Inter (Clean Sans)")
    val customFontPath = MutableStateFlow(prefs.getString("custom_font_path", "") ?: "")
    val shopkeeperPhotoPath = MutableStateFlow(prefs.getString("shopkeeper_photo_path", "") ?: "")

    val kitsuneModuleCoreActive = MutableStateFlow(prefs.getBoolean("kitsune_module_core_active", true))
    val kitsuneModuleFramePacerActive = MutableStateFlow(prefs.getBoolean("kitsune_module_frame_pacer_active", true))
    val kitsuneModuleSqliteCacheActive = MutableStateFlow(prefs.getBoolean("kitsune_module_sqlite_cache_active", true))
    val chromeCompositor4xSmooth = MutableStateFlow(prefs.getBoolean("chrome_compositor_4x_smooth", true))
    val kitsuneZygiskEnabled = MutableStateFlow(prefs.getBoolean("kitsune_zygisk_enabled", true))
    val kitsuneMagiskHideEnabled = MutableStateFlow(prefs.getBoolean("kitsune_magiskhide_enabled", true))
    val kitsuneSELinuxMode = MutableStateFlow(prefs.getString("kitsune_selinux_mode", "Enforcing") ?: "Enforcing")
    val kitsuneZygoteWarmupActive = MutableStateFlow(prefs.getBoolean("kitsune_zygote_warmup_active", true))
    val mass4xEngineActive = MutableStateFlow(prefs.getBoolean("mass_4x_engine_active", true))
    val display90HzActive = MutableStateFlow(prefs.getBoolean("display_90hz_active", true))
    val touch240HzActive = MutableStateFlow(prefs.getBoolean("touch_240hz_active", true))
    val screenCraftLiquidGlassActive = MutableStateFlow(prefs.getBoolean("screencraft_liquid_glass_active", true))
    val screenCraftBottomScrollBarActive = MutableStateFlow(prefs.getBoolean("screencraft_bottom_scrollbar_active", true))

    // Online App Update Engine
    val appUpdateManager = AppUpdateManager(context)
    val updateStatus: StateFlow<UpdateStatus> = appUpdateManager.updateStatus
    val currentUpdateInfo: StateFlow<AppUpdateInfo?> = appUpdateManager.currentUpdateInfo

    fun setUseDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean("use_dynamic_color", enabled).apply()
        useDynamicColor.value = enabled
    }

    fun setSelectedFont(font: String, customPath: String = "") {
        prefs.edit()
            .putString("selected_font", font)
            .putString("custom_font_path", customPath)
            .apply()
        selectedFont.value = font
        customFontPath.value = customPath
    }

    fun setShopkeeperPhotoPath(path: String) {
        prefs.edit().putString("shopkeeper_photo_path", path).apply()
        shopkeeperPhotoPath.value = path
    }

    fun setAnimationStyle(style: String) {
        prefs.edit().putString("animation_style", style).apply()
        animationStyle.value = style
    }

    fun setAnimationDuration(duration: Float) {
        prefs.edit().putFloat("animation_duration", duration).apply()
        animationDuration.value = duration
    }

    fun setGlassMode(active: Boolean) {
        prefs.edit().putBoolean("glass_active", active).apply()
        isGlassActive.value = active
    }

    fun setSmooth2Active(active: Boolean) {
        prefs.edit().putBoolean("smooth_2_active", active).apply()
        isSmooth2Active.value = active
        if (active) {
            setGlassMode(true)
            setSmoothUiEnabled(true)
            setCpuGpuTurboMode(true)
            setGooglePhotosAnimation(true)
            setAndroid17Animation(true)
            setZeroLagEngineActive(true)
        }
    }

    fun setGlassBlurIntensity(intensity: Float) {
        prefs.edit().putFloat("glass_blur_intensity", intensity).apply()
        glassBlurIntensity.value = intensity
    }

    fun setGlassTintIndex(index: Int) {
        prefs.edit().putInt("glass_tint_index", index).apply()
        glassTintIndex.value = index
    }

    fun setSpecularIntensity(intensity: Float) {
        prefs.edit().putFloat("specular_intensity", intensity).apply()
        specularIntensity.value = intensity
    }

    fun setChromaticGlowEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("chromatic_glow_enabled", enabled).apply()
        chromaticGlowEnabled.value = enabled
    }

    fun setGrainOverlayEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("grain_overlay_enabled", enabled).apply()
        grainOverlayEnabled.value = enabled
    }

    fun updateSystemBlurStatus(supported: Boolean, enabled: Boolean) {
        isSystemBlurSupported.value = supported
        isSystemCrossWindowBlurEnabled.value = enabled
        if (systemBlurAutoSync.value && supported && enabled) {
            if (!isGlassActive.value) {
                setGlassMode(true)
            }
        }
    }

    fun setSystemBlurAutoSync(autoSync: Boolean) {
        prefs.edit().putBoolean("system_blur_auto_sync", autoSync).apply()
        systemBlurAutoSync.value = autoSync
        if (autoSync && isSystemBlurSupported.value && isSystemCrossWindowBlurEnabled.value) {
            setGlassMode(true)
        }
    }

    fun setZeroLagEngineActive(active: Boolean) {
        prefs.edit().putBoolean("zero_lag_engine_active", active).apply()
        zeroLagEngineActive.value = active
    }

    fun setIosModeActive(active: Boolean) {
        prefs.edit().putBoolean("ios_mode_active", active).apply()
        isIosModeActive.value = active
        if (active) {
            prefs.edit().putBoolean("kitsune_theme_active", false).apply()
            isKitsuneTheme.value = false
            set2070Theme(false)
            setNothingTheme(false)
            setDarkModeSetting("Dark Mode OFF")
            setUseDynamicColor(false)
            setUnity60FpsActive(true)
            setCpuGpuTurboMode(true)
            setZeroLagEngineActive(true)
            setSmoothUiEnabled(true)
            setGlassMode(true)
            setSmooth2Active(true)
        }
    }

    fun setKitsuneTheme(active: Boolean) {
        prefs.edit().putBoolean("kitsune_theme_active", active).apply()
        isKitsuneTheme.value = active
        if (active) {
            prefs.edit().putBoolean("ios_mode_active", false).apply()
            isIosModeActive.value = false
            set2070Theme(false)
            setNothingTheme(false)
            setUseDynamicColor(false)
            setSmoothUiEnabled(true)
        }
    }

    fun setNothingTheme(active: Boolean) {
        prefs.edit().putBoolean("nothing_theme_active", active).apply()
        isNothingTheme.value = active
        if (active) {
            prefs.edit().putBoolean("kitsune_theme_active", false).apply()
            isKitsuneTheme.value = false
        }
    }

    fun set2070Theme(active: Boolean) {
        prefs.edit().putBoolean("cyber_2070_theme_active", active).apply()
        is2070Theme.value = active
        if (active) {
            prefs.edit().putBoolean("kitsune_theme_active", false).apply()
            isKitsuneTheme.value = false
        }
    }

    fun setSmoothUiEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("smooth_ui_enabled", enabled).apply()
        smoothUiEnabled.value = enabled
    }

    fun setCpuGpuTurboMode(enabled: Boolean) {
        prefs.edit().putBoolean("cpu_gpu_turbo_mode", enabled).apply()
        cpuGpuTurboMode.value = enabled
        if (enabled) {
            setSmoothUiEnabled(true)
            setAnimationDuration(140f)
        }
    }

    fun setSoundboxAlertEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("soundbox_alert_enabled", enabled).apply()
        soundboxAlertEnabled.value = enabled
    }

    fun setScrollGearSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("scroll_gear_sound_enabled", enabled).apply()
        scrollGearSoundEnabled.value = enabled
    }

    fun setIconifyStyle(style: String) {
        prefs.edit().putString("iconify_style", style).apply()
        iconifyStyle.value = style
    }

    fun setAvatarShape(shape: String) {
        prefs.edit().putString("avatar_shape", shape).apply()
        avatarShape.value = shape
    }

    fun setShowCustomerAvatars(enabled: Boolean) {
        prefs.edit().putBoolean("show_customer_avatars", enabled).apply()
        showCustomerAvatars.value = enabled
    }

    fun setShowCustomerBoxContainer(enabled: Boolean) {
        prefs.edit().putBoolean("show_customer_box_container", enabled).apply()
        showCustomerBoxContainer.value = enabled
    }

    fun setUnity60FpsActive(active: Boolean) {
        prefs.edit().putBoolean("unity_60fps_active", active).apply()
        isUnity60FpsActive.value = active
        if (active) {
            setZeroLagEngineActive(true)
            setCpuGpuTurboMode(true)
            setSmoothUiEnabled(true)
        }
    }

    fun setAndroid17Animation(enabled: Boolean) {
        prefs.edit().putBoolean("android17_animation_active", enabled).apply()
        isAndroid17Animation.value = enabled
        if (enabled) {
            setSmoothUiEnabled(true)
            setGooglePhotosAnimation(true)
            setCpuGpuTurboMode(true)
            setAnimationDuration(180f)
            setAnimationStyle("Android 17 Spring")
        }
    }

    fun setGooglePhotosAnimation(enabled: Boolean) {
        prefs.edit().putBoolean("google_photos_animation_active", enabled).apply()
        isGooglePhotosAnimation.value = enabled
    }

    fun setKitsuneModuleCoreActive(active: Boolean) {
        prefs.edit().putBoolean("kitsune_module_core_active", active).apply()
        kitsuneModuleCoreActive.value = active
    }

    fun setKitsuneModuleFramePacerActive(active: Boolean) {
        prefs.edit().putBoolean("kitsune_module_frame_pacer_active", active).apply()
        kitsuneModuleFramePacerActive.value = active
    }

    fun setKitsuneModuleSqliteCacheActive(active: Boolean) {
        prefs.edit().putBoolean("kitsune_module_sqlite_cache_active", active).apply()
        kitsuneModuleSqliteCacheActive.value = active
    }

    fun setChromeCompositor4xSmooth(enabled: Boolean) {
        prefs.edit().putBoolean("chrome_compositor_4x_smooth", enabled).apply()
        chromeCompositor4xSmooth.value = enabled
    }

    fun setKitsuneZygiskEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("kitsune_zygisk_enabled", enabled).apply()
        kitsuneZygiskEnabled.value = enabled
    }

    fun setKitsuneMagiskHideEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("kitsune_magiskhide_enabled", enabled).apply()
        kitsuneMagiskHideEnabled.value = enabled
    }

    fun setKitsuneSELinuxMode(mode: String) {
        prefs.edit().putString("kitsune_selinux_mode", mode).apply()
        kitsuneSELinuxMode.value = mode
    }

    fun setKitsuneZygoteWarmupActive(enabled: Boolean) {
        prefs.edit().putBoolean("kitsune_zygote_warmup_active", enabled).apply()
        kitsuneZygoteWarmupActive.value = enabled
    }

    fun setMass4xEngineActive(enabled: Boolean) {
        prefs.edit().putBoolean("mass_4x_engine_active", enabled).apply()
        mass4xEngineActive.value = enabled
    }

    fun setDisplay90HzActive(enabled: Boolean) {
        prefs.edit().putBoolean("display_90hz_active", enabled).apply()
        display90HzActive.value = enabled
    }

    fun setTouch240HzActive(enabled: Boolean) {
        prefs.edit().putBoolean("touch_240hz_active", enabled).apply()
        touch240HzActive.value = enabled
    }

    fun setScreenCraftLiquidGlassActive(enabled: Boolean) {
        prefs.edit().putBoolean("screencraft_liquid_glass_active", enabled).apply()
        screenCraftLiquidGlassActive.value = enabled
    }

    fun setScreenCraftBottomScrollBarActive(enabled: Boolean) {
        prefs.edit().putBoolean("screencraft_bottom_scrollbar_active", enabled).apply()
        screenCraftBottomScrollBarActive.value = enabled
    }

    fun flushKitsuneCacheAndBoost() {
        setKitsuneModuleCoreActive(true)
        setKitsuneModuleFramePacerActive(true)
        setKitsuneModuleSqliteCacheActive(true)
        setChromeCompositor4xSmooth(true)
        setKitsuneZygiskEnabled(true)
        setKitsuneMagiskHideEnabled(true)
        setKitsuneZygoteWarmupActive(true)
        setMass4xEngineActive(true)
        setDisplay90HzActive(true)
        setTouch240HzActive(true)
        setScreenCraftLiquidGlassActive(true)
        setScreenCraftBottomScrollBarActive(true)
        setZeroLagEngineActive(true)
        setCpuGpuTurboMode(true)
        setSmoothUiEnabled(true)
    }

    fun setAppResolution(resolution: String) {
        prefs.edit().putString("app_resolution", resolution).apply()
        appResolution.value = resolution
    }

    fun setDarkModeSetting(setting: String) {
        prefs.edit().putString("dark_mode_setting", setting).apply()
        darkModeSetting.value = setting
    }

    fun setShopName(name: String) {
        val clean = name.trim()
        val formatted = if (clean.isEmpty()) "OkKhata" else clean
        prefs.edit().putString("shop_name", formatted).apply()
        shopName.value = formatted
    }

    fun setSecurityPin(pin: String) {
        prefs.edit().putString("security_pin", pin).apply()
        securityPin.value = pin
        isLocked.value = false
    }

    fun removeSecurityPin() {
        prefs.edit().remove("security_pin").apply()
        securityPin.value = ""
        isLocked.value = false
    }

    fun setUpiId(newUpi: String) {
        val clean = newUpi.trim()
        val formatted = if (clean.isEmpty()) "pay@okkhata" else clean
        prefs.edit().putString("upi_id", formatted).apply()
        upiId.value = formatted
    }

    fun lockApp() {
        if (securityPin.value.isNotEmpty()) {
            isLocked.value = true
        }
    }

    fun unlockApp() {
        isLocked.value = false
    }

    val searchQuery = MutableStateFlow("")
    @OptIn(FlowPreview::class)
    private val debouncedSearchQuery = searchQuery
        .debounce { query -> if (query.isEmpty()) 0L else 50L }
        .distinctUntilChanged()
    val selectedFilter = MutableStateFlow(KhataFilter.ALL)
    val isSortedByAmount = MutableStateFlow(true) // defaults to true for high-to-low amount sorting

    // Google Cloud Sync operation states
    val isBackingUp = MutableStateFlow(false)
    val isRestoring = MutableStateFlow(false)
    val backupSuccessEvent = MutableStateFlow<String?>(null)

    // Gemini Insights response tracking
    val aiInsightsText = MutableStateFlow<String?>(null)
    val isGeneratingInsights = MutableStateFlow(false)

    // Fetch dynamic AI insight summary using fallback/default profile
    fun loadGoogleCloudAiInsights() {
        viewModelScope.launch {
            try {
                isGeneratingInsights.value = true
                val customersList = repository.allCustomers.first()
                val transactionsList = repository.allTransactions.first()
                val activeEmail = "raushangaming256@gmail.com"
                val response = com.example.data.GeminiCloudService.getBusinessInsights(customersList, transactionsList, activeEmail)
                aiInsightsText.value = response
            } catch (e: Exception) {
                aiInsightsText.value = "Unable to load insights: ${e.message}"
            } finally {
                isGeneratingInsights.value = false
            }
        }
    }

    // Dynamic recovery text prompt generator
    fun generateAiRecoveryMessage(customerName: String, amount: Double, phone: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val script = com.example.data.GeminiCloudService.generatePoliteReminder(
                customerName = customerName,
                amount = amount,
                phone = phone,
                shopName = shopName.value,
                upiId = upiId.value
            )
            onComplete(script)
        }
    }

    // Local ZIP Backup & Share triggers
    fun triggerLocalZipBackupAndShare(activityContext: android.content.Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isBackingUp.value = true
            val success = backupManager.exportAndShareBackupZip(activityContext)
            isBackingUp.value = false
            if (success) {
                backupSuccessEvent.value = "Aapka sara ledger data ZIP file me compressed ho gaya h aur share ke liye ready h!"
            }
            onComplete(success)
        }
    }

    fun triggerLocalZipBackupAndDownload(activityContext: android.content.Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isBackingUp.value = true
            val success = backupManager.exportAndSaveBackupToDownloads(activityContext)
            isBackingUp.value = false
            if (success) {
                backupSuccessEvent.value = "Aapka sara ledger data ZIP file me compressed ho gaya h aur Downloads folder me save ho gaya h!"
            }
            onComplete(success)
        }
    }

    fun triggerLocalZipRestore(uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isRestoring.value = true
            val success = backupManager.restoreFromBackupZipUri(uri)
            isRestoring.value = false
            onComplete(success)
        }
    }

    private suspend fun <T> StateFlow<T>.firstOrNull(): T? {
        return this.value
    }

    private data class LedgerAggregates(
        val balanceMap: Map<Int, Double>,
        val lastTxMap: Map<Int, Long>,
        val summary: KhataSummary
    )

    private val ledgerAggregatesFlow = combine(
        repository.allCustomers,
        repository.allTransactions
    ) { customers, transactions ->
        val balanceMap = HashMap<Int, Double>(customers.size.coerceAtLeast(16))
        val lastTxMap = HashMap<Int, Long>(customers.size.coerceAtLeast(16))
        for (tx in transactions) {
            if (tx.isDeleted) continue
            val delta = if (tx.isCredit) tx.amount else -tx.amount
            balanceMap[tx.customerId] = (balanceMap[tx.customerId] ?: 0.0) + delta
            val curLast = lastTxMap[tx.customerId] ?: 0L
            if (tx.timestamp > curLast) {
                lastTxMap[tx.customerId] = tx.timestamp
            }
        }

        var totalDue = 0.0
        var totalAdvance = 0.0
        var dueCount = 0
        var advCount = 0

        for (customer in customers) {
            val netBalance = balanceMap[customer.id] ?: 0.0
            if (netBalance > 0) {
                totalDue += netBalance
                dueCount++
            } else if (netBalance < 0) {
                totalAdvance += kotlin.math.abs(netBalance)
                advCount++
            }
        }

        LedgerAggregates(
            balanceMap = balanceMap,
            lastTxMap = lastTxMap,
            summary = KhataSummary(totalDue, totalAdvance, dueCount, advCount)
        )
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LedgerAggregates(emptyMap(), emptyMap(), KhataSummary())
    )

    // Financial business summary for header component
    val khataSummary: StateFlow<KhataSummary> = ledgerAggregatesFlow
        .map { it.summary }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = KhataSummary()
        )

    // Master list of all customers, sorted and filtered with zero UI-thread overhead
    val customerUiModels: StateFlow<List<CustomerUiModel>> = combine(
        repository.allCustomers,
        ledgerAggregatesFlow,
        debouncedSearchQuery,
        selectedFilter,
        isSortedByAmount
    ) { customers, aggregates, query, filter, sortByAmt ->
        val balanceMap = aggregates.balanceMap
        val lastTxMap = aggregates.lastTxMap
        val cleanQuery = query.trim()
        val hasQuery = cleanQuery.isNotEmpty()

        val resultList = ArrayList<CustomerUiModel>(customers.size)
        for (customer in customers) {
            val netBalance = balanceMap[customer.id] ?: 0.0

            val matchesFilter = when (filter) {
                KhataFilter.ALL -> true
                KhataFilter.DUE -> netBalance > 0
                KhataFilter.ADVANCE -> netBalance < 0
            }
            if (!matchesFilter) continue

            if (hasQuery) {
                val matchesQuery = customer.name.contains(cleanQuery, ignoreCase = true) ||
                        customer.phone.contains(cleanQuery, ignoreCase = true) ||
                        customer.secondaryPhone.contains(cleanQuery, ignoreCase = true) ||
                        customer.address.contains(cleanQuery, ignoreCase = true)
                if (!matchesQuery) continue
            }

            val absVal = kotlin.math.abs(netBalance)
            val formattedAmt = fastFormatAmount(absVal)
            val colorIdx = kotlin.math.abs(customer.name.hashCode()) % QuickAvatarColors.size
            val avatarHex = QuickAvatarColors[colorIdx]
            val initial = if (customer.name.isNotEmpty()) customer.name.take(1).uppercase() else "?"
            val phoneDisp = if (customer.phone.isNotEmpty()) customer.phone else if (customer.address.isNotEmpty()) customer.address else "Active Ledger"

            resultList.add(
                CustomerUiModel(
                    customer = customer,
                    netBalance = netBalance,
                    formattedBalance = formattedAmt,
                    initialChar = initial,
                    avatarColorHex = avatarHex,
                    phoneDisplay = phoneDisp,
                    lastTransactionTime = lastTxMap[customer.id],
                    avatarColor = PrecomputedAvatarColors[colorIdx],
                    avatarBgColor = PrecomputedAvatarBgColors[colorIdx],
                    avatarBorderColor = PrecomputedAvatarBorderColors[colorIdx]
                )
            )
        }

        if (sortByAmt) {
            resultList.sortByDescending { kotlin.math.abs(it.netBalance) }
        } else {
            resultList.sortByDescending { it.lastTransactionTime ?: it.customer.createdAt }
        }
        resultList
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Active customer tracking
    private val _selectedCustomerId = MutableStateFlow<Int?>(null)
    val selectedCustomerId: StateFlow<Int?> = _selectedCustomerId

    fun selectCustomer(id: Int?) {
        _selectedCustomerId.value = id
    }

    val activeCustomerUiModel: StateFlow<CustomerUiModel?> = combine(
        _selectedCustomerId,
        repository.allCustomers,
        repository.allTransactions
    ) { id, customers, transactions ->
        if (id == null) return@combine null
        val customer = customers.find { it.id == id } ?: return@combine null
        val txs = transactions.filter { it.customerId == id && !it.isDeleted }
        val netBalance = txs.sumOf { if (it.isCredit) it.amount else -it.amount }
        val lastTxTime = txs.maxOfOrNull { it.timestamp }
        val absVal = kotlin.math.abs(netBalance)
        val formattedAmt = String.format(java.util.Locale.getDefault(), "%,.2f", absVal).removeSuffix(".00")
        val colorIdx = kotlin.math.abs(customer.name.hashCode()) % QuickAvatarColors.size
        val avatarHex = QuickAvatarColors[colorIdx]
        val initial = if (customer.name.isNotEmpty()) customer.name.take(1).uppercase() else "?"
        val phoneDisp = if (customer.phone.isNotEmpty()) customer.phone else if (customer.address.isNotEmpty()) customer.address else "Active Ledger"

        CustomerUiModel(
            customer = customer,
            netBalance = netBalance,
            formattedBalance = formattedAmt,
            initialChar = initial,
            avatarColorHex = avatarHex,
            phoneDisplay = phoneDisp,
            lastTransactionTime = lastTxTime
        )
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val activeTransactionsList: StateFlow<List<TransactionEntity>> = combine(
        _selectedCustomerId,
        repository.allTransactions
    ) { id, transactions ->
        if (id == null) return@combine emptyList()
        transactions.filter { it.customerId == id }.sortedBy { it.timestamp }
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Pre-computed, background-thread isolated transaction groups (Zero UI thread lag during chat scroll)
    val activeGroupedTransactions: StateFlow<List<TransactionGroup>> = activeTransactionsList
        .map { transactions ->
            if (transactions.isEmpty()) return@map emptyList()
            val groupsMap = LinkedHashMap<String, MutableList<TransactionEntity>>()
            for (tx in transactions) {
                val header = fastGetChatDateHeader(tx.timestamp)
                groupsMap.getOrPut(header) { mutableListOf() }.add(tx)
            }
            groupsMap.map { (header, txList) -> TransactionGroup(header, txList) }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Business actions
    fun addCustomer(name: String, phone: String, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val cleanName = name.trim()
            val cleanPhone = phone.trim()
            if (cleanName.isNotEmpty()) {
                val newId = repository.insertCustomer(CustomerEntity(name = cleanName, phone = cleanPhone))
                launch(Dispatchers.Main) {
                    onComplete(newId.toInt())
                }
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun updateCustomerProfile(
        id: Int,
        name: String,
        phone: String,
        address: String,
        secondaryPhone: String,
        photoUri: String?,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val customer = repository.getCustomerById(id)
            if (customer != null) {
                val updated = customer.copy(
                    name = name.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    secondaryPhone = secondaryPhone.trim(),
                    photoUri = photoUri
                )
                repository.insertCustomer(updated)
                launch(Dispatchers.Main) {
                    onComplete(true)
                }
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                launch(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    fun updateCustomerAutoSms(
        customerId: Int,
        enabled: Boolean,
        frequency: String,
        time: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val customer = repository.getCustomerById(customerId)
            if (customer != null) {
                val updated = customer.copy(
                    autoSmsEnabled = enabled,
                    autoSmsFrequency = frequency,
                    autoSmsTime = time
                )
                repository.insertCustomer(updated)
                launch(Dispatchers.Main) {
                    onComplete(true)
                }
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                launch(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    fun deleteCustomer(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCustomer(id)
            if (_selectedCustomerId.value == id) {
                _selectedCustomerId.value = null
            }
            launch(Dispatchers.IO) {
                try {
                    backupManager.performAutoLocalBackup()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val lastAddedTransactionEvent = MutableStateFlow<TransactionEntity?>(null)

    fun addTransaction(customerId: Int, amount: Double, isCredit: Boolean, remarks: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (amount > 0) {
                val tx = TransactionEntity(
                    customerId = customerId,
                    amount = amount,
                    isCredit = isCredit,
                    remarks = remarks.trim(),
                    timestamp = System.currentTimeMillis()
                )
                repository.insertTransaction(tx)
                
                // Trigger Soundbox alert
                lastAddedTransactionEvent.value = tx

                // Perform auto backup asynchronously without blocking data flow
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun updateTransaction(
        id: Int,
        newAmount: Double,
        newIsCredit: Boolean,
        newRemarks: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repository.allTransactions.first()
            val existing = transactions.find { it.id == id }
            if (existing != null && newAmount > 0) {
                val updated = existing.copy(
                    amount = newAmount,
                    isCredit = newIsCredit,
                    remarks = newRemarks.trim(),
                    isEdited = true,
                    editedAt = System.currentTimeMillis(),
                    originalAmount = existing.originalAmount ?: existing.amount
                )
                repository.insertTransaction(updated)
                launch(Dispatchers.Main) { onComplete(true) }
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                launch(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun softDeleteTransaction(id: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repository.allTransactions.first()
            val existing = transactions.find { it.id == id }
            if (existing != null) {
                val updated = existing.copy(
                    isDeleted = true,
                    deletedAt = System.currentTimeMillis()
                )
                repository.insertTransaction(updated)
                launch(Dispatchers.Main) { onComplete() }
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun restoreTransaction(id: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repository.allTransactions.first()
            val existing = transactions.find { it.id == id }
            if (existing != null) {
                val updated = existing.copy(
                    isDeleted = false,
                    deletedAt = null
                )
                repository.insertTransaction(updated)
                launch(Dispatchers.Main) { onComplete() }
                launch(Dispatchers.IO) {
                    try {
                        backupManager.performAutoLocalBackup()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(id)
            launch(Dispatchers.IO) {
                try {
                    backupManager.performAutoLocalBackup()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun clearCustomerHistory(customerId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearCustomerHistory(customerId)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun clearAllKhataAndProfileData(resetProfile: Boolean = true, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
            _selectedCustomerId.value = null
            searchQuery.value = ""
            selectedFilter.value = KhataFilter.ALL
            if (resetProfile) {
                prefs.edit()
                    .remove("shopkeeper_photo_path")
                    .remove("shop_name")
                    .remove("upi_id")
                    .remove("upiId")
                    .apply()
                shopkeeperPhotoPath.value = ""
                shopName.value = "OkKhata"
                upiId.value = "pay@okkhata"
            }
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateFilter(filter: KhataFilter) {
        selectedFilter.value = filter
    }

    // Online Update Actions
    fun checkForAppUpdates(isRemote: Boolean = false) {
        viewModelScope.launch {
            if (isRemote) {
                appUpdateManager.checkForUpdatesRemote()
            } else {
                appUpdateManager.evaluateCurrentConfig()
            }
        }
    }

    fun saveUpdateConfig(
        serverJsonUrl: String,
        targetVersionName: String,
        targetVersionCode: Int,
        targetApkUrl: String,
        changelog: String,
        sizeMb: Double
    ) {
        appUpdateManager.saveCustomUpdateConfig(
            serverJsonUrl = serverJsonUrl,
            targetVersionName = targetVersionName,
            targetVersionCode = targetVersionCode,
            targetApkUrl = targetApkUrl,
            changelog = changelog,
            sizeMb = sizeMb
        )
    }

    fun startFastApkDownload(apkUrl: String = "", versionName: String = "") {
        viewModelScope.launch {
            val finalUrl = if (apkUrl.isNotBlank()) apkUrl else appUpdateManager.getTargetApkUrl()
            val finalVersion = if (versionName.isNotBlank()) versionName else appUpdateManager.getTargetVersionName()
            appUpdateManager.startFastApkDownload(finalUrl, finalVersion)
        }
    }

    val appApkBackupInfo = appUpdateManager.appApkBackupInfo
    val isBackingUpApk = appUpdateManager.isBackingUpApk

    fun createPureAppApkBackup(onResult: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val result = appUpdateManager.createPureAppApkBackup()
            result.onSuccess { info ->
                onResult?.invoke(true, "ऐप APK बैकअप Download/OkKhata_Backup में सेव हो गया! (${String.format(java.util.Locale.US, "%.1f", info.sizeMb)} MB)")
            }.onFailure { err ->
                onResult?.invoke(false, err.localizedMessage ?: "बैकअप असफल रहा")
            }
        }
    }

    fun shareApk(context: android.content.Context, apkFile: java.io.File) {
        appUpdateManager.shareApk(context, apkFile)
    }

    fun installDownloadedApk(context: android.content.Context, apkFile: java.io.File) {
        appUpdateManager.installApk(context, apkFile)
    }

    fun openDownloadsFolder(context: android.content.Context) {
        appUpdateManager.openDownloadsFolder(context)
    }

    fun downloadViaSystemManager(context: android.content.Context, apkUrl: String, versionName: String) {
        appUpdateManager.downloadViaSystemManager(context, apkUrl, versionName)
    }

    fun resetUpdateStatus() {
        appUpdateManager.resetStatus()
    }

    fun clearOldUpdateCache(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            appUpdateManager.clearAllOldUpdateCacheAndReset(onResult)
        }
    }

    fun deletePreviousSharedBackup(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = backupManager.deletePreviousSharedBackup()
            onResult(result.first, result.second)
        }
    }
}
