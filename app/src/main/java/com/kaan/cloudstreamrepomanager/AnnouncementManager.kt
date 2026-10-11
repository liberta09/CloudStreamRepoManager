package com.kaan.cloudstreamrepomanager

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale

/**
 * Tek bir duyuru. En yeni duyuru listenin başındadır.
 */
data class Announcement(
    val id: String,
    val title: String,
    val message: String,
    val link: String = "",
    val date: String = ""
)

/**
 * Duyurular GitHub'daki announcements.json dosyasında tutulur.
 * Admin uygulaması bu dosyayı GitHub token ile yazar, kullanıcı uygulaması açılışta okur.
 * Böylece yeni APK yayınlamadan herkese duyuru gönderilebilir.
 */
object AnnouncementManager {

    private const val CONTENTS_API =
        "https://api.github.com/repos/liberta09/CloudStreamRepoManager/contents/announcements.json"

    private const val RAW_URL =
        "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/announcements.json"

    private const val PREFS_NAME = "cloudstream_repo_manager"
    private const val LAST_SEEN_KEY = "last_seen_announcement_id"
    private const val CACHE_KEY = "announcements_cache"

    private val mainHandler = Handler(Looper.getMainLooper())

    /* ---------------- OKUMA ---------------- */

    /**
     * Duyuruları çeker. Önce GitHub Contents API (önbelleksiz), olmazsa raw CDN,
     * o da olmazsa en son başarıyla çekilen kopya kullanılır.
     */
    fun fetch(context: Context, onResult: (List<Announcement>, Announcement?) -> Unit) {
        Thread {
            val list = try {
                fetchFromContentsApi() ?: fetchFromRaw()
            } catch (_: Exception) {
                null
            }

            if (list != null) {
                saveCache(context, list)
            }
            val result = list ?: loadCache(context)
            val unseen = unseenLatest(context, result)
            mainHandler.post { onResult(result, unseen) }
        }.start()
    }

    private fun fetchFromContentsApi(): List<Announcement>? {
        val result = NetworkUtils.openFollowRedirectsConnection(initialUrl = CONTENTS_API, headers = emptyMap())
        if (!result.isSuccess || result.body.isBlank()) return null
        val base64 = JSONObject(result.body).optString("content", "")
            .replace("\n", "").replace("\r", "").trim()
        if (base64.isBlank()) return null
        return parse(String(decodeBase64(base64), Charsets.UTF_8))
    }

    private fun fetchFromRaw(): List<Announcement>? {
        val result = NetworkUtils.openFollowRedirectsConnection(
            initialUrl = "$RAW_URL?nocache=${System.currentTimeMillis()}",
            headers = mapOf("Cache-Control" to "no-cache")
        )
        if (!result.isSuccess || result.body.isBlank()) return null
        return parse(result.body)
    }

    /* ---------------- YAYINLAMA (ADMIN) ---------------- */

