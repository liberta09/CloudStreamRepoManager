package com.kaan.cloudstreamrepomanager

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class CentralRepoApiManager {

    companion object {
        private const val CENTRAL_REPO_URL =
            "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/central_repos.json"

        private const val GITHUB_CONTENTS_API =
            "https://api.github.com/repos/liberta09/CloudStreamRepoManager/contents/central_repos.json"

        /**
         * GitHub üzerindeki merkezi repo.json dosyasından canlı kataloğu çeker.
         * CDN önbellek (Cache) gecikmesini 0 saniyeye indirmek için doğrudan GitHub REST Contents API
         * ve yerel ortak paylaşım dosyası öncelikli olarak kullanılır.
         */
        fun fetchCentralRepos(
            context: Context,
            token: String = "",
            onResult: (List<Repo>?) -> Unit
        ) {
            Thread {
                try {
                    // 1. Öncelik: DİREKT GITHUB CONTENTS API (0 Saniye CDN Gecikmesi)
                    val apiRepos = fetchFromGitHubApiDirect(token)
                    if (apiRepos != null && apiRepos.isNotEmpty()) {
                        saveSharedLocalRepos(context, apiRepos)
                        Handler(Looper.getMainLooper()).post {
                            onResult(apiRepos)
                        }
                        return@Thread
                    }

                    // 2. Öncelik: YEREL ORTAK SENKRON DOSYASI (Aynı cihazda Admin <-> Kullanıcı testleri için)
                    val sharedLocalRepos = loadSharedLocalRepos(context)
                    if (sharedLocalRepos != null && sharedLocalRepos.isNotEmpty()) {
                        Handler(Looper.getMainLooper()).post {
                            onResult(sharedLocalRepos)
                        }
                        return@Thread
                    }

                    // 3. Öncelik: RAW GITHUB CDN
                    val cacheBustUrl = "$CENTRAL_REPO_URL?nocache=${System.currentTimeMillis()}"
                    val headers = mutableMapOf(
                        "Cache-Control" to "no-cache, no-store, must-revalidate",
                        "Pragma" to "no-cache"
                    )
                    if (token.isNotBlank()) {
                        headers["Authorization"] = "Bearer $token"
                    }

                    val result = NetworkUtils.openFollowRedirectsConnection(
                        initialUrl = cacheBustUrl,
                        headers = headers
                    )

                    if (result.isSuccess && result.body.isNotBlank()) {
                        val repos = jsonToRepos(result.body)
                        saveSharedLocalRepos(context, repos)
                        Handler(Looper.getMainLooper()).post {
                            onResult(repos)
                        }
                    } else {
                        val fallbackRepos = loadReposFromAssets(context)
                        Handler(Looper.getMainLooper()).post {
                            onResult(fallbackRepos)
                        }
                    }
                } catch (_: Exception) {
                    val sharedLocalRepos = loadSharedLocalRepos(context)
                    val fallbackRepos = sharedLocalRepos ?: loadReposFromAssets(context)
                    Handler(Looper.getMainLooper()).post {
                        onResult(fallbackRepos)
                    }
                }
            }.start()
        }

        private fun fetchFromGitHubApiDirect(token: String): List<Repo>? {
            return try {
                val headers = mutableMapOf<String, String>()
                if (token.isNotBlank()) {
                    headers["Authorization"] = "Bearer $token"
                }

                val result = NetworkUtils.openFollowRedirectsConnection(
                    initialUrl = GITHUB_CONTENTS_API,
                    headers = headers
                )

                if (result.isSuccess && result.body.isNotBlank()) {
                    val json = JSONObject(result.body)
                    val base64Content = json.optString("content", "").replace("\n", "").replace("\r", "").trim()
                    if (base64Content.isNotBlank()) {
                        val decodedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Base64.getDecoder().decode(base64Content)
                        } else {
                            android.util.Base64.decode(base64Content, android.util.Base64.DEFAULT)
                        }
                        val decodedJson = String(decodedBytes, Charsets.UTF_8)
                        jsonToRepos(decodedJson)
                    } else null
                } else null
            } catch (_: Exception) {
                null
            }
        }

        /**
         * GitHub üzerindeki repo.json kataloğunda yeni versiyon/güncelleme var mı kontrol eder.
         */
        fun checkForRepoCatalogUpdates(
            context: Context,
            currentRepos: List<Repo>,
            onResult: (Boolean, List<Repo>?, String) -> Unit
        ) {
            fetchCentralRepos(context) { liveRepos ->
                if (liveRepos != null) {
                    val currentUrls = currentRepos.map { it.url.trim().lowercase() }.toSet()
                    val liveUrls = liveRepos.map { it.url.trim().lowercase() }.toSet()

                    val isDifferent = currentUrls != liveUrls || currentRepos.size != liveRepos.size

                    if (isDifferent) {
                        onResult(true, liveRepos, "🚀 Merkezi repository kataloğunda yeni güncellemeler mevcut!")
                    } else {
                        onResult(false, null, "✅ Repository listeniz en güncel durumda.")
                    }
                } else {
                    onResult(false, null, "⚠️ Güncelleme sunucusuna bağlanılamadı.")
                }
            }
        }

        /**
         * Admin yetkisiyle güncellenmiş repo listesini GitHub REST API üzerinden
         * SHA çakışması kontrolü ve Bearer Token yetkilendirmesiyle yayınlar.
         */
        fun publishCentralReposToCloud(
            context: Context,
            repos: List<Repo>,
            githubToken: String = "",
            onResult: (Boolean, String) -> Unit
        ) {
            Thread {
                var connection: HttpURLConnection? = null
                try {
                    val adminAuthManager = AdminAuthManager(context)
                    val activeToken = githubToken.ifBlank { adminAuthManager.adminGithubToken }

                    // Her durumda yerel ortak senkron dosyasına kaydet
                    saveSharedLocalRepos(context, repos)
                    saveRepos(context, repos)

                    if (activeToken.isBlank()) {
                        Handler(Looper.getMainLooper()).post {
                            onResult(
                                false,
                                "⚠️ GitHub Token Tanımlı Değil! Değişiklikler bu cihaza kaydedildi ancak GitHub'a yayınlamak için Admin Panelinde Token giriniz."
                            )
                        }
                        return@Thread
                    }

                    val jsonContent = reposToJson(repos)

                    // 1. Dosyanın güncel SHA değerini al
                    val sha = getGitHubFileSha(activeToken)

                    // 2. Güncel JSON içeriğini Base64 formatına dönüştür
                    val encodedContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Base64.getEncoder().encodeToString(jsonContent.toByteArray(Charsets.UTF_8))
                    } else {
                        android.util.Base64.encodeToString(jsonContent.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
                    }

                    val payload = JSONObject().apply {
                        put("message", "👑 Admin: Updated central repository catalog (${repos.size} repos)")
                        put("content", encodedContent)
                        if (sha.isNotBlank()) {
                            put("sha", sha)
                        }
                        put("branch", "main")
                    }

                    val url = URL(GITHUB_CONTENTS_API)
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "PUT"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.doOutput = true
                    connection.setRequestProperty("User-Agent", NetworkUtils.DEFAULT_USER_AGENT)
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    connection.setRequestProperty("Authorization", "Bearer $activeToken")

                    connection.outputStream.use { os ->
                        os.write(payload.toString().toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    if (responseCode in 200..299) {
                        saveRepos(context, repos)
                        Handler(Looper.getMainLooper()).post {
                            onResult(true, "✅ Değişiklikler GitHub repository dosyasına (central_repos.json) başarıyla yayınlandı! SHA: ${sha.take(7)}")
                        }
                    } else if (responseCode == 409) {
                        Handler(Looper.getMainLooper()).post {
                            onResult(false, "⚠️ SHA Çakışması! Lütfen güncelleyip tekrar deneyin.")
                        }
                    } else if (responseCode == 401 || responseCode == 403) {
                        Handler(Looper.getMainLooper()).post {
                            onResult(false, "❌ Yetkisiz Erişim (HTTP $responseCode)! Girdiğiniz GitHub Token 'repo' yazma iznine sahip olmalıdır.")
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            onResult(false, "⚠️ GitHub Sunucu Yanıtı: HTTP $responseCode")
                        }
                    }

                } catch (e: Exception) {
                    saveRepos(context, repos)
                    Handler(Looper.getMainLooper()).post {
                        onResult(false, "⚠️ Hata oluştu: ${e.localizedMessage}")
                    }
                } finally {
                    connection?.disconnect()
                }
            }.start()
        }

        private fun getGitHubFileSha(token: String): String {
            return try {
                val headers = mutableMapOf<String, String>()
                if (token.isNotBlank()) {
                    headers["Authorization"] = "Bearer $token"
                }

                val result = NetworkUtils.openFollowRedirectsConnection(
                    initialUrl = GITHUB_CONTENTS_API,
                    headers = headers
                )

                if (result.isSuccess && result.body.isNotBlank()) {
                    val json = JSONObject(result.body)
                    json.optString("sha", "")
                } else ""
            } catch (_: Exception) {
                ""
            }
        }

        fun saveSharedLocalRepos(context: Context, repos: List<Repo>) {
            try {
                val sharedDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!sharedDir.exists()) sharedDir.mkdirs()
                val sharedFile = File(sharedDir, "cs_repo_manager_shared_catalog.json")
                val json = reposToJson(repos)
                sharedFile.writeText(json, Charsets.UTF_8)
            } catch (_: Exception) {}
        }

        fun loadSharedLocalRepos(context: Context): List<Repo>? {
            return try {
                val sharedDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val sharedFile = File(sharedDir, "cs_repo_manager_shared_catalog.json")
                if (sharedFile.exists()) {
                    val json = sharedFile.readText(Charsets.UTF_8)
                    jsonToRepos(json)
                } else null
            } catch (_: Exception) {
                null
            }
        }

        private fun loadReposFromAssets(context: Context): List<Repo> {
            return try {
                val json = context.assets.open("central_repos.json").bufferedReader().use { it.readText() }
                jsonToRepos(json)
            } catch (_: Exception) {
                getDefaultRepos()
            }
        }
    }
}
