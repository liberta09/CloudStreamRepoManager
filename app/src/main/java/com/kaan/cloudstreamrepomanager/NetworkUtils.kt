package com.kaan.cloudstreamrepomanager

import android.net.Uri
import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

object NetworkUtils {

    private const val TAG = "NetworkUtils"
    const val DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 CloudStreamRepoManager/1.1.1"

    /**
     * URL'deki boşlukları, özel karakterleri, çift slash (//) hatalarını temizler ve
     * geçerli bir HTTP/HTTPS URL'sine dönüştürür.
     */
    fun sanitizeUrl(rawUrl: String): String {
        if (rawUrl.isBlank()) return ""

        var url = rawUrl.trim()

        // Çift slash hatalarını temizle (http:// veya https:// protokolü sonrasındaki // yolları düzelt)
        val schemeEndIndex = url.indexOf("://")
        if (schemeEndIndex != -1) {
            val scheme = url.substring(0, schemeEndIndex + 3)
            val rest = url.substring(schemeEndIndex + 3)
            val cleanedRest = rest.replace(Regex("/{2,}"), "/")
            url = scheme + cleanedRest
        }

        return try {
            // Zaten encode edilmiş mi kontrol et
            val decoded = URLDecoder.decode(url, "UTF-8")
            if (decoded != url) {
                // Zaten encode edilmiş, geri döndür
                url
            } else {
                // Uri üzerinden güvenli encode et
                val parsedUri = Uri.parse(url)
                parsedUri.toString()
            }
        } catch (_: Exception) {
            url.replace(" ", "%20")
        }
    }

    /**
     * Yönlendirmeleri (301, 302, 303, 307, 308) şeffaf bir şekilde takip eder.
     * HTTP <-> HTTPS ve alan adı geçişlerinde de sorunsuz çalışır.
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
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.instanceFollowRedirects = false // Manuel takip

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

                // 3xx Yönlendirme Kontrolü
                if (responseCode in 300..399) {
                    val location = connection.getHeaderField("Location")
                    connection.disconnect()

                    if (!location.isNullOrBlank()) {
                        val newUrl = if (location.startsWith("http://") || location.startsWith("https://")) {
                            location
                        } else {
                            // Göreli (Relative) URL çözümleme
                            val base = URL(currentUrl)
                            URL(base, location).toString()
                        }

                        Log.d(TAG, "Yönlendirme ($responseCode): $currentUrl -> $newUrl")
                        currentUrl = sanitizeUrl(newUrl)
                        redirectCount++
                        continue
                    }
                }

                // Yanıt Oku
                val isSuccess = responseCode in 200..299
                val inputStream: InputStream? = if (isSuccess) connection.inputStream else connection.errorStream
                val body = inputStream?.bufferedReader()?.use { it.readText() } ?: ""

                if (!isSuccess) {
                    Log.e(
                        TAG,
                        "HTTP Hata ($responseCode) | Hedef URL: $currentUrl | Yanıt Gövdesi: ${body.take(300)}"
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
