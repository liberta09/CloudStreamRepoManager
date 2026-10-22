import java.io.File
import org.json.JSONObject
import org.json.JSONArray
import java.net.URL
import java.net.HttpURLConnection

fun main() {
    try {
        val url = URL("https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/manifest.json")
        val connection = url.openConnection() as HttpURLConnection
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(body)
        val scrapers = json.optJSONArray("scrapers")

        val uniqueIds = mutableSetOf<String>()
        val uniqueFilenames = mutableSetOf<String>()
        var count = 0

        if (scrapers != null) {
            count = scrapers.length()
            for (i in 0 until scrapers.length()) {
                val src = scrapers.optJSONObject(i) ?: continue
                uniqueIds.add(src.optString("id"))
                uniqueFilenames.add(src.optString("filename"))
            }
        }

        println("Eski (Global) Paket Durumu:")
        println("Toplam Scraper Sayısı: $count")
        println("Benzersiz ID: ${uniqueIds.size}")
        println("Benzersiz Filename: ${uniqueFilenames.size}")
        
    } catch (e: Exception) {
        println("Hata: ${e.message}")
    }
}

main()