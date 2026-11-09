package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.KhataDatabase
import com.example.data.KhataRepository
import com.example.data.CloudBackupManager

class DailyBackupWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = KhataDatabase.getDatabase(applicationContext)
            val repository = KhataRepository(database.khataDao())
            val backupManager = CloudBackupManager(applicationContext, repository)

            // Perform the daily midnight zip backup
            val success = backupManager.performMidnightZipBackup()

            if (success) {
                showBackupNotification()
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showBackupNotification() {
        val channelId = "midnight_backup_channel"
        
        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is not in the Support Library
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Midnight Backup Notifications"
            val descriptionText = "Notifications for automatic midnight ledger backups"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = android.content.Intent(applicationContext, com.example.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_backup_hub", true)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            applicationContext,
            1001,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Midnight Backup Completed! 💼")
            .setContentText("Aapka daily midnight backup ZIP safely create ho gaya hai. Tap to share/upload.")
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle()
                .bigText("Aapka daily midnight backup ZIP safely create ho gaya hai. Tap to share or upload to Google Drive, WhatsApp or Email."))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = androidx.core.app.NotificationManagerCompat.from(applicationContext)
        try {
            if (android.os.Build.VERSION.SDK_INT < 33 ||
                androidx.core.content.ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(1002, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
