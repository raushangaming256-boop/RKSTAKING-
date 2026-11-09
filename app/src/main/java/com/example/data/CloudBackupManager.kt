package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class CloudBackupManager(private val context: Context, private val repository: KhataRepository) {
    
    private val sharedPrefs by lazy { context.getSharedPreferences("google_cloud_sync_prefs", Context.MODE_PRIVATE) }
    private val moshi by lazy { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }
    private val payloadAdapter by lazy { moshi.adapter(LedgerBackupPayload::class.java) }

    // Check if auto-backup is enabled
    fun isAutoBackupEnabled(): Boolean {
        return sharedPrefs.getBoolean("auto_backup_enabled", true)
    }

    fun setAutoBackupEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("auto_backup_enabled", enabled).apply()
    }

    fun getLastBackupTimestamp(): Long {
        return sharedPrefs.getLong("last_local_backup_timestamp", 0L)
    }

    fun setLastBackupTimestamp(timestamp: Long) {
        sharedPrefs.edit().putLong("last_local_backup_timestamp", timestamp).apply()
    }

    fun getLastMidnightBackupTimestamp(): Long {
        return sharedPrefs.getLong("last_midnight_backup_timestamp", 0L)
    }

    fun getMidnightBackupFile(): File? {
        val zipFile = File(File(context.filesDir, "backups"), "OkKhata_Midnight_Backup.zip")
        return if (zipFile.exists()) zipFile else null
    }

    suspend fun performMidnightZipBackup(): Boolean = withContext(Dispatchers.IO) {
        try {
            val customers = repository.allCustomers.first()
            val transactions = repository.allTransactions.first()
            
            val payload = LedgerBackupPayload(
                customers = customers,
                transactions = transactions,
                backupTime = System.currentTimeMillis()
            )
            
            val jsonString = payloadAdapter.toJson(payload)
            val tempJsonFile = File(context.cacheDir, "okkhata_midnight_temp.json")
            FileOutputStream(tempJsonFile).use { output ->
                output.write(jsonString.toByteArray())
            }
            
            val backupsDir = File(context.filesDir, "backups")
            if (!backupsDir.exists()) {
                backupsDir.mkdirs()
            }
            val zipFile = File(backupsDir, "OkKhata_Midnight_Backup.zip")
            
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                FileInputStream(tempJsonFile).use { fis ->
                    val entry = ZipEntry("okkhata_backup.json")
                    zos.putNextEntry(entry)
                    val buffer = ByteArray(2048)
                    var len: Int
                    while (fis.read(buffer).also { len = it } > 0) {
                        zos.write(buffer, 0, len)
                    }
                    zos.closeEntry()
                }
            }
            
            tempJsonFile.delete()
            
            // Record last midnight backup timestamp
            sharedPrefs.edit().putLong("last_midnight_backup_timestamp", System.currentTimeMillis()).apply()
            
            android.util.Log.d("MidnightBackup", "Daily midnight backup completed successfully!")
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    fun shareMidnightBackupZip(activityContext: Context) {
        val zipFile = getMidnightBackupFile()
        if (zipFile == null || !zipFile.exists()) {
            Toast.makeText(context, "No midnight backup found. Please wait for the auto midnight backup or create a manual backup.", Toast.LENGTH_LONG).show()
            return
        }
        try {
            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                zipFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "OkKhata Midnight Backup")
                putExtra(Intent.EXTRA_TEXT, "Aapka OkKhata Daily Midnight Backup ZIP file. Restore easily inside the app.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooserIntent = Intent.createChooser(shareIntent, "Share Midnight Backup ZIP via...")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activityContext.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun getCloudBackupHistoryCount(): Int {
        val file = File(context.filesDir, "okkhata_auto_backup.json")
        if (!file.exists()) return 0
        return try {
            val json = file.readText()
            val payload = payloadAdapter.fromJson(json)
            payload?.customers?.size ?: 0
        } catch (e: Exception) {
            0
        }
    }

    // 1. Automatic local backup (triggered on every database change)
    suspend fun performAutoLocalBackup(): Boolean = withContext(Dispatchers.IO) {
        if (!isAutoBackupEnabled()) return@withContext false
        try {
            val customers = repository.allCustomers.first()
            val transactions = repository.allTransactions.first()
            
            val payload = LedgerBackupPayload(
                customers = customers,
                transactions = transactions,
                backupTime = System.currentTimeMillis()
            )
            
            val jsonString = payloadAdapter.toJson(payload)
            val file = File(context.filesDir, "okkhata_auto_backup.json")
            FileOutputStream(file).use { output ->
                output.write(jsonString.toByteArray())
            }
            
            setLastBackupTimestamp(System.currentTimeMillis())
            android.util.Log.d("LocalBackup", "Automatic ledger backup completed successfully!")
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    // 2. Export current database snapshot as a ZIP file and share it
    suspend fun exportAndShareBackupZip(activityContext: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get latest data
            val customers = repository.allCustomers.first()
            val transactions = repository.allTransactions.first()
            
            val payload = LedgerBackupPayload(
                customers = customers,
                transactions = transactions,
                backupTime = System.currentTimeMillis()
            )
            
            val jsonString = payloadAdapter.toJson(payload)
            
            // Write temporary JSON file
            val tempJsonFile = File(context.cacheDir, "okkhata_backup.json")
            FileOutputStream(tempJsonFile).use { output ->
                output.write(jsonString.toByteArray())
            }
            
            // Generate zip file name with date suffix for clean organization
            val dateFormat = SimpleDateFormat("dd_MMM_yyyy_HH_mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date())
            val zipFile = File(context.cacheDir, "OkKhata_Backup_$dateStr.zip")
            
            // Compress JSON into ZIP
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                FileInputStream(tempJsonFile).use { fis ->
                    val entry = ZipEntry("okkhata_backup.json")
                    zos.putNextEntry(entry)
                    val buffer = ByteArray(2048)
                    var len: Int
                    while (fis.read(buffer).also { len = it } > 0) {
                        zos.write(buffer, 0, len)
                    }
                    zos.closeEntry()
                }
            }
            
            // Delete temporary JSON file
            tempJsonFile.delete()
            
            // Share ZIP using FileProvider
            withContext(Dispatchers.Main) {
                try {
                    val fileUri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        zipFile
                    )
                    
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/zip"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        putExtra(Intent.EXTRA_SUBJECT, "OkKhata Ledger Backup ZIP")
                        putExtra(Intent.EXTRA_TEXT, "OkKhata App data backup file. Select 'Restore Backup' in OkKhata App to import this backup.")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    
                    val chooserIntent = Intent.createChooser(shareIntent, "Share Backup ZIP via...")
                    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activityContext.startActivity(chooserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            
            // Log backup event
            setLastBackupTimestamp(System.currentTimeMillis())
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    // 2.5 Export current database snapshot as a ZIP file and save/download directly to device storage Downloads folder
    suspend fun exportAndSaveBackupToDownloads(activityContext: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get latest data
            val customers = repository.allCustomers.first()
            val transactions = repository.allTransactions.first()
            
            val payload = LedgerBackupPayload(
                customers = customers,
                transactions = transactions,
                backupTime = System.currentTimeMillis()
            )
            
            val jsonString = payloadAdapter.toJson(payload)
            
            // Write temporary JSON file
            val tempJsonFile = File(context.cacheDir, "okkhata_backup.json")
            FileOutputStream(tempJsonFile).use { output ->
                output.write(jsonString.toByteArray())
            }
            
            // Generate zip file name with date suffix for clean organization
            val dateFormat = SimpleDateFormat("dd_MMM_yyyy_HH_mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date())
            val fileName = "OkKhata_Backup_$dateStr.zip"
            
            var success = false
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                
                val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { os ->
                        ZipOutputStream(os).use { zos ->
                            FileInputStream(tempJsonFile).use { fis ->
                                val entry = ZipEntry("okkhata_backup.json")
                                zos.putNextEntry(entry)
                                val buffer = ByteArray(2048)
                                var len: Int
                                while (fis.read(buffer).also { len = it } > 0) {
                                    zos.write(buffer, 0, len)
                                }
                                zos.closeEntry()
                            }
                        }
                    }
                    success = true
                }
            } else {
                // Legacy Android versions (below Q)
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val destFile = File(downloadsDir, fileName)
                FileOutputStream(destFile).use { fos ->
                    ZipOutputStream(fos).use { zos ->
                        FileInputStream(tempJsonFile).use { fis ->
                            val entry = ZipEntry("okkhata_backup.json")
                            zos.putNextEntry(entry)
                            val buffer = ByteArray(2048)
                            var len: Int
                            while (fis.read(buffer).also { len = it } > 0) {
                                zos.write(buffer, 0, len)
                            }
                            zos.closeEntry()
                        }
                    }
                }
                success = true
            }
            
            // Clean up temp json
            tempJsonFile.delete()
            
            if (success) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Downloaded to Downloads folder: $fileName", Toast.LENGTH_LONG).show()
                }
                // Log backup event
                setLastBackupTimestamp(System.currentTimeMillis())
            }
            
            return@withContext success
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
            return@withContext false
        }
    }

    // 3. Restore database snapshot from a selected ZIP file URI
    suspend fun restoreFromBackupZipUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return@withContext false
            
            val tempExtractedJson = File(context.cacheDir, "temp_restored_backup.json")
            
            // Extract JSON from ZipInputStream
            var successExtract = false
            ZipInputStream(inputStream).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (entry.name == "okkhata_backup.json" || entry.name.endsWith(".json")) {
                        FileOutputStream(tempExtractedJson).use { fos ->
                            val buffer = ByteArray(2048)
                            var len: Int
                            while (zis.read(buffer).also { len = it } > 0) {
                                fos.write(buffer, 0, len)
                            }
                        }
                        zis.closeEntry()
                        successExtract = true
                        break
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            
            if (!successExtract || !tempExtractedJson.exists()) {
                android.util.Log.e("LocalBackup", "Failed to extract valid json file from zip.")
                return@withContext false
            }
            
            // Read JSON
            val jsonString = tempExtractedJson.readText()
            tempExtractedJson.delete() // Clean up cache
            
            val payload = payloadAdapter.fromJson(jsonString) ?: return@withContext false
            
            // Drop old tables and repopulate with backup payload safely
            repository.deleteAllData()
            
            payload.customers.forEach { cust ->
                repository.insertCustomer(cust)
            }
            payload.transactions.forEach { trans ->
                repository.insertTransaction(trans)
            }
            
            // Update last local backup timestamp
            setLastBackupTimestamp(System.currentTimeMillis())
            
            // Also write to local auto backup file for safety
            val file = File(context.filesDir, "okkhata_auto_backup.json")
            FileOutputStream(file).use { output ->
                output.write(jsonString.toByteArray())
            }
            
            android.util.Log.d("LocalBackup", "Ledger successfully restored from ZIP!")
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    fun scheduleDailyMidnightBackup() {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        
        var targetTime = calendar.timeInMillis
        if (targetTime <= System.currentTimeMillis()) {
            targetTime += 24 * 60 * 60 * 1000L // Next day midnight
        }
        
        val initialDelay = targetTime - System.currentTimeMillis()
        
        val backupRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.worker.DailyBackupWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
        .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
        .setConstraints(
            androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.NOT_REQUIRED)
                .build()
        )
        .build()

        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyMidnightBackupWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )
    }

    /**
     * Delete the previous shared / exported backup (1 step back)
     */
    fun deletePreviousSharedBackup(): Pair<Boolean, String> {
        return try {
            val cacheFiles = context.cacheDir.listFiles { file ->
                file.name.startsWith("OkKhata_Backup_") || file.name == "okkhata_backup.json" || file.name.endsWith(".zip")
            }?.sortedByDescending { it.lastModified() }

            if (!cacheFiles.isNullOrEmpty()) {
                val latest = cacheFiles.first()
                val name = latest.name
                val deleted = latest.delete()
                if (deleted) {
                    Pair(true, "पिछला शेयर बैकअप हटा दिया गया: $name (1 स्टेप पीछे)")
                } else {
                    Pair(false, "फ़ाइल डिलीट नहीं हो सकी")
                }
            } else {
                val backupsDir = File(context.filesDir, "backups")
                val files = backupsDir.listFiles()?.sortedByDescending { it.lastModified() }
                if (!files.isNullOrEmpty()) {
                    val target = files.first()
                    val name = target.name
                    target.delete()
                    Pair(true, "बैकअप फ़ाइल डिलीट की गई: $name")
                } else {
                    Pair(false, "हटाने के लिए कोई पुराना शेयर बैकअप नहीं मिला")
                }
            }
        } catch (e: Exception) {
            Pair(false, "एरर: ${e.message}")
        }
    }
}

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class LedgerBackupPayload(
    val customers: List<CustomerEntity>,
    val transactions: List<TransactionEntity>,
    val backupTime: Long
)