    /**
     * Duyuru listesinin tamamını GitHub'a yazar (admin token gerekir).
     */
    fun publish(context: Context, list: List<Announcement>, onResult: (Boolean, String) -> Unit) {
        Thread {
            var connection: HttpURLConnection? = null
            try {
                val token = AdminAuthManager(context).adminGithubToken
                if (token.isBlank()) {
                    mainHandler.post {
                        onResult(false, "⚠️ GitHub Token tanımlı değil! Admin panelinden token girin.")
                    }
                    return@Thread
                }

                val sha = getSha(token)
                val payload = JSONObject().apply {
                    put("message", "📢 Admin: Duyurular güncellendi (${list.size} duyuru)")
                    put("content", encodeBase64(toJson(list).toByteArray(Charsets.UTF_8)))
                    if (sha.isNotBlank()) put("sha", sha)
                    put("branch", "main")
                }

                connection = (URL(CONTENTS_API).openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    connectTimeout = 10000
                    readTimeout = 10000
                    doOutput = true
                    setRequestProperty("User-Agent", NetworkUtils.DEFAULT_USER_AGENT)
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("Authorization", "Bearer $token")
                }
                connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }

                val code = connection.responseCode
                val message = when {
                    code in 200..299 -> {
                        saveCache(context, list)
                        "✅ Duyurular yayınlandı! Kullanıcılar uygulamayı açınca görecek."
                    }
                    code == 409 -> "⚠️ Çakışma oldu, lütfen tekrar deneyin."
                    code == 401 || code == 403 -> "❌ Yetkisiz erişim (HTTP $code). Token'ın yazma izni olmalı."
                    else -> "⚠️ GitHub yanıtı: HTTP $code"
                }
                mainHandler.post { onResult(code in 200..299, message) }
            } catch (e: Exception) {
                mainHandler.post { onResult(false, "⚠️ Hata: ${e.localizedMessage}") }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    private fun getSha(token: String): String = try {
        val result = NetworkUtils.openFollowRedirectsConnection(
            initialUrl = CONTENTS_API,
            headers = mapOf("Authorization" to "Bearer $token")
        )
        if (result.isSuccess && result.body.isNotBlank()) JSONObject(result.body).optString("sha", "") else ""
    } catch (_: Exception) {
        ""
    }

    /** Yeni bir duyuru nesnesi oluşturur (id = zaman damgası). */
    fun create(title: String, message: String, link: String): Announcement {
        val now = Date()
        return Announcement(
            id = now.time.toString(),
            title = title.trim(),
            message = message.trim(),
            link = link.trim(),
            date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr")).format(now)
        )
    }

    /* ---------------- "GÖRÜLDÜ" TAKİBİ ---------------- */

    /** Kullanıcının henüz görmediği en yeni duyuru (yoksa null). */
    fun unseenLatest(context: Context, list: List<Announcement>): Announcement? {
        val latest = list.firstOrNull() ?: return null
        val lastSeen = prefs(context).getString(LAST_SEEN_KEY, "") ?: ""
        return if (latest.id != lastSeen) latest else null
    }

    fun markSeen(context: Context, announcement: Announcement) {
        Thread {
            try {
                prefs(context).edit().putString(LAST_SEEN_KEY, announcement.id).apply()
            } catch (_: Exception) {}
        }.start()
    }

    /* ---------------- JSON ---------------- */

    private fun parse(json: String): List<Announcement> {
        val array = JSONObject(json).optJSONArray("announcements") ?: JSONArray()
        return (0 until array.length()).mapNotNull { i ->
            val o = array.optJSONObject(i) ?: return@mapNotNull null
            val id = o.optString("id", "")
            val message = o.optString("message", "")
            if (id.isBlank() || message.isBlank()) null
            else Announcement(
                id = id,
                title = o.optString("title", ""),
                message = message,
                link = o.optString("link", ""),
                date = o.optString("date", "")
            )
        }
    }

    private fun toJson(list: List<Announcement>): String {
        val array = JSONArray()
        list.forEach { a ->
            array.put(JSONObject().apply {
                put("id", a.id)
                put("title", a.title)
                put("message", a.message)
                put("link", a.link)
                put("date", a.date)
            })
        }
        return JSONObject().apply {
            put("version", 1)
            put("announcements", array)
        }.toString(2)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun saveCache(context: Context, list: List<Announcement>) {
        prefs(context).edit().putString(CACHE_KEY, toJson(list)).apply()
    }

    private fun loadCache(context: Context): List<Announcement> = try {
        parse(prefs(context).getString(CACHE_KEY, "") ?: "")
    } catch (_: Exception) {
        emptyList()
    }

    private fun decodeBase64(text: String): ByteArray =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Base64.getDecoder().decode(text)
        else android.util.Base64.decode(text, android.util.Base64.DEFAULT)

    private fun encodeBase64(bytes: ByteArray): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Base64.getEncoder().encodeToString(bytes)
        else android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
}
