package com.kaan.cloudstreamrepomanager

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
         * 🔌 Nuvio ortak plugin paketi.
         * Kullanıcı bu linki Nuvio → Ayarlar → İçerik ve Keşif → Pluginler'e BİR KEZ ekler;
         * Admin'in eklediği tüm plugin repoları bu tek manifestte toplanır ve Nuvio onu kendiliğinden yeniler.
         * Not: Nuvio linkin ".../manifest.json" ile bitmesini bekler.
         */
        const val NUVIO_PLUGIN_BUNDLE_URL =
            "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/manifest.json"

        private const val NUVIO_PLUGIN_BUNDLE_CONTENTS_API =
            "https://api.github.com/repos/liberta09/CloudStreamRepoManager/contents/nuvio/manifest.json"

        private fun getRepoNuvioContentsApi(slug: String): String {
            return "https://api.github.com/repos/liberta09/CloudStreamRepoManager/contents/nuvio/$slug/manifest.json"
        }

        fun getRepoNuvioManifestUrl(repoName: String): String {
            val slug = repoName.lowercase()
                .replace(Regex("[^a-z0-9]+"), "-")
                .trim('-')
                .ifBlank { "repo" }
            return "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/$slug/manifest.json"
        }

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
                        return@Thread
                    }

                    val apiRepos = fetchFromGitHubApiDirect(token)
                    if (!apiRepos.isNullOrEmpty()) {
                        saveSharedLocalRepos(context, apiRepos)
                        Handler(Looper.getMainLooper()).post {
                            onResult(apiRepos)
                        }
                        return@Thread
                    }

                    val fallbackRepos = loadReposFromAssets(context)
                    Handler(Looper.getMainLooper()).post {
                        onResult(fallbackRepos)
                    }
                } catch (_: Exception) {
                    val fallbackRepos = loadReposFromAssets(context)
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

                        // 🔌 Katalog yayınlandıktan sonra Nuvio ortak plugin paketini güncelle.
                        // Bu adım başarısız olsa bile katalog yayını başarılı sayılır.
                        if (repos.any { it.isNuvioPlugin() }) {
                            val bundleMessage = try {
                                publishNuvioPluginBundle(repos, activeToken)
                            } catch (e: Exception) {
                                "⚠️ Plugin paketi güncellenemedi: ${e.localizedMessage}"
                            }
                            if (bundleMessage.isNotBlank()) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, bundleMessage, Toast.LENGTH_LONG).show()
                                }
                            }
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

        /**
         * Tüm Nuvio plugin kayıtlarının manifestlerini indirir, tek bir Nuvio plugin manifestinde birleştirir
         * ve GitHub'a nuvio/manifest.json olarak yazar. (Arka plan iş parçacığında çağrılmalıdır.)
         *
         * - Göreli "filename" değerleri mutlak URL'ye çevrilir (Nuvio http(s) ile başlayan filename'i doğrudan indirir).
         * - Sağlayıcı kimlikleri "<repo-adı>__<id>" yapılır; böylece farklı repolardaki aynı id'ler çakışmaz.
         * - İçerik değişmediyse GitHub'a gereksiz commit atılmaz.
         *
         * @return Admin'e gösterilecek kısa sonuç mesajı (boşsa gösterilmez).
         */
        fun publishNuvioPluginBundle(repos: List<Repo>, token: String): String {
            if (token.isBlank()) return ""
            val pluginRepos = repos.filter { it.isNuvioPlugin() }
            if (pluginRepos.isEmpty()) return ""

            val globalScrapers = JSONArray()
            val usedFilenames = mutableSetOf<String>()
            val usedIds = mutableSetOf<String>()
            var okRepoCount = 0
            val skippedRepos = mutableListOf<String>()

            for (repo in pluginRepos) {
                val manifestUrl = repo.url.trim()
                
                // 1. Kendi ürettiğimiz manifestleri içeri almayı engelle (Self-reference Guard)
                if (manifestUrl.contains("/liberta09/cloudstreamrepomanager/", ignoreCase = true) && 
                    manifestUrl.contains("/nuvio/", ignoreCase = true)) {
                    skippedRepos.add(repo.name)
                    continue
                }
                
                val result = try {
                    NetworkUtils.openFollowRedirectsConnection(initialUrl = manifestUrl, method = "GET")
                } catch (_: Exception) {
                    null
                }
                val repoScrapers = if (result != null && result.isSuccess) extractNuvioPluginScrapers(result.body) else null
                if (repoScrapers == null || repoScrapers.length() == 0) {
                    skippedRepos.add(repo.name)
                    continue
                }

                val baseUrl = manifestUrl.substringBefore("?").substringBeforeLast("/")
                val slug = repo.name.lowercase()
                    .replace(Regex("[^a-z0-9]+"), "-")
                    .trim('-')
                    .ifBlank { "repo" }

                var addedToGlobal = 0
                val repoSpecificScrapers = JSONArray()

                for (i in 0 until repoScrapers.length()) {
                    val src = repoScrapers.optJSONObject(i) ?: continue
                    val originalId = src.optString("id").trim()
                    val rawFilename = src.optString("filename").trim()
                    if (originalId.isBlank() || rawFilename.isBlank()) continue

                    val absoluteFilename = if (rawFilename.startsWith("http://") || rawFilename.startsWith("https://")) {
                        rawFilename
                    } else {
                        "$baseUrl/${rawFilename.trimStart('/')}"
                    }

                    // 2. Normalize ID (Gereksiz "slug__" öneklerini kaldır)
                    val baseId = if (originalId.contains("__")) originalId.substringAfterLast("__") else originalId
                    val newId = "${slug}__${baseId}"
                    
                    // Repo'ya özel manifest için
                    val repoScraper = JSONObject(src.toString())
                    repoScraper.put("id", newId)
                    repoScraper.put("filename", absoluteFilename)
                    if (repoScraper.optString("name").isBlank()) repoScraper.put("name", baseId)
                    if (repoScraper.optString("version").isBlank()) repoScraper.put("version", "1.0.0")
                    repoSpecificScrapers.put(repoScraper)

                    // Global manifest için deduplication (filename bazlı)
                    if (usedFilenames.add(absoluteFilename.lowercase())) {
                        val globalScraper = JSONObject(src.toString())
                        
                        // ID çakışmasını önle
                        var finalId = newId
                        var counter = 1
                        while (!usedIds.add(finalId)) {
                            finalId = "${newId}_$counter"
                            counter++
                        }
                        
                        globalScraper.put("id", finalId)
                        globalScraper.put("filename", absoluteFilename)
                        if (globalScraper.optString("name").isBlank()) globalScraper.put("name", baseId)
                        if (globalScraper.optString("version").isBlank()) globalScraper.put("version", "1.0.0")
                        globalScrapers.put(globalScraper)
                        addedToGlobal++
                    }
                }

                if (repoSpecificScrapers.length() > 0) {
                    okRepoCount++
                    // Her repo için kendi manifestini asenkron olarak GitHub'a kaydet
                    publishSingleRepoManifest(slug, repo.name, repoSpecificScrapers, token)
                } else {
                    skippedRepos.add(repo.name)
                }
            }

            if (globalScrapers.length() == 0) {
                return "⚠️ Plugin paketi yazılmadı: plugin repolarından hiç sağlayıcı alınamadı."
            }

            // Mevcut global dosyayı oku (SHA + içerik karşılaştırması)
            var existingSha = ""
            var existingScrapers = ""
            try {
                val existing = NetworkUtils.openFollowRedirectsConnection(
                    initialUrl = NUVIO_PLUGIN_BUNDLE_CONTENTS_API,
                    headers = mapOf("Authorization" to "Bearer $token")
                )
                if (existing.isSuccess && existing.body.isNotBlank()) {
                    val json = JSONObject(existing.body)
                    existingSha = json.optString("sha", "")
                    val b64 = json.optString("content", "").replace("\n", "").replace("\r", "").trim()
                    if (b64.isNotBlank()) {
                        val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String(Base64.getDecoder().decode(b64), Charsets.UTF_8)
                        } else {
                            String(android.util.Base64.decode(b64, android.util.Base64.DEFAULT), Charsets.UTF_8)
                        }
                        existingScrapers = JSONObject(decoded).optJSONArray("scrapers")?.toString() ?: ""
                    }
                }
            } catch (_: Exception) {
                // Dosya henüz yoksa ilk kez oluşturulacak
            }

            val skippedNote = if (skippedRepos.isNotEmpty()) " (${skippedRepos.size} repo atlandı: ${skippedRepos.joinToString()})" else ""

            if (existingScrapers.isNotBlank() && existingScrapers == globalScrapers.toString()) {
                return "🔌 Plugin paketi zaten güncel: ${globalScrapers.length()} plugin$skippedNote"
            }

            val version = SimpleDateFormat("yyyy.MM.dd.HHmm", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val bundle = JSONObject().apply {
                put("name", "Repo Pluginleri (Global)")
                put("version", version)
                put("description", "Repo Manager ortak Nuvio plugin paketi ($okRepoCount repo, ${globalScrapers.length()} tekil plugin)")
                put("author", "liberta09")
                put("scrapers", globalScrapers)
            }

            val content = bundle.toString(2)
            val encoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(content.toByteArray(Charsets.UTF_8))
            } else {
                android.util.Base64.encodeToString(content.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
            }

            val payload = JSONObject().apply {
                put("message", "🔌 Admin: Updated global Nuvio plugin bundle (${globalScrapers.length()} unique plugins)")
                put("content", encoded)
                if (existingSha.isNotBlank()) put("sha", existingSha)
                put("branch", "main")
            }

            var connection: HttpURLConnection? = null
            return try {
                connection = URL(NUVIO_PLUGIN_BUNDLE_CONTENTS_API).openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.doOutput = true
                connection.setRequestProperty("User-Agent", NetworkUtils.DEFAULT_USER_AGENT)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.outputStream.use { os -> os.write(payload.toString().toByteArray(Charsets.UTF_8)) }

                val code = connection.responseCode
                if (code in 200..299) {
                    "🔌 Plugin paketi yayınlandı: $okRepoCount repodan ${globalScrapers.length()} tekil plugin$skippedNote"
                } else {
                    "⚠️ Plugin paketi yayınlanamadı (HTTP $code)$skippedNote"
                }
            } finally {
                connection?.disconnect()
            }
        }

        private fun publishSingleRepoManifest(slug: String, repoName: String, scrapers: JSONArray, token: String) {
            val apiUrl = "https://api.github.com/repos/liberta09/CloudStreamRepoManager/contents/nuvio/$slug/manifest.json"
            
            Thread {
                var existingSha = ""
                var existingScrapers = ""
                try {
                    val existing = NetworkUtils.openFollowRedirectsConnection(
                        initialUrl = apiUrl,
                        headers = mapOf("Authorization" to "Bearer $token")
                    )
                    if (existing.isSuccess && existing.body.isNotBlank()) {
                        val json = JSONObject(existing.body)
                        existingSha = json.optString("sha", "")
                        val b64 = json.optString("content", "").replace("\n", "").replace("\r", "").trim()
                        if (b64.isNotBlank()) {
                            val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                String(Base64.getDecoder().decode(b64), Charsets.UTF_8)
                            } else {
                                String(android.util.Base64.decode(b64, android.util.Base64.DEFAULT), Charsets.UTF_8)
                            }
                            existingScrapers = JSONObject(decoded).optJSONArray("scrapers")?.toString() ?: ""
                        }
                    }
                } catch (_: Exception) {}

                if (existingScrapers.isNotBlank() && existingScrapers == scrapers.toString()) {
                    return@Thread // Zaten güncel
                }

                val version = SimpleDateFormat("yyyy.MM.dd.HHmm", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date())

                val bundle = JSONObject().apply {
                    put("name", "$repoName Pluginleri")
                    put("version", version)
                    put("description", "Repo Manager - $repoName Nuvio plugin paketi (${scrapers.length()} plugin)")
                    put("author", "liberta09")
                    put("scrapers", scrapers)
                }

                val content = bundle.toString(2)
                val encoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(content.toByteArray(Charsets.UTF_8))
                } else {
                    android.util.Base64.encodeToString(content.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
                }

                val payload = JSONObject().apply {
                    put("message", "🔌 Admin: Updated Nuvio plugin manifest for $slug (${scrapers.length()} plugins)")
                    put("content", encoded)
                    if (existingSha.isNotBlank()) put("sha", existingSha)
                    put("branch", "main")
                }

                var connection: HttpURLConnection? = null
                try {
                    connection = URL(apiUrl).openConnection() as HttpURLConnection
                    connection.requestMethod = "PUT"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.doOutput = true
                    connection.setRequestProperty("User-Agent", NetworkUtils.DEFAULT_USER_AGENT)
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    connection.setRequestProperty("Authorization", "Bearer $token")
                    connection.outputStream.use { os -> os.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                    connection.responseCode // execute
                } catch (_: Exception) {}
                finally { connection?.disconnect() }
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
            Thread {
                try {
                    val sharedDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!sharedDir.exists()) sharedDir.mkdirs()
                    val sharedFile = File(sharedDir, "cs_repo_manager_shared_catalog.json")
                    val json = reposToJson(repos)
                    sharedFile.writeText(json, Charsets.UTF_8)
                } catch (_: Exception) {}
            }.start()
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
