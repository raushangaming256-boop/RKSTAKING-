package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppUpdateInfo
import com.example.data.UpdateStatus
import com.example.viewmodel.KhataViewModel

@Composable
fun AppUpdateDialog(
    viewModel: KhataViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val updateStatus by viewModel.updateStatus.collectAsStateWithLifecycle()
    val updateInfo by viewModel.currentUpdateInfo.collectAsStateWithLifecycle()
    val apkBackupInfo by viewModel.appApkBackupInfo.collectAsStateWithLifecycle()
    val isBackingUpApk by viewModel.isBackingUpApk.collectAsStateWithLifecycle()

    var selectedDialogTab by remember { mutableStateOf(0) }

    val currentVersion = viewModel.appUpdateManager.currentVersionName
    val currentBuildCode = viewModel.appUpdateManager.currentVersionCode

    var showConfigEditor by remember { mutableStateOf(false) }

    // Editor fields
    var editVersionName by remember(updateInfo) {
        mutableStateOf(viewModel.appUpdateManager.getTargetVersionName())
    }
    var editVersionCode by remember(updateInfo) {
        mutableStateOf(viewModel.appUpdateManager.getTargetVersionCode().toString())
    }
    var editApkUrl by remember(updateInfo) {
        mutableStateOf(viewModel.appUpdateManager.getTargetApkUrl())
    }
    var editServerJsonUrl by remember(updateInfo) {
        mutableStateOf(viewModel.appUpdateManager.getServerJsonUrl())
    }
    var editChangelog by remember(updateInfo) {
        mutableStateOf(viewModel.appUpdateManager.getTargetChangelog())
    }
    var editSizeMb by remember(updateInfo) {
        mutableStateOf(viewModel.appUpdateManager.getTargetSizeMb().toString())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp)
                .testTag("app_update_dialog_box"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f))
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
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
                                Text(
                                    text = "ऑनलाइन ऐप अपडेट",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Online APK Auto-Updater",
                                    fontSize = 11.5.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("close_update_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Tab selector between Online Update and App APK Backup (No Data)
                    TabRow(
                        selectedTabIndex = selectedDialogTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedDialogTab == 0,
                            onClick = { selectedDialogTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("ऑनलाइन अपडेट", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        )
                        Tab(
                            selected = selectedDialogTab == 1,
                            onClick = { selectedDialogTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("ऐप APK बैकअप", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        )
                    }

                    // Scrollable content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (selectedDialogTab == 0) {
                            // Current vs Latest Version Comparison Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Installed version
                                Column {
                                    Text("इंस्टॉल्ड वर्शन (Current)", fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "v$currentVersion (b$currentBuildCode)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("डिवाइस में चालू", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                Text("➔", fontSize = 18.sp, color = Color.Gray)

                                // Target/Latest version
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("लेटेस्ट अपडेट (Latest)", fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "v${updateInfo?.versionName ?: "4.0"}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = if (updateInfo?.isUpdateAvailable == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (updateInfo?.isUpdateAvailable == true)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else Color.Gray.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (updateInfo?.isUpdateAvailable == true) "🚀 New Available" else "✅ Up To Date",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (updateInfo?.isUpdateAvailable == true) MaterialTheme.colorScheme.primary else Color.Gray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Status / Action Banner based on UpdateStatus
                        when (val status = updateStatus) {
                            is UpdateStatus.Checking -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.5.dp
                                        )
                                        Text("सर्वर से अपडेट जाँचा जा रहा है...", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            is UpdateStatus.Downloading -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CloudDownload,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text("जस्ट सेकंड में APK डाउनलोड हो रहा है...", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text("${status.progressPercent}%", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                        }

                                        LinearProgressIndicator(
                                            progress = { status.progressPercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            val downloadedMb = status.downloadedBytes / (1024.0 * 1024.0)
                                            val totalMb = status.totalBytes / (1024.0 * 1024.0)
                                            Text(
                                                String.format("%.1f MB / %.1f MB", downloadedMb, totalMb),
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                                Text(String.format("%.1f MB/s", status.speedMbps), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }

                            is UpdateStatus.DownloadCompleted -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF008955).copy(alpha = 0.15f)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF008955).copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF008955),
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Column {
                                                Text(
                                                    "APK Download फोल्डर में सेव हो गया! 🎉",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.5.sp,
                                                    color = Color(0xFF008955)
                                                )
                                                Text(
                                                    "वर्शन v${status.versionName} • साइज: ${String.format("%.1f MB", status.sizeMb)}",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        // Folder path display
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Folder,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "लोकेशन: आंतरिक मेमोरी / Download",
                                                        fontSize = 10.sp,
                                                        color = Color.Gray
                                                    )
                                                    Text(
                                                        text = status.downloadLocationText,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }

                                        // Primary Action: Install APK now
                                        Button(
                                            onClick = {
                                                viewModel.installDownloadedApk(context, status.apkFile)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .testTag("install_downloaded_apk_btn"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008955)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.InstallMobile, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("अभी इंस्टॉल करें (Install Now)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                        }

                                        // Secondary Actions: Download folder and Share APK
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.openDownloadsFolder(context)
                                                    Toast.makeText(context, "Download फोल्डर खोला जा रहा है 📂", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .testTag("open_downloads_folder_btn"),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary)
                                            ) {
                                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("फ़ोल्डर खोलें 📂", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.shareApk(context, status.apkFile)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .testTag("share_downloaded_apk_btn"),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("शेयर करें 📤", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            is UpdateStatus.Error -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "त्रुटि: ${status.message}",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            else -> {
                                // Idle or UpdateAvailable or UpToDate
                            }
                        }

                        // Changelog / क्या नया है Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NewReleases,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text("क्या नया है (Release Notes):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Text(
                                    text = updateInfo?.releaseNotes ?: "• Fast 90Hz scrolling\n• Cloud backup restore\n• Performance engine",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "पैकेज साइज: ~${updateInfo?.apkSizeMb ?: 16.8} MB • रिलीज: लेटेस्ट",
                                    fontSize = 10.5.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Quick Teaser card to pure App APK Backup (No Data)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDialogTab = 1 }
                                .testTag("switch_to_apk_backup_tab_card"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Backup,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            "ऐप APK बैकअप विकल्प (No Data)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "बिना डेटा केवल ऐप की मूल APK सेव या शेयर करें",
                                            fontSize = 10.5.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    "खोलें ➔",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Action Buttons: Fast Download
                        if (updateStatus !is UpdateStatus.Downloading && updateStatus !is UpdateStatus.DownloadCompleted) {
                            Button(
                                onClick = {
                                    viewModel.startFastApkDownload()
                                    Toast.makeText(context, "APK डाउनलोड शुरू हो रहा है... 🚀", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_fast_download_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (updateInfo?.isUpdateAvailable == true)
                                        "⚡ तुरंत APK डाउनलोड करें (${updateInfo?.apkSizeMb ?: 16.8} MB)"
                                    else
                                        "⚡ दोबारा APK डाउनलोड करें (${updateInfo?.apkSizeMb ?: 16.8} MB)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.checkForAppUpdates(isRemote = true)
                                        Toast.makeText(context, "सर्वर से अपडेट चेक किया जा रहा है...", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).height(40.dp).testTag("check_updates_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("चेक करें", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.openDownloadsFolder(context)
                                        Toast.makeText(context, "Download फोल्डर खोला जा रहा है 📂", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).height(40.dp).testTag("open_downloads_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Downloads", fontSize = 12.sp)
                                }
                            }

                            // Clean/Delete old update cache button
                            OutlinedButton(
                                onClick = {
                                    viewModel.clearOldUpdateCache { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(40.dp).testTag("clear_old_updates_cache_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("पुराना अपडेट कैशे साफ़ करें (Clean Update)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        // "यहाँ से चेंज करे वहाँ अपडेट डाउनलोड करने का ऑप्शन दे दे"
                        // CONFIGURATION & DYNAMIC VERSION CHANGER ACCORDION
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showConfigEditor = !showConfigEditor }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SettingsSuggest,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "⚙️ अपडेट सोर्स व वर्शन बदलें (Live Config)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "यहाँ वर्शन बदलें और तुरंत डाउनलोड ऑप्शन पाएँ",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (showConfigEditor) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = showConfigEditor,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        "⚡ त्वरित प्रीसेट चुने (Quick Version Presets):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Quick Presets
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    editVersionName = "4.0"
                                                    editVersionCode = "40"
                                                    editChangelog = "• OkKhata v4.0 Official Release\n• 120Hz Liquid Navigation & Speed Boost\n• App APK Pure Backup (No Data)"
                                                }
                                        ) {
                                            Text(
                                                "v4.0 Ultra 🚀",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    editVersionName = "4.1"
                                                    editVersionCode = "41"
                                                    editChangelog = "• Supercharged Turbo Mode\n• AI Smart Ledger Audio\n• 120 FPS Magisk Transitions"
                                                }
                                        ) {
                                            Text(
                                                "v4.1 Beta ⚡",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    editVersionName = "5.0"
                                                    editVersionCode = "50"
                                                    editChangelog = "• Major 5.0 Revolution Release\n• Multi-shop Cloud Engine\n• Instant Soundbox Pro"
                                                }
                                        ) {
                                            Text(
                                                "v5.0 Pro 🔥",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = editVersionName,
                                        onValueChange = { editVersionName = it },
                                        label = { Text("टारगेट वर्शन (Version Name)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    OutlinedTextField(
                                        value = editVersionCode,
                                        onValueChange = { editVersionCode = it },
                                        label = { Text("वर्शन कोड (Version Code)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    OutlinedTextField(
                                        value = editApkUrl,
                                        onValueChange = { editApkUrl = it },
                                        label = { Text("APK डाउनलोड लिंक (Direct Download URL)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    OutlinedTextField(
                                        value = editServerJsonUrl,
                                        onValueChange = { editServerJsonUrl = it },
                                        label = { Text("ऑनलाइन JSON सर्वर लिंक (Server Config URL)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    OutlinedTextField(
                                        value = editChangelog,
                                        onValueChange = { editChangelog = it },
                                        label = { Text("क्या नया है (Changelog Notes)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 4,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Button(
                                        onClick = {
                                            val vCode = editVersionCode.toIntOrNull() ?: 3
                                            val size = editSizeMb.toDoubleOrNull() ?: 16.8
                                            viewModel.saveUpdateConfig(
                                                serverJsonUrl = editServerJsonUrl,
                                                targetVersionName = editVersionName,
                                                targetVersionCode = vCode,
                                                targetApkUrl = editApkUrl,
                                                changelog = editChangelog,
                                                sizeMb = size
                                            )
                                            showConfigEditor = false
                                            Toast.makeText(context, "अपडेट सेटिंग्स सेव हो गई! नया अपडेट डिटेक्ट हुआ ✅", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .testTag("save_update_config_btn"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("💾 सेव करें और अपडेट रिफ्रेश करें", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        // TAB 1: PURE APP APK BACKUP (NO DATA BACKUP - ONLY APK)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Info Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Backup,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = "शुद्ध ऐप APK बैकअप",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "No Data • Only Latest App APK",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Text(
                                        text = "इस विकल्प से बिना किसी ग्राहक या लेन-देन डेटा के केवल ऐप की मूल APK सीधे आपके फ़ोन के 'Download/OkKhata_Backup/' फ़ोल्डर में सुरक्षित सेव हो जाती है। आप इसे कभी भी दोबारा इंस्टॉल कर सकते हैं या किसी भी फ़ोन में शेयर कर सकते हैं।",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                                        lineHeight = 18.sp
                                    )

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                                    // Key security and convenience points
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF008955), modifier = Modifier.size(16.dp))
                                        Text("100% डेटा सुरक्षित: शून्य डेटा बैकअप, सिर्फ ऐप फ़ाइल", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF008955), modifier = Modifier.size(16.dp))
                                        Text("सुपरफास्ट 1 सेकंड में बैकअप तैयार", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Text("पाथ: आंतरिक मेमोरी / Download / OkKhata_Backup /", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                                    }
                                }
                            }

                            // Big Primary Action Button to trigger App APK Backup
                            Button(
                                onClick = {
                                    viewModel.createPureAppApkBackup { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("create_app_apk_backup_btn"),
                                enabled = !isBackingUpApk,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                if (isBackingUpApk) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("APK बैकअप निकाला जा रहा है...", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                } else {
                                    Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ऐप APK बैकअप बनाएं (No Data)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                }
                            }

                            // If backup info exists, display rich action card
                            if (apkBackupInfo != null) {
                                val backup = apkBackupInfo!!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF008955).copy(alpha = 0.08f)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF008955).copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF008955),
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Column {
                                                Text(
                                                    "ऐप APK बैकअप सुरक्षित सेव है! 🎉",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.5.sp,
                                                    color = Color(0xFF008955)
                                                )
                                                Text(
                                                    "${backup.fileName} • ${String.format(java.util.Locale.US, "%.1f MB", backup.sizeMb)}",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Folder,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "लोकेशन: आंतरिक मेमोरी",
                                                        fontSize = 10.sp,
                                                        color = Color.Gray
                                                    )
                                                    Text(
                                                        text = backup.locationPath,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }

                                        // Install backup APK directly
                                        Button(
                                            onClick = {
                                                viewModel.installDownloadedApk(context, backup.backupFile)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .testTag("install_backup_apk_btn"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008955)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.InstallMobile, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("यह APK इंस्टॉल करें (Install)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                viewModel.openDownloadsFolder(context)
                                                Toast.makeText(context, "Download फोल्डर खोला जा रहा है 📂", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                                .testTag("open_backup_folder_btn"),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("फ़ोल्डर खोलें 📂", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                    // Bottom Close
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("बंद करें (Dismiss)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
