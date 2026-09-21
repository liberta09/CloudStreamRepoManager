package com.kaan.cloudstreamrepomanager

import android.R
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersionTag: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val htmlUrl: String
)

class AppUpdateManager {

    companion object {
        private const val GITHUB_RELEASE_API =
            "https://api.github.com/repos/liberta09/CloudStreamRepoManager/releases/latest"

        /**
         * GitHub API üzerinden uygulamanın güncel sürümünü kontrol eder.
         */
        fun checkForUpdates(
            currentVersion: String,
            onResult: (AppUpdateInfo?) -> Unit
        ) {
            Thread {
                try {
                    val result = NetworkUtils.openFollowRedirectsConnection(
                        initialUrl = GITHUB_RELEASE_API,
                        headers = mapOf("Accept" to "application/vnd.github.v3+json")
                    )

                    if (result.isSuccess && result.body.isNotBlank()) {
                        val json = JSONObject(result.body)

                        val latestTag = json.optString("tag_name", "").removePrefix("v").trim()
                        val releaseNotes = json.optString("body", "Yeni özellikler ve hata düzeltmeleri içerir.")
                        val htmlUrl = json.optString("html_url", "https://github.com/liberta09/CloudStreamRepoManager/releases")

                        var downloadUrl = ""
                        val assets = json.optJSONArray("assets")
                        if (assets != null && assets.length() > 0) {
                            val isAdminMode = BuildConfig.ENABLE_ADMIN_PANEL
                            var fallbackUrl = ""
                            for (i in 0 until assets.length()) {
                                val asset = assets.getJSONObject(i)
                                val assetName = asset.optString("name", "").lowercase()
                                val assetUrl = NetworkUtils.sanitizeUrl(asset.optString("browser_download_url", ""))
                                if (assetName.endsWith(".apk")) {
                                    if (fallbackUrl.isBlank()) fallbackUrl = assetUrl

                                    if (isAdminMode && (assetName.contains("admin") || assetName.contains("manager_admin"))) {
                                        downloadUrl = assetUrl
                                        break
                                    } else if (!isAdminMode && (assetName.contains("user") || assetName.contains("kullanici") || assetName.contains("manager_user"))) {
                                        downloadUrl = assetUrl
                                        break
                                    }
                                }
                            }
                            if (downloadUrl.isBlank()) {
                                downloadUrl = fallbackUrl
                            }
                        }

                        if (downloadUrl.isBlank()) {
                            downloadUrl = htmlUrl
                        }

                        val isUpdate = isVersionGreater(latestTag, currentVersion)

                        val info = AppUpdateInfo(
                            isUpdateAvailable = isUpdate,
                            latestVersionTag = latestTag,
                            releaseNotes = releaseNotes,
                            downloadUrl = NetworkUtils.sanitizeUrl(downloadUrl),
                            htmlUrl = NetworkUtils.sanitizeUrl(htmlUrl)
                        )

                        Handler(Looper.getMainLooper()).post {
                            onResult(info)
                        }
                    } else {
                        val defaultInfo = AppUpdateInfo(
                            isUpdateAvailable = false,
                            latestVersionTag = currentVersion,
                            releaseNotes = "Uygulamanız en güncel sürümde.",
                            downloadUrl = "https://github.com/liberta09/CloudStreamRepoManager/releases",
                            htmlUrl = "https://github.com/liberta09/CloudStreamRepoManager"
                        )
                        Handler(Looper.getMainLooper()).post {
                            onResult(defaultInfo)
                        }
                    }

                } catch (_: Exception) {
                    val defaultInfo = AppUpdateInfo(
                        isUpdateAvailable = false,
                        latestVersionTag = currentVersion,
                        releaseNotes = "Uygulamanız en güncel sürümde.",
                        downloadUrl = "https://github.com/liberta09/CloudStreamRepoManager/releases",
                        htmlUrl = "https://github.com/liberta09/CloudStreamRepoManager"
                    )
                    Handler(Looper.getMainLooper()).post {
                        onResult(defaultInfo)
                    }
                }
            }.start()
        }

        /**
         * Android 8.0+ için Bilinmeyen Kaynaklardan Yükleme İznini kontrol eder ve gerekirse yönlendirir.
         */
        fun checkInstallPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    Toast.makeText(
                        context,
                        "Güncellemeyi kurabilmek için lütfen bilinmeyen kaynaklardan uygulama yükleme iznini verin.",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    return false
                }
            }
            return true
        }

        /**
         * Yeni sürüm APK'sını indirir, ekranda % olarak Progress Dialog gösterir ve indirme bittiğinde
         * doğrudan Android Paket Yükleyici (Package Installer) ekranını açar.
         */
        fun downloadAndInstallUpdate(context: Context, rawDownloadUrl: String) {
            try {
                if (!checkInstallPermission(context)) {
                    return
                }

                val downloadUrl = NetworkUtils.sanitizeUrl(rawDownloadUrl)

                if (downloadUrl.contains(".apk", ignoreCase = true)) {
                    val apkFileName = "CloudStreamRepoManager_Update.apk"
                    val destinationFile = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        apkFileName
                    )

                    if (destinationFile.exists()) {
                        destinationFile.delete()
                    }

                    // --- Progress Dialog UI ---
                    val progressLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(60, 40, 60, 40)
                    }

                    val titleText = TextView(context).apply {
                        text = "⚡ Güncelleme İndiriliyor..."
                        textSize = 16f
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(Color.WHITE)
                    }

                    val progressBar = ProgressBar(
                        context,
                        null,
                        R.attr.progressBarStyleHorizontal
                    ).apply {
                        isIndeterminate = false
                        max = 100
                        progress = 0
                        setPadding(0, 30, 0, 20)
                    }

                    val percentText = TextView(context).apply {
                        text = "%0 indirildi..."
                        textSize = 13f
                        setTextColor(Color.LTGRAY)
                    }

                    progressLayout.addView(titleText)
                    progressLayout.addView(progressBar)
                    progressLayout.addView(percentText)

                    val progressDialog = AlertDialog.Builder(context)
                        .setView(progressLayout)
                        .setCancelable(false)
                        .create()

                    try {
                        progressDialog.show()
                    } catch (_: Exception) {}

                    val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                        setTitle("CloudStream Repo Manager Güncellemesi")
                        setDescription("Yeni sürüm indiriliyor...")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationUri(Uri.fromFile(destinationFile))
                        setMimeType("application/vnd.android.package-archive")
                    }

                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val downloadId = downloadManager.enqueue(request)

                    // --- İndirme Yüzdesi Takip Handler ---
                    val handler = Handler(Looper.getMainLooper())
                    val progressRunnable = object : Runnable {
                        override fun run() {
                            try {
                                val query = DownloadManager.Query().setFilterById(downloadId)
                                val cursor = downloadManager.query(query)
                                if (cursor != null && cursor.moveToFirst()) {
                                    val bytesDownloadedIndex =
                                        cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                                    val bytesTotalIndex =
                                        cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                    val statusIndex =
                                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                                    val bytesDownloaded =
                                        if (bytesDownloadedIndex >= 0) cursor.getInt(bytesDownloadedIndex) else 0
                                    val bytesTotal =
                                        if (bytesTotalIndex >= 0) cursor.getInt(bytesTotalIndex) else 0
                                    val status =
                                        if (statusIndex >= 0) cursor.getInt(statusIndex) else 0

                                    if (bytesTotal > 0) {
                                        val progress = ((bytesDownloaded * 100L) / bytesTotal).toInt()
                                        progressBar.progress = progress
                                        percentText.text = "%$progress indirildi (${bytesDownloaded / 1024} KB / ${bytesTotal / 1024} KB)"
                                    }

                                    cursor.close()

                                    if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                                        return
                                    }
                                } else {
                                    cursor?.close()
                                }
                            } catch (_: Exception) {}

                            handler.postDelayed(this, 300)
                        }
                    }
                    handler.post(progressRunnable)

                    // --- BroadcastReceiver on Download Complete ---
                    val onCompleteReceiver = object : BroadcastReceiver() {
                        override fun onReceive(recvContext: Context?, intent: Intent?) {
                            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                                if (id == downloadId) {
                                    try {
                                        handler.removeCallbacks(progressRunnable)
                                    } catch (_: Exception) {}

                                    try {
                                        context.applicationContext.unregisterReceiver(this)
                                    } catch (_: Exception) {}

                                    try {
                                        if (progressDialog.isShowing) {
                                            progressDialog.dismiss()
                                        }
                                    } catch (_: Exception) {}

                                    promptInstallApk(context, destinationFile)
                                }
                            }
                        }
                    }

                    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    ContextCompat.registerReceiver(
                        context.applicationContext,
                        onCompleteReceiver,
                        filter,
                        ContextCompat.RECEIVER_EXPORTED
                    )

                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            } catch (_: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawDownloadUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }

        /**
         * İndirilen APK dosyasını FileProvider ile güvenli şekilde Paket Yükleyiciye (Package Installer) iletir.
         */
        fun promptInstallApk(context: Context, apkFile: File) {
            try {
                if (!apkFile.exists()) {
                    Toast.makeText(context, "Güncelleme dosyası bulunamadı", Toast.LENGTH_SHORT).show()
                    return
                }

                val apkUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                )

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(apkUri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                context.startActivity(installIntent)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Kurulum ekranı açılamadı: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun isVersionGreater(latest: String, current: String): Boolean {
            if (latest.isBlank() || current.isBlank()) return false
            return try {
                val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
                val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }

                val maxLen = maxOf(latestParts.size, currentParts.size)
                for (i in 0 until maxLen) {
                    val l = latestParts.getOrElse(i) { 0 }
                    val c = currentParts.getOrElse(i) { 0 }
                    if (l > c) return true
                    if (l < c) return false
                }
                false
            } catch (_: Exception) {
                latest != current
            }
        }
    }
}
