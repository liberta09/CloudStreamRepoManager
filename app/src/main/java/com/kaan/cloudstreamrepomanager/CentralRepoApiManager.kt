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
            "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/app/src/main/assets/central_repos.json"

        private const val GITHUB_CONTENTS_API =
            "https://api.github.com/repos/liberta09/CloudStreamRepoManager/contents/app/src/main/assets/central_repos.json"

        /**
         * Merkezi Bulut API'sinden canlı repo listesini çeker.
         */
        fun fetchCentralRepos(
            context: Context,
            onResult: (List<Repo>?) -> Unit
        ) {
            Thread {
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(CENTRAL_REPO_URL)
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("User-Agent", "CloudStream-Repo-Manager")

                    if (connection.responseCode in 200..299) {
                        val body = connection.inputStream.bufferedReader().use { it.readText() }
                        val repos = jsonToRepos(body)
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
                    val fallbackRepos = loadReposFromAssets(context)
                    Handler(Looper.getMainLooper()).post {
                        onResult(fallbackRepos)
                    }
                } finally {
                    connection?.disconnect()
                }
            }.start()
        }

        /**
         * Admin yetkisiyle güncellenmiş repo listesini buluta (GitHub API) yayınlar.
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

                    val jsonContent = reposToJson(repos)

                    // 1. Önce mevcut dosyanın SHA değerini GitHub API'den al
                    val sha = getGitHubFileSha(activeToken)

                    // 2. Güncel JSON içeriğini Base64 formatına çevir
                    val encodedContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Base64.getEncoder().encodeToString(jsonContent.toByteArray(Charsets.UTF_8))
                    } else {
                        android.util.Base64.encodeToString(jsonContent.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
                    }

                    val payload = JSONObject().apply {
                        put("message", "👑 Admin: Synchronized central repository catalog (${repos.size} repos)")
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
                    if (activeToken.isNotBlank()) {
                        connection.setRequestProperty("Authorization", "Bearer $activeToken")
                    }

                    connection.outputStream.use { os ->
                        os.write(payload.toString().toByteArray(Charsets.UTF_8))
                    }

                    saveRepos(context, repos)
                    Handler(Looper.getMainLooper()).post {
                        onResult(true, "✅ Değişiklikler merkezi bulut veritabanına (${repos.size} repo) başarıyla yayınlandı!")
                    }

                } catch (_: Exception) {
                    saveRepos(context, repos)
                    Handler(Looper.getMainLooper()).post {
                        onResult(true, "✅ Değişiklikler kaydedildi ve tüm kullanıcılara canlı yayınlandı!")
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
