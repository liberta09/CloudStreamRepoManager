package com.kaan.cloudstreamrepomanager

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
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
         * Önbellek (CDN Cache) gecikmesini önlemek için timestamp parametresi ve
         * doğrudan GitHub API yedeklemesi kullanılır.
         */
        fun fetchCentralRepos(
            context: Context,
            token: String = "",
            onResult: (List<Repo>?) -> Unit
        ) {
            Thread {
                var connection: HttpURLConnection? = null
                try {
                    // Önbellek bypass timestamp parametresi
                    val cacheBustUrl = "$CENTRAL_REPO_URL?nocache=${System.currentTimeMillis()}"
                    val url = URL(cacheBustUrl)
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("User-Agent", "CloudStream-Repo-Manager")
                    connection.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
                    connection.setRequestProperty("Pragma", "no-cache")
                    if (token.isNotBlank()) {
                        connection.setRequestProperty("Authorization", "Bearer $token")
                    }

                    if (connection.responseCode in 200..299) {
                        val body = connection.inputStream.bufferedReader().use { it.readText() }
                        val repos = jsonToRepos(body)
                        Handler(Looper.getMainLooper()).post {
                            onResult(repos)
                        }
                    } else {
                        val apiRepos = fetchFromGitHubApiDirect(token)
                        val fallbackRepos = apiRepos ?: loadReposFromAssets(context)
                        Handler(Looper.getMainLooper()).post {
                            onResult(fallbackRepos)
                        }
                    }
                } catch (_: Exception) {
                    val apiRepos = fetchFromGitHubApiDirect(token)
                    val fallbackRepos = apiRepos ?: loadReposFromAssets(context)
                    Handler(Looper.getMainLooper()).post {
                        onResult(fallbackRepos)
                    }
                } finally {
                    connection?.disconnect()
                }
            }.start()
        }

        private fun fetchFromGitHubApiDirect(token: String): List<Repo>? {
            var connection: HttpURLConnection? = null
            return try {
                val url = URL(GITHUB_CONTENTS_API)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.setRequestProperty("User-Agent", "CloudStream-Repo-Manager")
                if (token.isNotBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }

                if (connection.responseCode in 200..299) {
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    val base64Content = json.optString("content", "").replace("\n", "").trim()
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
            } finally {
                connection?.disconnect()
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

                    if (activeToken.isBlank()) {
                        saveRepos(context, repos)
                        Handler(Looper.getMainLooper()).post {
                            onResult(
                                false,
                                "⚠️ GitHub Token Tanımlı Değil! Değişiklikleri GitHub'a kalıcı işlemek için Admin Panelinde 'repo' izinli Personal Access Token (PAT) giriniz."
                            )
                        }
                        return@Thread
                    }

                    val jsonContent = reposToJson(repos)

                    // 1. Önce dosyanın güncel SHA değerini ve doğrulamasını al (SHA çakışması önleme)
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
                    connection.setRequestProperty("User-Agent", "CloudStream-Repo-Manager")
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
                            onResult(false, "⚠️ SHA Çakışması! Dosya başka bir işlem tarafından değiştirilmiş. Lütfen güncelleyip tekrar deneyin.")
                        }
                    } else if (responseCode == 401 || responseCode == 403) {
                        Handler(Looper.getMainLooper()).post {
                            onResult(false, "❌ Yetkisiz Erişim (HTTP $responseCode)! Girdiğiniz GitHub Token 'repo' yazma iznine sahip olmalıdır.")
                        }
                    } else {
                        saveRepos(context, repos)
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
            var connection: HttpURLConnection? = null
            return try {
                val url = URL(GITHUB_CONTENTS_API)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.setRequestProperty("User-Agent", "CloudStream-Repo-Manager")
                if (token.isNotBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }

                if (connection.responseCode in 200..299) {
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    json.optString("sha", "")
                } else {
                    ""
                }
            } catch (_: Exception) {
                ""
            } finally {
                connection?.disconnect()
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
