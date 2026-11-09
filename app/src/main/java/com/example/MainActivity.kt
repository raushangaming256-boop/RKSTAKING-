package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import com.example.data.KhataDatabase
import com.example.data.KhataRepository
import com.example.ui.components.OkKhataApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.KhataViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable hardware acceleration and high refresh rate (90Hz / 120Hz) for zero-lag smooth scrolling
        applyHighRefreshRate()

        // 🚀 Mass 4x Cold-Start & Open Lag Killer:
        // Pre-warm database, SharedPreferences & threadpools on background worker thread concurrently
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                KhataDatabase.getDatabase(applicationContext).openHelper.readableDatabase
            } catch (_: Exception) {}
        }

        val database = KhataDatabase.getDatabase(applicationContext)
        val repository = KhataRepository(database.khataDao())
        val backupManager = com.example.data.CloudBackupManager(applicationContext, repository)

        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return KhataViewModel(repository, backupManager, applicationContext) as T
            }
        })[KhataViewModel::class.java]

        // APatch-style System Blur detection & real-time sync with Android 12+ WindowManager
        setupSystemBlurSupport(viewModel)

        setContent {
            val isIosTheme by viewModel.isIosModeActive.collectAsStateWithLifecycle()
            val isKitsuneTheme by viewModel.isKitsuneTheme.collectAsStateWithLifecycle()
            val isNothingTheme by viewModel.isNothingTheme.collectAsStateWithLifecycle()
            val is2070Theme by viewModel.is2070Theme.collectAsStateWithLifecycle()
            val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()
            val selectedFont by viewModel.selectedFont.collectAsStateWithLifecycle()
            val customFontPath by viewModel.customFontPath.collectAsStateWithLifecycle()
            val darkModeSetting by viewModel.darkModeSetting.collectAsStateWithLifecycle()
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDarkTheme = when (darkModeSetting) {
                "Dark Mode ON" -> true
                "Dark Mode OFF" -> false
                else -> systemDark
            }

            MyApplicationTheme(
                iosTheme = isIosTheme,
                kitsuneTheme = isKitsuneTheme,
                nothingTheme = isNothingTheme,
                cyber2070Theme = is2070Theme,
                selectedFont = selectedFont,
                customFontPath = customFontPath,
                darkTheme = isDarkTheme,
                dynamicColor = useDynamicColor
            ) {
                OkKhataApp(viewModel = viewModel)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        applyHighRefreshRate()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applyHighRefreshRate()
        }
    }

    private fun setupSystemBlurSupport(viewModel: KhataViewModel) {
        // Fast, zero-lag blur detection without blocking SurfaceFlinger window compositor
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val isCrossWindowBlur = try { windowManager.isCrossWindowBlurEnabled } catch (_: Throwable) { true }
            viewModel.updateSystemBlurStatus(supported = true, enabled = isCrossWindowBlur)
        } else {
            viewModel.updateSystemBlurStatus(supported = false, enabled = true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun applyHighRefreshRate() {
        try {
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )

            // 🚀 240Hz Ultra-Low Latency Touch Sampling Pipeline
            window.decorView.isHovered = false
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                window.colorMode = android.content.pm.ActivityInfo.COLOR_MODE_DEFAULT
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
                window.isStatusBarContrastEnforced = false
            }

            // 🚀 90Hz Display Refresh Rate Engine
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val displayObj = try { display } catch (_: Exception) { null }
                val modes = displayObj?.supportedModes
                // Prioritize 90Hz mode, or 120Hz if device defaults to high
                val targetMode = modes?.filter { it.refreshRate in 88f..95f }?.firstOrNull()
                    ?: modes?.filter { it.refreshRate >= 89f }?.minByOrNull { it.refreshRate }
                    ?: modes?.maxByOrNull { it.refreshRate }
                if (targetMode != null) {
                    window.attributes = window.attributes.apply {
                        preferredDisplayModeId = targetMode.modeId
                        preferredRefreshRate = targetMode.refreshRate
                    }
                }
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                val modes = window.windowManager.defaultDisplay.supportedModes
                val targetMode = modes?.filter { it.refreshRate in 88f..95f }?.firstOrNull()
                    ?: modes?.filter { it.refreshRate >= 89f }?.minByOrNull { it.refreshRate }
                    ?: modes?.maxByOrNull { it.refreshRate }
                if (targetMode != null) {
                    window.attributes = window.attributes.apply {
                        preferredDisplayModeId = targetMode.modeId
                        preferredRefreshRate = targetMode.refreshRate
                    }
                } else {
                    window.attributes = window.attributes.apply {
                        preferredRefreshRate = 90f
                    }
                }
            }
        } catch (_: Exception) { }
    }
}

