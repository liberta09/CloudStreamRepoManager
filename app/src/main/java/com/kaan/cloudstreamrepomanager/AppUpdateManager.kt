package com.kaan.cloudstreamrepomanager

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.json.JSONObject
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
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(GITHUB_RELEASE_API)
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("User-Agent", "CloudStream-Repo-Manager")
                    connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

                    if (connection.responseCode in 200..299) {
                        val body = connection.inputStream.bufferedReader().use { it.readText() }
                        val json = JSONObject(body)

                        val latestTag = json.optString("tag_name", "").removePrefix("v").trim()
                        val releaseNotes = json.optString("body", "Yeni özellikler ve hata düzeltmeleri içerir.")
                        val htmlUrl = json.optString("html_url", "https://github.com/liberta09/CloudStreamRepoManager/releases")

                        var downloadUrl = ""
                        val assets = json.optJSONArray("assets")
                        if (assets != null && assets.length() > 0) {
                            for (i in 0 until assets.length()) {
                                val asset = assets.getJSONObject(i)
                                val assetName = asset.optString("name", "").lowercase()
                                if (assetName.endsWith(".apk")) {
                                    downloadUrl = asset.optString("browser_download_url", "")
                                    break
                                }
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
                            downloadUrl = downloadUrl,
                            htmlUrl = htmlUrl
                        )

                        Handler(Looper.getMainLooper()).post {
                            onResult(info)
                        }
                    } else {
                        // GitHub üzerinde henüz Release tag oluşturulmamışsa varsayılan olarak güncel kabul et
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
                } finally {
                    connection?.disconnect()
                }
            }.start()
        }

        /**
         * Yeni sürüm APK'sını indirir veya tarayıcıda indirme sayfasını açar.
         */
        fun downloadAndInstallUpdate(context: Context, downloadUrl: String) {
            try {
                if (downloadUrl.endsWith(".apk")) {
                    val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                        setTitle("CloudStream Repo Manager Güncellemesi")
                        setDescription("Yeni sürüm indiriliyor...")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            "CloudStreamRepoManager_Update.apk"
                        )
                        setMimeType("application/vnd.android.package-archive")
                    }

                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)

                    Toast.makeText(
                        context,
                        "Güncelleme indiriliyor. Tamamlandığında bildirim panelinden tıklayıp kurabilirsiniz.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            } catch (_: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
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
