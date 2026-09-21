package com.kaan.cloudstreamrepomanager

import android.net.Uri
import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

object NetworkUtils {

    private const val TAG = "NetworkUtils"
    const val DEFAULT_USER_AGENT = "CloudStreamRepoManager-Android"

    /**
     * URL'deki boşlukları, özel karakterleri, GitHub hatalı formatlarını temizler ve
     * doğrudan indirme/raw formatına dönüştürür.
     */
    fun sanitizeUrl(rawUrl: String): String {
        if (rawUrl.isBlank()) return ""

        var url = rawUrl.trim()

        // 1. RAW Dosya URL Dönüştürme:
        // "github.com/{user}/{repo}/raw/{branch}/{file}" -> "raw.githubusercontent.com/{user}/{repo}/{branch}/{file}"
        if (url.contains("github.com/", ignoreCase = true) && url.contains("/raw/", ignoreCase = true)) {
            url = url.replace(Regex("https?://github\\.com/([^/]+)/([^/]+)/raw/"), "https://raw.githubusercontent.com/$1/$2/")
        }
        // "github.com/{user}/{repo}/blob/{branch}/{file}?raw=true" -> "raw.githubusercontent.com/{user}/{repo}/{branch}/{file}"
        if (url.contains("github.com/", ignoreCase = true) && url.contains("/blob/", ignoreCase = true)) {
            url = url.replace(Regex("https?://github\\.com/([^/]+)/([^/]+)/blob/"), "https://raw.githubusercontent.com/$1/$2/")
                .replace("?raw=true", "")
                .replace("&raw=true", "")
        }

        // 2. Çift Slash Hatalarını Temizle (http:// veya https:// protokolü dışındaki // yolları)
        val schemeEndIndex = url.indexOf("://")
        if (schemeEndIndex != -1) {
            val scheme = url.substring(0, schemeEndIndex + 3)
            val rest = url.substring(schemeEndIndex + 3)
            val cleanedRest = rest.replace(Regex("/{2,}"), "/")
            url = scheme + cleanedRest
        }

        return try {
            val decoded = URLDecoder.decode(url, "UTF-8")
            if (decoded != url) {
                url
            } else {
                val parsedUri = Uri.parse(url)
                parsedUri.toString()
            }
        } catch (_: Exception) {
            url.replace(" ", "%20")
        }
    }

    /**
     * GitHub Release ve Raw indirmeleri için doğrudan indirme URL'si oluşturur.
     * Format: https://github.com/{owner}/{repo}/releases/download/{tag}/{file_name}
     */
    fun buildReleaseDownloadUrl(owner: String, repo: String, tag: String = "latest", fileName: String): String {
        val cleanTag = tag.trim().removePrefix("v")
        val tagSegment = if (cleanTag.lowercase() == "latest") "latest/download" else "download/v$cleanTag"
        return sanitizeUrl("https://github.com/$owner/$repo/releases/$tagSegment/$fileName")
    }

    /**
     * GitHub Raw dosyaları için doğrudan CDN URL'si oluşturur.
     * Format: https://raw.githubusercontent.com/{owner}/{repo}/{branch}/{file_name}
     */
    fun buildRawFileUrl(owner: String, repo: String, branch: String = "main", fileName: String): String {
        return sanitizeUrl("https://raw.githubusercontent.com/$owner/$repo/$branch/$fileName")
    }

    /**
     * Yönlendirmeleri (301, 302, 303, 307, 308) ve SSL yönlendirmelerini şeffaf şekilde takip eder.
     * objects.githubusercontent.com CDN geçişlerini sorunsuz halleder.
     */
    fun openFollowRedirectsConnection(
        initialUrl: String,
        method: String = "GET",
        headers: Map<String, String> = emptyMap(),
        maxRedirects: Int = 10
    ): HttpResult {
        var currentUrl = sanitizeUrl(initialUrl)
        var redirectCount = 0

        while (redirectCount < maxRedirects) {
            var connection: HttpURLConnection? = null
            try {
                val urlObj = URL(currentUrl)
                connection = urlObj.openConnection() as HttpURLConnection
                connection.requestMethod = method
                connection.connectTimeout = 12000
                connection.readTimeout = 12000
                connection.instanceFollowRedirects = true
                HttpURLConnection.setFollowRedirects(true)

                // Standard Başlıklar
                connection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT)
                connection.setRequestProperty("Accept", "*/*")
                connection.setRequestProperty("Accept-Language", "tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7")
                connection.setRequestProperty("Referer", "https://github.com/liberta09/CloudStreamRepoManager")

                // Özel Başlıklar
                headers.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }

                val responseCode = connection.responseCode

                // 3xx Yönlendirme Kontrolü (Cross-Protocol / Cross-Domain Manuel Yönlendirme)
                if (responseCode in 300..399) {
                    val location = connection.getHeaderField("Location")
                    connection.disconnect()

                    if (!location.isNullOrBlank()) {
                        val newUrl = if (location.startsWith("http://") || location.startsWith("https://")) {
                            location
                        } else {
                            val base = URL(currentUrl)
                            URL(base, location).toString()
                        }

                        Log.d(TAG, "Yönlendirme ($responseCode): $currentUrl -> $newUrl")
                        currentUrl = sanitizeUrl(newUrl)
                        redirectCount++
                        continue
                    }
                }

                val isSuccess = responseCode in 200..299
                val inputStream: InputStream? = if (isSuccess) connection.inputStream else connection.errorStream
                val body = inputStream?.bufferedReader()?.use { it.readText() } ?: ""

                if (!isSuccess) {
                    Log.e(
                        TAG,
                        "HTTP Hata ($responseCode) | Hedef URL: $currentUrl | Body: ${body.take(300)}"
                    )
                }

                return HttpResult(
                    responseCode = responseCode,
                    finalUrl = currentUrl,
                    body = body,
                    isSuccess = isSuccess,
                    headers = connection.headerFields
                )

            } catch (e: Exception) {
                Log.e(TAG, "Bağlantı Hatası: ${e.localizedMessage} | URL: $currentUrl", e)
                connection?.disconnect()
                return HttpResult(
                    responseCode = -1,
                    finalUrl = currentUrl,
                    body = e.localizedMessage ?: "Bağlantı hatası",
                    isSuccess = false
                )
            }
        }

        return HttpResult(
            responseCode = 310,
            finalUrl = currentUrl,
            body = "Çok fazla yönlendirme döngüsü (Too many redirects)",
            isSuccess = false
        )
    }

    data class HttpResult(
        val responseCode: Int,
        val finalUrl: String,
        val body: String,
        val isSuccess: Boolean,
        val headers: Map<String, List<String>> = emptyMap()
    )
}
