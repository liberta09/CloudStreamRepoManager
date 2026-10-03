
package com.kaan.cloudstreamrepomanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.kaan.cloudstreamrepomanager.ui.theme.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun TvIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (focused) 1.2f else 1.0f, animationSpec = tween(150), label = "iconScale")
    val borderColor by animateColorAsState(targetValue = if (focused) CyberYellow else Color.Transparent, animationSpec = tween(150), label = "iconBorder")
    val bgColor by animateColorAsState(targetValue = if (focused) CyberCardDark else Color.Transparent, animationSpec = tween(150), label = "iconBg")

    Box(
        modifier = modifier
            .size(36.dp)
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(bgColor, RoundedCornerShape(50))
            .border(if (focused) 2.dp else 0.dp, borderColor, RoundedCornerShape(50))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun TvOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (focused) 1.08f else 1.0f, animationSpec = tween(150), label = "outBtnScale")
    val borderColor by animateColorAsState(targetValue = if (focused) CyberYellow else CyberBorder, animationSpec = tween(150), label = "outBtnBorder")

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        enabled = enabled,
        border = BorderStroke(if (focused) 2.dp else 1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        content = content
    )
}

@Composable
private fun TvButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (focused) 1.08f else 1.0f, animationSpec = tween(150), label = "btnScale")
    val borderColor by animateColorAsState(targetValue = if (focused) CyberYellow else Color.Transparent, animationSpec = tween(150), label = "btnBorder")

    Button(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        enabled = enabled,
        border = BorderStroke(2.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        content = content
    )
}

@Composable
private fun TvFilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (focused) 1.08f else 1.0f, animationSpec = tween(150), label = "tonalScale")
    val borderColor by animateColorAsState(targetValue = if (focused) CyberYellow else Color.Transparent, animationSpec = tween(150), label = "tonalBorder")

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        enabled = enabled,
        border = BorderStroke(2.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        content = content
    )
}


data class Repo(
    val name: String,
    val url: String,
    val code: String = "",
    val category: String,
    val stars: Int = 0,
    val type: String = "cloudstream"
)

private const val PREFS_NAME = "cloudstream_repo_manager"
private const val REPOS_KEY = "repos"

// Product Flavors Build Konfigürasyonu (BuildConfig üzerinden otomatik gelir)
val ENABLE_ADMIN_PANEL_FEATURE = BuildConfig.ENABLE_ADMIN_PANEL

// Ayarlar ekranındaki Telegram kanalı bağlantısı
const val TELEGRAM_CHANNEL_URL = "https://t.me/+o-RFlV4U3UY5NGU8"
private const val TELEGRAM_INVITE_HIDDEN_KEY = "telegram_invite_hidden"

fun openLink(context: Context, link: String) {
    if (link.isBlank()) return
    val url = if (link.startsWith("http://") || link.startsWith("https://")) link else "https://$link"
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        Toast.makeText(context, "Bağlantı açılamadı", Toast.LENGTH_SHORT).show()
    }
}

fun openTelegramChannel(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_CHANNEL_URL)))
    } catch (e: Exception) {
        Toast.makeText(context, "Bağlantı açılamadı", Toast.LENGTH_SHORT).show()
    }
}

/* =========================================================
   ACTIVITY
   ========================================================= */

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode = getSharedPreferences("cloudstream_repo_manager", MODE_PRIVATE)
                .getString("theme_mode", "Cyber") ?: "Cyber"
            CloudStreamRepoManagerTheme(themeMode = themeMode) {
                var showSplash by remember { mutableStateOf(true) }

                Box(modifier = Modifier.fillMaxSize()) {
                    CloudStreamRepoManager()

                    if (showSplash) {
                        CyberSplashScreen(onFinish = { showSplash = false })
                    }
                }
            }
        }
    }
}

/* =========================================================
   SİBER AÇILIŞ EKRANI (FULL SCREEN CYBER SPLASH)
   ========================================================= */

@Composable
fun CyberSplashScreen(onFinish: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("[ 00% ] INITIALIZING CYBER CORE...") }

    LaunchedEffect(Unit) {
        delay(150)
        progress = 0.35f
        statusText = "[ 35% ] LOADING REPOSITORY DATA..."
        delay(200)
        progress = 0.75f
        statusText = "[ 75% ] VERIFYING CLOUDSTREAM PROTOCOLS..."
        delay(200)
        progress = 1.0f
        statusText = "[ 100% ] SYSTEM ONLINE."
        delay(200)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBgDark)
            .clickable { onFinish() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Tam Ekran Siber Logo Bileşeni
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Cyber Logo",
                modifier = Modifier
                    .size(240.dp)
                    .border(2.dp, CyberCyan, RoundedCornerShape(24.dp))
                    .background(CyberSurfaceDark, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "⚡ CS REPO MANAGER",
                fontWeight = FontWeight.Bold,
                color = CyberYellow,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SYSTEM // REPO_KATALOG_V2.0",
                color = CyberCyan,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Yükleme İlerleme Çubuğu
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(10.dp)
                    .border(1.dp, CyberBorder, RoundedCornerShape(5.dp))
                    .background(CyberCardDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(CyberYellow, RoundedCornerShape(5.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusText,
                color = CyberPink,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/* =========================================================
   JSON
   ========================================================= */

fun reposToJson(repos: List<Repo>, version: Int = 1): String {
    val root = JSONObject()
    root.put("version", version)
    root.put("updatedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))

    val array = JSONArray()
    repos.forEachIndexed { index, repo ->
        val obj = JSONObject()
        val pType = repo.type.ifBlank { "cloudstream" }
        obj.put("id", (index + 1).toString())
        obj.put("name", repo.name)
        obj.put("url", repo.url)
        obj.put("code", repo.code)
        obj.put("category", repo.category)
        obj.put("stars", repo.stars)
        obj.put("type", pType)
        obj.put("platform", pType)

        array.put(obj)
    }
    root.put("repos", array)

    return root.toString(2)
}

fun jsonToRepos(json: String): List<Repo> {
    val result = mutableListOf<Repo>()
    if (json.isBlank()) return result

    val trimmed = json.trim()
    val array = if (trimmed.startsWith("{")) {
        val root = JSONObject(trimmed)
        root.optJSONArray("repos") ?: JSONArray()
    } else {
        JSONArray(trimmed)
    }

    for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val rawType = obj.optString("platform", obj.optString("type", "cloudstream")).ifBlank { "cloudstream" }
        val parsedStars = if (obj.has("stars")) {
            obj.optInt("stars", 0)
        } else {
            0
        }
        
        result.add(
            Repo(
                name = obj.optString("name"),
                url = obj.optString("url"),
                code = obj.optString("code"),
                category = obj.optString("category"),
                stars = parsedStars,
                type = rawType
            )
        )
    }

    return result
}

/* =========================================================
   KAYDET / YÜKLE
   ========================================================= */

fun saveRepos(
    context: Context,
    repos: List<Repo>
) {
    Thread {
        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                REPOS_KEY,
                reposToJson(repos)
            )
            .apply()
    }.start()
}

fun getDefaultRepos(): List<Repo> {
    return listOf(
        Repo(
            name = "Manitux Cloudstream (HDFilmCehennemi & DiziBox)",
            url = "https://raw.githubusercontent.com/manitux-app/cs-plugins/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "NeO Eklenti Deposu (HDFilmCehennemi & DiziPal)",
            url = "https://raw.githubusercontent.com/neoser1984/cloudstream-extensions/main/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "cs-karma",
            url = "https://raw.githubusercontent.com/Kraptor123/cs-Karma/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "TurkSinema",
            url = "https://raw.githubusercontent.com/Wiojelt/TurkSinema/main/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "cstest",
            url = "https://raw.githubusercontent.com/ctnkyaumt/cstest/master/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "AllForU",
            url = "https://raw.githubusercontent.com/RVRBEAST76/allforu-repo/builds/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "BronzeCloud",
            url = "https://raw.githubusercontent.com/Dr-Octagon/cloudstream-turkish/refs/heads/builds/repo_stable.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "Turkish Providers Repository | @feroxxcs3",
            url = "https://raw.githubusercontent.com/liberta09/Kekik-cloudstream/builds/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "Phisher Repo",
            url = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/refs/heads/builds/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "Mega repository",
            url = "https://raw.githubusercontent.com/self-similarity/MegaRepo/builds/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "mudti",
            url = "https://raw.githubusercontent.com/pltmustafa/plt-stream/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "megaturk",
            url = "https://raw.githubusercontent.com/Kraptor123/TurkMegaRepo/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "Latte - Sinetech.TR",
            url = "https://raw.githubusercontent.com/GitLatte/Sinetech/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        ),
        Repo(
            name = "Makoto'nun Cloudstream Reposu",
            url = "https://raw.githubusercontent.com/Sertel392/Makotogecici/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            stars = 0
        )
    )
}

private const val DELETED_REPOS_KEY = "deleted_repos_urls"

fun getDeletedUrls(context: Context): Set<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getStringSet(DELETED_REPOS_KEY, emptySet()) ?: emptySet()
}

fun addDeletedUrl(context: Context, url: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val currentSet = getDeletedUrls(context).toMutableSet()
    currentSet.add(url.trim().lowercase())
    prefs.edit().putStringSet(DELETED_REPOS_KEY, currentSet).apply()
}

fun clearDeletedUrls(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().remove(DELETED_REPOS_KEY).apply()
}

fun filterDeletedRepos(context: Context, repos: List<Repo>): List<Repo> {
    val deletedSet = getDeletedUrls(context)
    if (deletedSet.isEmpty()) return repos
    return repos.filter { !deletedSet.contains(it.url.trim().lowercase()) }
}

fun loadRepos(
    context: Context
): List<Repo> {

    val prefs =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    val json =
        prefs.getString(
            REPOS_KEY,
            null
        )

    if (json.isNullOrBlank()) {
        val defaultList = getDefaultRepos()
        val filtered = filterDeletedRepos(context, defaultList)
        saveRepos(context, filtered)
        return filtered
    }

    return try {
        val repos = jsonToRepos(json).toMutableList()
        val defaultList = getDefaultRepos()
        val existingUrls = repos.map { it.url.trim().lowercase() }.toSet()
        val deletedUrls = getDeletedUrls(context)
        var addedNew = false
        for (defaultRepo in defaultList) {
            val urlClean = defaultRepo.url.trim().lowercase()
            if (!existingUrls.contains(urlClean) && !deletedUrls.contains(urlClean)) {
                repos.add(0, defaultRepo)
                addedNew = true
            }
        }
        val filtered = filterDeletedRepos(context, repos)
        if (addedNew) {
            saveRepos(context, filtered)
        }
        filtered
    } catch (_: Exception) {
        val defaultList = getDefaultRepos()
        val filtered = filterDeletedRepos(context, defaultList)
        saveRepos(context, filtered)
        filtered
    }
}

/* =========================================================
   KOPYALA
   ========================================================= */

fun copyToClipboard(
    context: Context,
    text: String,
    message: String
) {
    val clipboard =
        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

    clipboard.setPrimaryClip(
        ClipData.newPlainText(
            "CloudStream",
            text
        )
    )

    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun openCloudStreamAndPrepareRepo(
    context: Context,
    repo: Repo,
    onNotInstalled: () -> Unit
) {

    /*
     * CloudStream eklenti deposu yönlendirmesi
     *
     * 1. cloudstreamrepo:// özel derin bağlantısını (deep link) doğrudan Intent ile çağırır.
     * 2. Doğrudan çağrı açılmazsa bilinen CloudStream paketlerine özel Intent dener.
     * 3. Yine açılmazsa CloudStream uygulamasını başlatır ve bağlantıyı panoya kopyalar.
     * 4. Cihazda CloudStream yüklü değilse bilgilendirme diyaloğunu tetikler.
     */

    val cleanUrl = repo.url.trim()
        .removePrefix("https://")
        .removePrefix("http://")

    val cloudStreamRepoUri = Uri.parse("cloudstreamrepo://$cleanUrl")

    // Repo bağlantısını panoya kopyalayalım
    copyToClipboard(
        context,
        cloudStreamRepoUri.toString(),
        "Repo bağlantısı panoya kopyalandı"
    )

    val packageNames = listOf(
        "com.lagradost.cloudstream3",
        "com.lagradost.cloudstream3.prerelease",
        "com.lagradost.cloudstream3.prerelease.debug",
        "com.lagradost.cloudstream3.debug"
    )

    // 1. AŞAMA: Doğrudan genel cloudstreamrepo:// derin bağlantı (deep link) intent'i
    try {
        val directIntent = Intent(Intent.ACTION_VIEW, cloudStreamRepoUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(directIntent)

        Toast.makeText(
            context,
            "${repo.name} CloudStream'e aktarılıyor...",
            Toast.LENGTH_LONG
        ).show()
        return
    } catch (_: Exception) {
        // Doğrudan genel intent başarısız olduysa devam et
    }

    // 2. AŞAMA: CloudStream paket adlarına özel derin bağlantı intent'i
    for (packageName in packageNames) {
        try {
            val packageIntent = Intent(Intent.ACTION_VIEW, cloudStreamRepoUri).apply {
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(packageIntent)

            Toast.makeText(
                context,
                "${repo.name} CloudStream'e aktarılıyor...",
                Toast.LENGTH_LONG
            ).show()
            return
        } catch (_: Exception) {
            // Sıradaki paket adını dene
        }
    }

    // 3. AŞAMA: Doğrudan CloudStream uygulamasını başlatma
    for (packageName in packageNames) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)

                Toast.makeText(
                    context,
                    "CloudStream açıldı. Bağlantı panoya kopyalandı; Eklentiler → Repo Ekle bölümüne yapıştırabilirsiniz.",
                    Toast.LENGTH_LONG
                ).show()
                return
            } catch (_: Exception) {
                // Sıradaki paketi dene
            }
        }
    }

    // 4. AŞAMA: Cihazda CloudStream yüklü DEĞİLSE
    onNotInstalled()
}

fun openNuvioAndPrepareRepo(
    context: Context,
    repo: Repo,
    onNotInstalled: () -> Unit
) {
    /*
     * Nuvio eklenti deposu yönlendirmesi
     *
     * 1. Doğrudan repo URL'sini panoya kopyalar.
     * 2. nuviorepo:// ve nuvio:// derin bağlantılarını (deep link) Intent ile çağırır.
     * 3. Doğrudan çağrı açılmazsa bilinen Nuvio paketlerine özel Intent dener.
     * 4. Yine açılmazsa Nuvio uygulamasını başlatır ve bağlantıyı panoya kopyalanmış tutar.
     * 5. Cihazda Nuvio yüklü değilse bilgilendirme diyaloğunu tetikler.
     */

    val rawUrl = repo.url.trim()
    val cleanUrl = rawUrl
        .removePrefix("https://")
        .removePrefix("http://")

    // Repo bağlantısını panoya kopyalayalım
    copyToClipboard(
        context,
        rawUrl,
        "Nuvio repo bağlantısı panoya kopyalandı"
    )

    val packageNames = listOf(
        "com.nuvio.app",
        "com.nuvio",
        "app.nuvio",
        "com.nuvio.android",
        "tv.nuvio.app",
        "com.nuvio.tv"
    )

    val nuvioUris = listOf(
        Uri.parse("nuviorepo://$cleanUrl"),
        Uri.parse("nuvio://$cleanUrl"),
        Uri.parse("nuvio://add-repo?url=${Uri.encode(rawUrl)}"),
        Uri.parse("nuvio://repo?url=${Uri.encode(rawUrl)}")
    )

    // 1. AŞAMA: Doğrudan genel derin bağlantı (deep link) intent'leri
    for (nuvioUri in nuvioUris) {
        try {
            val directIntent = Intent(Intent.ACTION_VIEW, nuvioUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(directIntent)

            Toast.makeText(
                context,
                "${repo.name} Nuvio'ya aktarılıyor...",
                Toast.LENGTH_LONG
            ).show()
            return
        } catch (_: Exception) {
            // Sonraki URI'yi dene
        }
    }

    // 2. AŞAMA: Nuvio paket adlarına özel derin bağlantı intent'i
    for (packageName in packageNames) {
        for (nuvioUri in nuvioUris) {
            try {
                val packageIntent = Intent(Intent.ACTION_VIEW, nuvioUri).apply {
                    setPackage(packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(packageIntent)

                Toast.makeText(
                    context,
                    "${repo.name} Nuvio'ya aktarılıyor...",
                    Toast.LENGTH_LONG
                ).show()
                return
            } catch (_: Exception) {
                // Sonraki paket/URI'yi dene
            }
        }
    }

    // 3. AŞAMA: Doğrudan Nuvio uygulamasını başlatma
    for (packageName in packageNames) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)

                Toast.makeText(
                    context,
                    "Nuvio açıldı. Bağlantı panoya kopyalandı; Eklentiler / Repo Ekle bölümüne yapıştırabilirsiniz.",
                    Toast.LENGTH_LONG
                ).show()
                return
            } catch (_: Exception) {
                // Sıradaki paketi dene
            }
        }
    }

    // 4. AŞAMA: Cihazda Nuvio yüklü DEĞİLSE
    onNotInstalled()
}

/* =========================================================
   TEK LİNK KONTROLÜ
   ========================================================= */

fun validateCloudStreamRepoJson(
    jsonText: String
): String {

    return try {

        val json =
            JSONObject(jsonText)

        val name =
            json.optString("name").trim()

        val manifestVersion =
            json.opt("manifestVersion")

        val pluginLists =
            json.optJSONArray("pluginLists")

        when {

            name.isBlank() ->
                "🟡 JSON geçerli ama repo adı (name) yok"

            manifestVersion !is Number ->
                "🟡 JSON geçerli ama manifestVersion yok"

            pluginLists == null ||
                    pluginLists.length() == 0 ->
                "🟡 JSON geçerli ama pluginLists yok"

            else -> {

                var validPluginList = true

                for (i in 0 until pluginLists.length()) {

                    if (
                        pluginLists.optString(i)
                            .trim()
                            .isBlank()
                    ) {
                        validPluginList = false
                        break
                    }
                }

                if (validPluginList) {

                    "🟢 Geçerli CloudStream Repo"

                } else {

                    "🟡 pluginLists içinde geçersiz link var"
                }
            }
        }

    } catch (_: Exception) {

        "🟡 Link çalışıyor ama geçerli JSON değil"
    }
}

fun performRepoCheck(
    urlString: String
): String {
    return try {
        val result = NetworkUtils.openFollowRedirectsConnection(
            initialUrl = urlString,
            method = "GET"
        )

        when {
            result.isSuccess -> {
                validateCloudStreamRepoJson(result.body)
            }
            result.responseCode in 400..499 -> {
                "🟠 HTTP ${result.responseCode} Hata"
            }
            result.responseCode in 500..599 -> {
                "🔴 Sunucu hatası — HTTP ${result.responseCode}"
            }
            else -> {
                "🔴 Bağlantı başarısız (HTTP ${result.responseCode})"
            }
        }
    } catch (e: Exception) {
        "🔴 Bağlantı hatası"
    }
}

fun checkRepoUrl(
    urlString: String,
    onResult: (String) -> Unit
) {
    Thread {

        val result =
            performRepoCheck(urlString)

        Handler(
            Looper.getMainLooper()
        ).post {
            onResult(result)
        }

    }.start()
}

/* =========================================================
   TÜM LİNKLERİ KONTROL ET
   ========================================================= */

fun checkAllRepoUrls(
    repos: List<Repo>,
    onStart: (String) -> Unit,
    onResult: (String, String) -> Unit,
    onFinished: () -> Unit
) {

    Thread {

        repos.forEachIndexed { index, repo ->

            Handler(
                Looper.getMainLooper()
            ).post {
                onStart(
                    "Kontrol ediliyor: ${index + 1}/${repos.size} — ${repo.name}"
                )
            }

            val result =
                performRepoCheck(repo.url)

            Handler(
                Looper.getMainLooper()
            ).post {
                onResult(
                    repo.url,
                    result
                )
            }
        }

        Handler(
            Looper.getMainLooper()
        ).post {
            onFinished()
        }

    }.start()
}

/* =========================================================
   ANA EKRAN
   ========================================================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudStreamRepoManager() {

    val context = LocalContext.current

    val repos =
        remember {
            mutableStateListOf<Repo>().apply {
                addAll(loadRepos(context))
            }
        }

    val checkResults =
        remember {
            mutableStateMapOf<String, String>()
        }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showSettingsDialog by remember {
        mutableStateOf(false)
    }

    // Açılışta Telegram daveti (sadece kullanıcı sürümü, "Bir daha gösterme" denmediyse)
    // Duyurular
    val announcements = remember { mutableStateListOf<Announcement>() }
    var announcementPopup by remember { mutableStateOf<Announcement?>(null) }
    var showAnnouncementsDialog by remember { mutableStateOf(false) }
    var showAdminAnnouncementsDialog by remember { mutableStateOf(false) }
    var publishingAnnouncements by remember { mutableStateOf(false) }

    var showTelegramInvite by remember {
        mutableStateOf(false)
    }

    var selectedRepo by remember {
        mutableStateOf<Repo?>(null)
    }

    var repoToDelete by remember {
        mutableStateOf<Repo?>(null)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("Tümü")
    }

    var selectedPlatform by remember {
        mutableStateOf("Tümü")
    }

    var showFavoritesOnly by remember {
        mutableStateOf(false)
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var selectedBottomTab by remember { mutableStateOf("Repolar") }
    var selectedRepoForDetail by remember { mutableStateOf<Repo?>(null) }
    var sortByStars by remember { mutableStateOf(false) }

    var currentThemeMode by remember {
        mutableStateOf(
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("theme_mode", "Cyber") ?: "Cyber"
        )
    }

    // Force theme apply globally immediately
    applyThemeColors(currentThemeMode)

    var checkingAll by remember {
        mutableStateOf(false)
    }

    var checkingMessage by remember {
        mutableStateOf("")
    }

    var showCloudStreamNotInstalledDialog by remember {
        mutableStateOf(false)
    }

    var showNuvioNotInstalledDialog by remember {
        mutableStateOf(false)
    }

    var updateInfo by remember {
        mutableStateOf<AppUpdateInfo?>(null)
    }

    var showUpdateDialog by remember {
        mutableStateOf(false)
    }

    var checkingUpdate by remember {
        mutableStateOf(false)
    }

    val adminAuthManager = remember { AdminAuthManager(context) }
    var isAdminLoggedIn by remember { mutableStateOf(ENABLE_ADMIN_PANEL_FEATURE) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var isPublishingToCloud by remember { mutableStateOf(false) }

    // Uygulama her açıldığında merkezi GitHub repo.json verisini çek, silinenleri filtrele ve önbelleği güncelle
    LaunchedEffect(Unit) {
        CentralRepoApiManager.fetchCentralRepos(context) { liveRepos ->
            if (!liveRepos.isNullOrEmpty()) {
                val cleanRepos = filterDeletedRepos(context, liveRepos)
                if (cleanRepos != repos.toList()) {
                    repos.clear()
                    repos.addAll(cleanRepos)
                    saveRepos(context, cleanRepos)
                }
            }
        }

        AnnouncementManager.fetch(context) { list, unseen ->
            if (announcements.toList() != list) {
                announcements.clear()
                announcements.addAll(list)
            }
            if (!ENABLE_ADMIN_PANEL_FEATURE && unseen != null) {
                announcementPopup = unseen
            }
        }

        AppUpdateManager.checkForUpdates(BuildConfig.VERSION_NAME) { info ->
            if (info != null && info.isUpdateAvailable) {
                updateInfo = info
                showUpdateDialog = true
            }
        }
    }

    var showFixExtractorsDialog by remember {
        mutableStateOf(false)
    }

    /* =====================================================
       YEDEKLE
       ===================================================== */

    val backupLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.CreateDocument(
                    "application/json"
                )
        ) { uri: Uri? ->

            if (uri != null) {

                try {

                    context.contentResolver
                        .openOutputStream(uri)
                        ?.use { output ->

                            output.write(
                                reposToJson(repos)
                                    .toByteArray(
                                        Charsets.UTF_8
                                    )
                            )
                        }

                    Toast.makeText(
                        context,
                        "Yedek başarıyla oluşturuldu",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (_: Exception) {

                    Toast.makeText(
                        context,
                        "Yedek oluşturulamadı",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    /* =====================================================
       GERİ YÜKLE
       ===================================================== */

    val restoreLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri != null) {

                try {

                    val json =
                        context.contentResolver
                            .openInputStream(uri)
                            ?.use { input ->
                                input.readBytes()
                                    .toString(
                                        Charsets.UTF_8
                                    )
                            }

                    if (!json.isNullOrBlank()) {

                        val restored =
                            jsonToRepos(json)

                        repos.clear()
                        repos.addAll(restored)

                        saveRepos(
                            context,
                            repos
                        )

                        checkResults.clear()

                        searchText = ""
                        selectedCategory = "Tümü"
                        showFavoritesOnly = false

                        Toast.makeText(
                            context,
                            "${restored.size} repo geri yüklendi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (_: Exception) {

                    Toast.makeText(
                        context,
                        "Geçersiz yedek dosyası",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    /* =====================================================
       KATEGORİLER
       ===================================================== */

    val categories =
        listOf("Tümü") +
                repos
                    .map {
                        it.category.trim()
                    }
                    .filter {
                        it.isNotBlank()
                    }
                    .distinct()
                    .sorted()

    /* =====================================================
       FİLTRE
       ===================================================== */

    val filteredRepos =
        repos.filter { repo ->

            val searchMatch =
                repo.name.contains(searchText, ignoreCase = true) ||
                repo.url.contains(searchText, ignoreCase = true) ||
                repo.code.contains(searchText, ignoreCase = true) ||
                repo.category.contains(searchText, ignoreCase = true)

            val favoriteMatch = !showFavoritesOnly || repo.stars > 0

            val categoryMatch =
                selectedCategory == "Tümü" ||
                        repo.category ==
                        selectedCategory

            val platformMatch = when (selectedPlatform) {
                "CloudStream" -> repo.type.lowercase() != "nuvio"
                "Nuvio" -> repo.type.lowercase() == "nuvio"
                else -> true
            }

            searchMatch &&
                    favoriteMatch &&
                    categoryMatch &&
                    platformMatch
        }.let { list ->
            if (sortByStars) list.sortedByDescending { it.stars } else list
        }

    val favoriteCount = repos.count { it.stars == 3 }

    val categoryCount =
        repos
            .map {
                it.category.trim()
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .size

    val validRepoCount =
        checkResults.values.count {
            it.startsWith("🟢")
        }

    val warningCount =
        checkResults.values.count {
            it.startsWith("🟡")
        }

    val failedCount =
        checkResults.values.count {
            it.startsWith("🔴") ||
                    it.startsWith("🟠")
        }

    /* =====================================================
       EKRAN
       ===================================================== */

    val listState = rememberLazyListState()

    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                color = CyberBgDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // SOL: Logo + REPO Başlığı + Alt Başlık
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Yeni Logo Bileşeni (Ayrı Resource'tan çekilen, bozulmayan yapı)
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .background(CyberCardDark, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "REPO",
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberTextPrimary,
                                fontSize = 24.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "CLOUDSTREAM DEPOLARI",
                                color = CyberTextSecondary,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // SAĞ: Kontroller (Arama, Senkronizasyon, Telegram, Ayarlar)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TvIconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Text("🔍", fontSize = 20.sp)
                        }
                        TvIconButton(
                            onClick = {
                                if (repos.isNotEmpty() && !checkingAll) {
                                    checkingAll = true
                                    checkResults.clear()
                                    checkAllRepoUrls(
                                        repos = repos.toList(),
                                        onStart = { checkingMessage = it },
                                        onResult = { url, res -> checkResults[url] = res },
                                        onFinished = { checkingAll = false }
                                    )
                                }
                            },
                            enabled = !checkingAll
                        ) {
                            Text(if (checkingAll) "⏳" else "🔄", fontSize = 18.sp)
                        }
                        TvIconButton(onClick = { openTelegramChannel(context) }) {
                            // Telegram için uygun renkli ikon temsilcisi
                            Text("✈️", fontSize = 18.sp)
                        }
                        // Bulut Senkronizasyonu Geri Getirildi (Admin için publish, User için fetch)
                        TvIconButton(onClick = {
                            if (ENABLE_ADMIN_PANEL_FEATURE && isAdminLoggedIn) {
                                isPublishingToCloud = true
                                Toast.makeText(context, "Buluta kaydediliyor...", Toast.LENGTH_SHORT).show()
                                CentralRepoApiManager.publishCentralReposToCloud(context, repos.toList(), "") { _, _ ->
                                    isPublishingToCloud = false
                                    Toast.makeText(context, "💾 Bulut verisi güncellendi!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Buluttan veriler senkronize ediliyor...", Toast.LENGTH_SHORT).show()
                                CentralRepoApiManager.fetchCentralRepos(context) { liveRepos ->
                                    if (!liveRepos.isNullOrEmpty()) {
                                        val cleanRepos = filterDeletedRepos(context, liveRepos)
                                        repos.clear()
                                        repos.addAll(cleanRepos)
                                        saveRepos(context, cleanRepos)
                                        Toast.makeText(context, "✅ Repolar senkronize edildi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }) {
                            Text(if (isPublishingToCloud) "⏳" else "☁️", fontSize = 18.sp)
                        }
                        TvIconButton(onClick = { showSettingsDialog = true }) {
                            Text("⚙️", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
        // ALT NAVİGASYON (bottomBar) TAMAMEN KALDIRILDI.
    ) { padding ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (selectedBottomTab == "Ayarlar") {
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(3.dp)
                                            .background(CyberAccent, RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                } else {
                                    Spacer(modifier = Modifier.height(9.dp))
                                }
                                Text("⚙️", fontSize = 22.sp) // Sistem (settings gear icon approximation)
                            }
                        },
                        label = {
                            Text(
                                "SİSTEM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedBottomTab == "Ayarlar") CyberAccent else CyberTextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberAccent,
                            unselectedIconColor = CyberTextSecondary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->

        if (selectedBottomTab == "Ayarlar") {
            // SİSTEM EKRANI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(CyberBgDark)
            ) {
                // SİSTEM Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("SİSTEM", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                        Text("GÖRÜNÜM", fontSize = 11.sp, color = CyberTextSecondary, letterSpacing = 1.5.sp)
                    }

                    // Admin Profil
                    if (ENABLE_ADMIN_PANEL_FEATURE) {
                        Surface(
                            color = CyberSurfaceDark,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, CyberAccent.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .height(36.dp)
                                .clickable {
                                    if (isAdminLoggedIn) {
                                        adminAuthManager.logoutAdmin()
                                        isAdminLoggedIn = false
                                        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showAdminLoginDialog = true
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👤", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isAdminLoggedIn) "Admin" else "Giriş Yap", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CyberTextPrimary)
                            }
                        }
                    }
                }

                // PANEL GÖRÜNÜMÜ Header
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(18.dp).background(CyberAccent, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                        Text("i", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PANEL GÖRÜNÜMÜ", fontSize = 11.sp, color = CyberTextSecondary, letterSpacing = 1.sp)
                }

                // Tema Seçici
                Text("PANEL TEMASI", fontSize = 10.sp, color = CyberTextSecondary, letterSpacing = 1.2.sp, modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp))
                
                var showThemePicker by remember { mutableStateOf(false) }
                
                Surface(
                    color = CyberSurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyberTextSecondary.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp)
                        .clickable { showThemePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎨", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            val currentThemeName = when(currentThemeMode) {
                                "Camel" -> "Camel (Sıcak)"
                                "Indigo" -> "Indigo (Soğuk)"
                                else -> "Darknes Purple (Varsayılan)"
                            }
                            Text(currentThemeName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CyberTextPrimary)
                        }
                        Text("▼", fontSize = 16.sp, color = CyberTextSecondary)
                    }
                }

                // Tema Seçici Modal (BottomSheet alternatifi)
                if (showThemePicker) {
                    AlertDialog(
                        onDismissRequest = { showThemePicker = false },
                        containerColor = CyberSurfaceDark, // Use current theme's surface
                        title = { Text("Panel Teması", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CyberTextPrimary) },
                        text = {
                            Column {
                                val themes = listOf(
                                    "Darknes Purple" to "Darknes Purple (Varsayılan)",
                                    "Camel" to "Camel (Sıcak)",
                                    "Indigo" to "Indigo (Soğuk)"
                                )
                                themes.forEach { (themeKey, themeName) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .clickable {
                                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                                    .edit().putString("theme_mode", themeKey).apply()
                                                currentThemeMode = themeKey
                                                applyThemeColors(themeKey)
                                                showThemePicker = false
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(36.dp).background(CyberCardDark, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                                            Text("🎨")
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(themeName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CyberTextPrimary, modifier = Modifier.weight(1f))
                                        if (currentThemeMode == themeKey || (currentThemeMode == "Cyber" && themeKey == "Darknes Purple")) {
                                            Text("🟢", fontSize = 14.sp) // Radio selected equivalent
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemePicker = false }) { Text("KAPAT", color = CyberAccent) }
                        }
                    )
                }

                // Diğer Ayarlar (Görsel tutarlılık için)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(18.dp).background(CyberAccent, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                        Text("i", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UYGULAMA BİLGİSİ VE DİĞER", fontSize = 11.sp, color = CyberTextSecondary, letterSpacing = 1.sp)
                }
                
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    TvOutlinedButton(onClick = { showAnnouncementsDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("📢 Duyurular", fontSize = 12.sp, color = CyberTextPrimary) }
                    Spacer(Modifier.height(8.dp))
                    TvOutlinedButton(onClick = {
                        AppUpdateManager.checkForUpdates(BuildConfig.VERSION_NAME) { info ->
                            if (info != null && info.isUpdateAvailable) {
                                updateInfo = info
                                showUpdateDialog = true
                            } else {
                                Toast.makeText(context, "Zaten en güncel sürümdesiniz.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("🔄 Güncellemeleri Kontrol Et", fontSize = 12.sp, color = CyberTextPrimary) }
                    Spacer(Modifier.height(8.dp))
                    TvOutlinedButton(onClick = { backupLauncher.launch("cloudstream_repos_backup.json") }, modifier = Modifier.fillMaxWidth()) { Text("💾 Yedekle", fontSize = 12.sp, color = CyberTextPrimary) }
                    Spacer(Modifier.height(8.dp))
                    TvOutlinedButton(onClick = { restoreLauncher.launch(arrayOf("application/json", "text/json", "text/plain", "*/*")) }, modifier = Modifier.fillMaxWidth()) { Text("📥 Yedeği Geri Yükle", fontSize = 12.sp, color = CyberTextPrimary) }
                }

                Spacer(modifier = Modifier.weight(1f))
                Text("PANEL BY DARKNES LORD", fontSize = 11.sp, color = CyberAccent, letterSpacing = 1.5.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), textAlign = TextAlign.Center)
            }
        } else {
            // REPO EKRANI (Ana Liste)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(CyberBgDark)
            ) {
                // Arka plan büyük repo yazısı (Decorative)
                Text(
                    text = "REPOLAR",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberTextSecondary.copy(alpha = 0.05f),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp),
                    letterSpacing = 5.sp
                )

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Arama Çubuğu (Sabit)
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = { Text("Repo ara...", fontSize = 13.sp, color = CyberTextSecondary) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, color = CyberTextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberAccent,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = CyberSurfaceDark,
                            unfocusedContainerColor = CyberSurfaceDark,
                        ),
                        leadingIcon = { Text("🔎", fontSize = 14.sp) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Text("✖️", fontSize = 12.sp)
                                }
                            }
                        }
                    )

                    // Bottom Sheet benzeri alan (Persistent)
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = CyberSurfaceDark,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                        ) {
                            // Drag handle
                            Box(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .width(36.dp)
                                    .height(4.dp)
                                    .background(CyberTextSecondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .align(Alignment.CenterHorizontally)
                            )

                            // Title
                            Text(
                                text = "Kayıtlı Repolar",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary,
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                            )
                            
                            // Info / Count
                            val filteredCount = filteredRepos.size
                            Text(
                                text = "cloudstream.nuvio.app",
                                fontSize = 11.sp,
                                color = CyberTextSecondary,
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                            )
                            Text(
                                text = "$filteredCount eklenti bulundu",
                                fontSize = 11.sp,
                                color = CyberTextSecondary,
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp, bottom = 16.dp)
                            )

                            // Filtreler (Tümü / Favoriler / CS / Nuvio)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Tümü
                                Surface(
                                    color = if (selectedPlatform == "Tümü" && !showFavoritesOnly) CyberAccent else CyberCardDark,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, if (selectedPlatform == "Tümü" && !showFavoritesOnly) CyberAccent else CyberBorder),
                                    modifier = Modifier.clickable { 
                                        selectedPlatform = "Tümü" 
                                        showFavoritesOnly = false
                                    }
                                ) {
                                    Text(
                                        text = "Tümü",
                                        color = if (selectedPlatform == "Tümü" && !showFavoritesOnly) Color.White else CyberTextSecondary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                // Favoriler
                                Surface(
                                    color = if (showFavoritesOnly) CyberAccent else CyberCardDark,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, if (showFavoritesOnly) CyberAccent else CyberBorder),
                                    modifier = Modifier.clickable { 
                                        showFavoritesOnly = true 
                                        selectedPlatform = "Tümü"
                                    }
                                ) {
                                    Text(
                                        text = "Favoriler",
                                        color = if (showFavoritesOnly) Color.White else CyberTextSecondary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                Surface(
                                    color = if (selectedPlatform == "CloudStream" && !showFavoritesOnly) CyberAccent else CyberCardDark,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, if (selectedPlatform == "CloudStream" && !showFavoritesOnly) CyberAccent else CyberBorder),
                                    modifier = Modifier.clickable { 
                                        selectedPlatform = "CloudStream" 
                                        showFavoritesOnly = false
                                    }
                                ) {
                                    Text(
                                        text = "CloudStream",
                                        color = if (selectedPlatform == "CloudStream" && !showFavoritesOnly) Color.White else CyberTextSecondary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                Text(
                                    text = "Nuvio",
                                    color = if (selectedPlatform == "Nuvio" && !showFavoritesOnly) Color.White else CyberTextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

            val configuration = LocalConfiguration.current
            val isTvOrTablet = configuration.screenWidthDp >= 600
            val screenWidth = configuration.screenWidthDp.dp

            val columns = when {
                screenWidth >= 1100.dp -> 3
                screenWidth >= 700.dp -> 2
                else -> 1
            }

            val chunkedRepos = remember(filteredRepos.toList(), columns) {
                filteredRepos.chunked(columns)
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredRepos.isEmpty()) {
                    item(key = "empty") {
                        EmptyRepoCard(hasRepos = repos.isNotEmpty())
                    }
                } else {
                    items(
                        items = chunkedRepos,
                        key = { row -> row.joinToString { it.url } }
                    ) { rowRepos ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowRepos.forEach { repo ->
                                Column(modifier = Modifier.weight(1f)) {
                                    RepoCard(
                                        repo = repo,
                                        isTvOrTablet = isTvOrTablet,
                                        checkResult = checkResults[repo.url],
                                        onStarsChange = { newStars ->
                                            val index = repos.indexOf(repo)
                                            if (index >= 0) {
                                                repos[index] = repo.copy(stars = newStars)
                                                saveRepos(context, repos)
                                            }
                                        },
                                        onEdit = {
                                            selectedRepo = repo
                                            showEditDialog = true
                                        },
                                        onDelete = {
                                            repoToDelete = repo
                                            showDeleteDialog = true
                                        },
                                        onCopyLink = { copyToClipboard(context, repo.url, "Repo linki kopyalandı") },
                                        onCopyCode = { copyToClipboard(context, repo.code, "Kısa kod kopyalandı") },
                                        onAddToCloudStream = {
                                            if (repo.type.lowercase() == "nuvio") {
                                                openNuvioAndPrepareRepo(
                                                    context,
                                                    repo,
                                                    onNotInstalled = { showNuvioNotInstalledDialog = true }
                                                )
                                            } else {
                                                openCloudStreamAndPrepareRepo(
                                                    context,
                                                    repo,
                                                    onNotInstalled = { showCloudStreamNotInstalledDialog = true }
                                                )
                                            }
                                        },
                                        onCheck = {
                                            checkResults[repo.url] = "⏳ Kontrol ediliyor..."
                                            checkRepoUrl(repo.url) { result -> checkResults[repo.url] = result }
                                        },
                                        onDetailClick = {
                                            selectedRepoForDetail = repo
                                        }
                                    )
                                }
                            }
                            repeat(columns - rowRepos.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        } // End Column of Bottom Sheet
    } // End Surface of Bottom Sheet
    } // End Column of Ana Liste
    } // End Box of Ana Liste
    } // End of Scaffold content block

    if (selectedRepoForDetail != null) {
        RepoDetailDialog(
            repo = selectedRepoForDetail!!,
            checkResult = checkResults[selectedRepoForDetail!!.url],
            onDismiss = { selectedRepoForDetail = null },
            onStarsChange = { newStars ->
                val index = repos.indexOf(selectedRepoForDetail!!)
                if (index >= 0) {
                    repos[index] = selectedRepoForDetail!!.copy(stars = newStars)
                    selectedRepoForDetail = repos[index]
                    saveRepos(context, repos)
                }
            },
            onTransfer = {
                val repo = selectedRepoForDetail!!
                if (repo.type.lowercase() == "nuvio") {
                    openNuvioAndPrepareRepo(context, repo, onNotInstalled = { showNuvioNotInstalledDialog = true })
                } else {
                    openCloudStreamAndPrepareRepo(context, repo, onNotInstalled = { showCloudStreamNotInstalledDialog = true })
                }
            },
            onCheck = {
                val repo = selectedRepoForDetail!!
                checkResults[repo.url] = "⏳ Kontrol ediliyor..."
                checkRepoUrl(repo.url) { result -> checkResults[repo.url] = result }
            },
            onCopyLink = {
                val repo = selectedRepoForDetail!!
                copyToClipboard(context, repo.url, "Repo linki kopyalandı")
            },
            onDelete = if (ENABLE_ADMIN_PANEL_FEATURE) {
                {
                    repoToDelete = selectedRepoForDetail!!
                    selectedRepoForDetail = null
                    showDeleteDialog = true
                }
            } else null
        )
    }

    /* =========================================================
       EKLE
       ========================================================= */

    if (showTelegramInvite) {
        TelegramInviteDialog(
            onJoin = {
                showTelegramInvite = false
                openTelegramChannel(context)
            },
            onLater = { showTelegramInvite = false },
            onNeverShow = {
                showTelegramInvite = false
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit().putBoolean(TELEGRAM_INVITE_HIDDEN_KEY, true).apply()
            }
        )
    }

    // Yeni duyuru penceresi (Telegram daveti kapandıktan sonra gösterilir)
    val popup = announcementPopup
    if (popup != null && !showTelegramInvite) {
        AnnouncementPopupDialog(
            announcement = popup,
            onOpenLink = {
                AnnouncementManager.markSeen(context, popup)
                announcementPopup = null
                openLink(context, popup.link)
            },
            onDismiss = {
                AnnouncementManager.markSeen(context, popup)
                announcementPopup = null
            }
        )
    }

    if (showAnnouncementsDialog) {
        AnnouncementsListDialog(
            announcements = announcements.toList(),
            onOpenLink = { link -> openLink(context, link) },
            onDismiss = { showAnnouncementsDialog = false }
        )
    }

    if (showAdminAnnouncementsDialog) {
        AdminAnnouncementsDialog(
            announcements = announcements.toList(),
            publishing = publishingAnnouncements,
            onPublish = { newList ->
                publishingAnnouncements = true
                AnnouncementManager.publish(context, newList) { ok, msg ->
                    publishingAnnouncements = false
                    if (ok) {
                        announcements.clear()
                        announcements.addAll(newList)
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            },
            onDismiss = { showAdminAnnouncementsDialog = false }
        )
    }

    if (showSettingsDialog) {

        SettingsDialog(

            onAnnouncements = {
                showSettingsDialog = false
                showAnnouncementsDialog = true
            },

            onDismiss = {
                showSettingsDialog = false
                // update theme immediately locally 
                currentThemeMode = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString("theme_mode", "Cyber") ?: "Cyber"
            },

            onCheckAll = {

                if (repos.isEmpty()) {

                    Toast.makeText(
                        context,
                        "Kontrol edilecek repo yok",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (!checkingAll) {

                    checkingAll = true
                    checkResults.clear()
                    checkingMessage = "Kontrol başlatılıyor..."

                    checkAllRepoUrls(
                        repos = repos.toList(),
                        onStart = { message ->
                            checkingMessage = message
                        },
                        onResult = { url, result ->
                            checkResults[url] = result
                        },
                        onFinished = {
                            checkingAll = false
                            checkingMessage = "✅ Tüm repo kontrolleri tamamlandı"

                            Toast.makeText(
                                context,
                                "Tüm linkler kontrol edildi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    showSettingsDialog = false
                }
            },

            onBackup = {
                backupLauncher.launch("cloudstream_repos_backup.json")
                showSettingsDialog = false
            },

            onRestore = {
                restoreLauncher.launch(
                    arrayOf(
                        "application/json",
                        "text/json",
                        "text/plain",
                        "*/*"
                    )
                )
                showSettingsDialog = false
            },

            onRestoreDefaults = {
                val defaultList = getDefaultRepos()
                repos.clear()
                repos.addAll(defaultList)
                saveRepos(context, defaultList)
                checkResults.clear()
                showSettingsDialog = false
                Toast.makeText(
                    context,
                    "Hazır repolar başarıyla yüklendi",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onCheckAppUpdate = {
                checkingUpdate = true

                // 1. Uygulama Versiyon Kontrolü
                AppUpdateManager.checkForUpdates(BuildConfig.VERSION_NAME) { info ->
                    checkingUpdate = false
                    if (info != null && info.isUpdateAvailable) {
                        updateInfo = info
                        showUpdateDialog = true
                        showSettingsDialog = false
                    } else {
                        // 2. Yeni APK yoksa Merkezi Repo Kataloğu Güncelleme Kontrolü yap ve bildir
                        CentralRepoApiManager.checkForRepoCatalogUpdates(context, repos.toList()) { hasCatalogUpdate, liveRepos, catalogMsg ->
                            if (hasCatalogUpdate && liveRepos != null) {
                                repos.clear()
                                repos.addAll(liveRepos)
                                saveRepos(context, liveRepos)
                                Toast.makeText(context, catalogMsg, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, catalogMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
    }

    if (showAddDialog) {
        AddRepoDialog(
            onDismiss = {
                showAddDialog = false
            },
            onAdd = { name, url, code, category, type ->
                val cleanName = name.trim()
                val cleanUrl = url.trim()
                val cleanCode = code.trim()
                val cleanCategory = category.trim().ifBlank { "Genel" }
                val cleanType = type.trim().ifBlank { "cloudstream" }

                when {
                    cleanName.isBlank() -> {
                        Toast.makeText(context, "Repo adı boş bırakılamaz", Toast.LENGTH_SHORT).show()
                    }
                    cleanUrl.isBlank() -> {
                        Toast.makeText(context, "Repo linki boş bırakılamaz", Toast.LENGTH_SHORT).show()
                    }
                    !cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://") -> {
                        Toast.makeText(context, "Link http:// veya https:// ile başlamalı", Toast.LENGTH_SHORT).show()
                    }
                    repos.any { it.url.equals(cleanUrl, ignoreCase = true) } -> {
                        Toast.makeText(context, "Bu repo linki zaten kayıtlı", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val newRepo = Repo(
                            name = cleanName,
                            url = cleanUrl,
                            code = cleanCode,
                            category = cleanCategory,
                            type = cleanType
                        )

                        repos.add(0, newRepo)
                        saveRepos(context, repos)

                        if (isAdminLoggedIn) {
                            Toast.makeText(context, "Repo eklendi, GitHub'a senkronize ediliyor...", Toast.LENGTH_SHORT).show()
                            CentralRepoApiManager.publishCentralReposToCloud(
                                context,
                                repos.toList()
                            ) { success, msg -> 
                                val statusMsg = if (success) "✅ GitHub senkronizasyonu başarılı!" else "❌ Senkronizasyon hatası: $msg"
                                Toast.makeText(context, statusMsg, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Repo eklendi", Toast.LENGTH_SHORT).show()
                        }

                        showAddDialog = false
                    }
                }
            }
        )
    }

    /* =========================================================
       DÜZENLE
       ========================================================= */

    if (showEditDialog && selectedRepo != null) {
        EditRepoDialog(
            repo = selectedRepo!!,
            onDismiss = {
                showEditDialog = false
                selectedRepo = null
            },
            onSave = { name, url, code, category, type ->
                val oldRepo = selectedRepo
                if (oldRepo != null) {
                    val cleanName = name.trim()
                    val cleanUrl = url.trim()
                    val cleanCode = code.trim()
                    val cleanCategory = category.trim().ifBlank { "Genel" }
                    val cleanType = type.trim().ifBlank { "cloudstream" }

                    when {
                        cleanName.isBlank() -> {
                            Toast.makeText(context, "Repo adı boş bırakılamaz", Toast.LENGTH_SHORT).show()
                        }
                        cleanUrl.isBlank() -> {
                            Toast.makeText(context, "Repo linki boş bırakılamaz", Toast.LENGTH_SHORT).show()
                        }
                        !cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://") -> {
                            Toast.makeText(context, "Link http:// veya https:// ile başlamalı", Toast.LENGTH_SHORT).show()
                        }
                        repos.any { it != oldRepo && it.url.equals(cleanUrl, ignoreCase = true) } -> {
                            Toast.makeText(context, "Bu repo linki başka bir kayıtta kullanılıyor", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val index = repos.indexOf(oldRepo)
                            if (index >= 0) {
                                checkResults.remove(oldRepo.url)
                                val updatedRepo = oldRepo.copy(
                                    name = cleanName,
                                    url = cleanUrl,
                                    code = cleanCode,
                                    category = cleanCategory,
                                    type = cleanType
                                )
                                repos[index] = updatedRepo
                                saveRepos(context, repos)

                                if (isAdminLoggedIn) {
                                    Toast.makeText(context, "Repo güncellendi, GitHub'a senkronize ediliyor...", Toast.LENGTH_SHORT).show()
                                    CentralRepoApiManager.publishCentralReposToCloud(
                                        context,
                                        repos.toList()
                                    ) { success, msg -> 
                                        val statusMsg = if (success) "✅ GitHub senkronizasyonu başarılı!" else "❌ Senkronizasyon hatası: $msg"
                                        Toast.makeText(context, statusMsg, Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Repo güncellendi", Toast.LENGTH_SHORT).show()
                                }
                            }
                            showEditDialog = false
                            selectedRepo = null
                        }
                    }
                }
            }
        )
    }

    /* =========================================================
       SİLME
       ========================================================= */

    if (
        showDeleteDialog &&
        repoToDelete != null
    ) {

        DeleteRepoDialog(

            repo =
                repoToDelete!!,

            onDismiss = {

                showDeleteDialog = false
                repoToDelete = null
            },

            onConfirm = {

                val repo =
                    repoToDelete

                if (repo != null) {

                    addDeletedUrl(context, repo.url)

                    repos.remove(repo)

                    checkResults.remove(
                        repo.url
                    )

                    saveRepos(
                        context,
                        repos
                    )

                    if (ENABLE_ADMIN_PANEL_FEATURE && isAdminLoggedIn) {
                        Toast.makeText(context, "Repo silindi, GitHub'a senkronize ediliyor...", Toast.LENGTH_SHORT).show()
                        CentralRepoApiManager.publishCentralReposToCloud(
                            context,
                            repos.toList()
                        ) { success, msg ->
                            val statusMsg = if (success) "✅ GitHub senkronizasyonu başarılı!" else "❌ Senkronizasyon hatası: $msg"
                            Toast.makeText(context, statusMsg, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Repo kalıcı olarak silindi ve önbellek temizlendi.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                showDeleteDialog = false
                repoToDelete = null
            }
        )
    }

    /* =========================================================
       CLOUDSTREAM YÜKLÜ DEĞİL DİYALOĞU
       ========================================================= */

    if (showCloudStreamNotInstalledDialog) {

        AlertDialog(
            onDismissRequest = {
                showCloudStreamNotInstalledDialog = false
            },
            title = {
                Text(
                    "⚠️ CloudStream Bulunamadı",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Bu cihazda CloudStream uygulaması yüklü görünmüyor.\n\n" +
                            "• Repo adresi panoya kopyalandı.\n" +
                            "• Dilerseniz aşağıdaki butondan CloudStream'in resmi indirme sayfasına gidebilirsiniz."
                )
            },
            confirmButton = {
                TvButton(
                    onClick = {
                        try {
                            val downloadIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/recloudstream/cloudstream/releases")
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(downloadIntent)
                        } catch (_: Exception) {
                        }
                        showCloudStreamNotInstalledDialog = false
                    }
                ) {
                    Text("📥 CloudStream İndir (GitHub)")
                }
            },
            dismissButton = {
                TvOutlinedButton(
                    onClick = {
                        showCloudStreamNotInstalledDialog = false
                    }
                ) {
                    Text("Tamam")
                }
            }
        )
    }

    if (showNuvioNotInstalledDialog) {
        AlertDialog(
            onDismissRequest = {
                showNuvioNotInstalledDialog = false
            },
            title = {
                Text(
                    "⚠️ Nuvio Bulunamadı",
                    fontWeight = FontWeight.Bold,
                    color = CyberYellow
                )
            },
            text = {
                Text(
                    "Bu cihazda Nuvio uygulaması yüklü görünmüyor.\n\n" +
                            "• Repo adresi panoya kopyalandı.\n" +
                            "• Nuvio uygulamasını kurduktan sonra Eklentiler / Repo Ekle bölümünden adresi yapıştırabilirsiniz."
                )
            },
            confirmButton = {
                TvButton(
                    onClick = {
                        showNuvioNotInstalledDialog = false
                    }
                ) {
                    Text("Tamam")
                }
            }
        )
    }

    if (showFixExtractorsDialog) {
        FixExtractorsDialog(
            onDismiss = {
                showFixExtractorsDialog = false
            },
            onInstallExtractors = {
                val extractorsRepo = Repo(
                    name = "Hexated Extractors Repo",
                    url = "https://raw.githubusercontent.com/hexated/cloudstream-extensions-hexated/builds/repo.json",
                    code = "extractors",
                    category = "Extractors"
                )
                openCloudStreamAndPrepareRepo(
                    context,
                    extractorsRepo,
                    onNotInstalled = {
                        showCloudStreamNotInstalledDialog = true
                    }
                )
                showFixExtractorsDialog = false
            }
        )
    }

    /* =========================================================
       GÜNCELLEME DİYALOĞU
       ========================================================= */

    if (showUpdateDialog && updateInfo != null) {

        AlertDialog(
            onDismissRequest = {
                showUpdateDialog = false
            },
            title = {
                Text(
                    "🚀 YENİ SÜRÜM MEVCUT!",
                    fontWeight = FontWeight.Bold,
                    color = CyberYellow
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Yeni Sürüm: v${updateInfo!!.latestVersionTag}",
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                    Text(
                        "Mevcut Sürüm: v${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                    HorizontalDivider()
                    Text(
                        "Yenilikler ve Notlar:",
                        fontWeight = FontWeight.SemiBold,
                        color = CyberTextPrimary
                    )
                    Text(
                        updateInfo!!.releaseNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                }
            },
            confirmButton = {
                TvButton(
                    onClick = {
                        AppUpdateManager.downloadAndInstallUpdate(context, updateInfo!!.downloadUrl)
                        showUpdateDialog = false
                    }
                ) {
                    Text("📥 Şimdi Güncelle (APK İndir)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TvOutlinedButton(
                    onClick = {
                        showUpdateDialog = false
                    }
                ) {
                    Text("Daha Sonra")
                }
            }
        )
    }

    /* =========================================================
       ADMIN GİRİŞ DİYALOĞU
       ========================================================= */

    if (showAdminLoginDialog) {

        AdminLoginDialog(
            currentGithubToken = adminAuthManager.adminGithubToken,
            onDismiss = {
                showAdminLoginDialog = false
            },
            onLoginSuccess = { token ->
                adminAuthManager.isAdminLoggedIn = true
                if (token.isNotBlank()) {
                    adminAuthManager.adminGithubToken = token
                }
                isAdminLoggedIn = true
                showAdminLoginDialog = false
                Toast.makeText(context, "👑 Admin paneline giriş yapıldı!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/* =========================================================
   ADMIN GİRİŞ DİYALOĞU BİLEŞENİ
   ========================================================= */

@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    currentGithubToken: String = ""
) {
    var enteredPin by remember { mutableStateOf("") }
    var enteredToken by remember { mutableStateOf(currentGithubToken) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("👑 Admin Panel Girişi", fontWeight = FontWeight.Bold, color = CyberYellow)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Değişiklikleri tüm kullanıcılara canlı yayınlamak için Admin PIN ve GitHub Tokeninizi girin:",
                    color = CyberTextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = { enteredPin = it },
                    label = { Text("Admin PIN") },
                    placeholder = { Text("Varsayılan PIN: 1907") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberYellow,
                        unfocusedBorderColor = CyberBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = enteredToken,
                    onValueChange = { enteredToken = it },
                    label = { Text("GitHub Token (Bulut Eşitleme İçin)") },
                    placeholder = { Text("ghp_...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage.isNotBlank()) {
                    Text(errorMessage, color = CyberPink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TvButton(
                onClick = {
                    if (enteredPin.trim() == "1907" || enteredPin.trim() == "admin123") {
                        onLoginSuccess(enteredToken.trim())
                    } else {
                        errorMessage = "❌ Hatalı Admin PIN Kodu! (Varsayılan PIN: 1907)"
                    }
                }
            ) {
                Text("Giriş Yap", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TvOutlinedButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

/* =========================================================
   OYNATMA SORUNU ÇÖZÜCÜ DİYALOĞU
   ========================================================= */

@Composable
fun FixExtractorsDialog(
    onDismiss: () -> Unit,
    onInstallExtractors: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "🛠️ OYNATMA / LİNK ÇÖZÜCÜ",
                fontWeight = FontWeight.Bold,
                color = CyberYellow
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Film açıldığında 'Bağlantı Bulunamadı' uyarısını düzeltmek için 2 adım:",
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider()

                Text(
                    "ADIM 1: Oynatıcı Çözücülerini Yükle",
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                Text(
                    "Vidmoly, Doodstream, Filemoon gibi video oynatıcı çözücü eklentilerini CloudStream'e yükler.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberTextSecondary
                )

                TvButton(
                    onClick = onInstallExtractors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚡ EXTRACTORS REPOSUNU YÜKLE", fontWeight = FontWeight.Bold)
                }

                HorizontalDivider()

                Text(
                    "ADIM 2: DNS Over HTTPS (DoH) Açın",
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                Text(
                    "Türkiye internet engellerini aşmak için CloudStream içinden DNS değiştirmeniz şarttır:\n\n" +
                            "1. CloudStream'i açın ➔ Sağ alttan 'Ayarlar (⚙️)' seçin.\n" +
                            "2. 'Ağ (Network)' sekmesine girin.\n" +
                            "3. 'DNS over HTTPS (DoH)' seçeneğini 'Cloudflare (1.1.1.1)' yapın.\n" +
                            "4. Uygulamayı yeniden başlatın.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberYellow
                )
            }
        },
        confirmButton = {
            TvButton(onClick = onDismiss) {
                Text("Anladım")
            }
        }
    )
}

/* =========================================================
   İSTATİSTİK KARTI
   ========================================================= */

@Composable
fun StatCard(
    emoji: String,
    value: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, CyberBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(emoji, style = MaterialTheme.typography.titleMedium)
            Column {
                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    color = CyberYellow,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = title,
                    color = CyberTextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

/* =========================================================
   BOŞ LİSTE
   ========================================================= */

@Composable
fun EmptyRepoCard(
    hasRepos: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = if (hasRepos) "🔎 REPO BULUNAMADI" else "📦 REPO LİSTESİ BOŞ",
                fontWeight = FontWeight.Bold,
                color = CyberYellow
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (hasRepos)
                    "Arama veya kategori filtresini değiştirmeyi deneyebilirsiniz."
                else
                    "Başlamak için '+ Repo Ekle' butonunu kullanabilirsiniz.",
                color = CyberTextSecondary
            )
        }
    }
}

/* =========================================================
   REPO DETAY DİYALOĞU
   ========================================================= */

@Composable
fun RepoDetailDialog(
    repo: Repo,
    checkResult: String?,
    onDismiss: () -> Unit,
    onStarsChange: (Int) -> Unit,
    onTransfer: () -> Unit,
    onCheck: () -> Unit,
    onCopyLink: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isNuvio = repo.type.lowercase() == "nuvio"
    val platformName = if (isNuvio) "NUVIO" else "CLOUDSTREAM"
    val platformColor = if (isNuvio) CyberPink else CyberCyan

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(CyberSurfaceDark, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isNuvio) "🟣" else "📦", fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = repo.name,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary,
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(color = CyberCardDark, shape = RoundedCornerShape(4.dp)) {
                                    Text("[ ${repo.category.uppercase()} ]", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                                Surface(color = CyberCardDark, shape = RoundedCornerShape(4.dp)) {
                                    Text("[ $platformName ]", color = platformColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 3 Stars Rating
                Text("Değerlendirme:", fontSize = 12.sp, color = CyberTextSecondary)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..3).forEach { starIndex ->
                        TvIconButton(
                            onClick = {
                                val newStars = if (repo.stars == starIndex) 0 else starIndex
                                onStarsChange(newStars)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text(if (repo.stars >= starIndex) "★" else "☆", color = CyberYellow, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Big Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TvButton(
                        onClick = onTransfer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isNuvio) "🟣 Nuvio'ya Aktar" else "☁️ CS'ye Aktar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    TvOutlinedButton(
                        onClick = onCheck,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🛡️ Kontrol Et", fontSize = 12.sp)
                    }
                }

                if (checkResult != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = checkResult,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            checkResult.startsWith("🟢") -> CyberGreen
                            checkResult.startsWith("🟡") -> CyberYellow
                            else -> CyberPink
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = CyberBorder)
                Spacer(Modifier.height(16.dp))

                // Information Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📋 Repo Bilgileri", fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 14.sp)

                    Surface(
                        color = CyberCardDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Dil / Kategori:", fontSize = 12.sp, color = CyberTextSecondary)
                                Text(repo.category, fontSize = 12.sp, color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Platform:", fontSize = 12.sp, color = CyberTextSecondary)
                                Text(platformName, fontSize = 12.sp, color = platformColor, fontWeight = FontWeight.Bold)
                            }
                            if (ENABLE_ADMIN_PANEL_FEATURE) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Repo URL:", fontSize = 12.sp, color = CyberTextSecondary)
                                    Text(if (repo.url.length > 22) repo.url.take(22) + "..." else repo.url, fontSize = 11.sp, color = CyberCyan)
                                }
                            }
                        }
                    }

                    TvOutlinedButton(
                        onClick = onCopyLink,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔗 Bağlantıyı Panoya Kopyala", fontSize = 12.sp)
                    }

                    if (ENABLE_ADMIN_PANEL_FEATURE && onDelete != null) {
                        Button(
                            onClick = onDelete,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🗑️ Repoyu Sil", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Kapat")
            }
        }
    )
}

/* =========================================================
   REPO KARTI
   ========================================================= */

@Composable
fun RepoCard(
    repo: Repo,
    isTvOrTablet: Boolean = false,
    checkResult: String?,
    onStarsChange: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyLink: () -> Unit,
    onCopyCode: () -> Unit,
    onAddToCloudStream: () -> Unit,
    onCheck: () -> Unit,
    onDetailClick: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) CyberCyan else CyberBorder
    val isNuvio = repo.type.lowercase() == "nuvio"
    val platformName = if (isNuvio) "NUVIO" else "CLOUDSTREAM"
    val platformColor = if (isNuvio) CyberPink else CyberCyan
    
    val baseTextSize = if (isTvOrTablet) 14.sp else 10.sp
    val titleTextSize = if (isTvOrTablet) 18.sp else 14.sp
    val buttonTextSize = if (isTvOrTablet) 12.sp else 11.sp
    val paddingSize = if (isTvOrTablet) 20.dp else 16.dp
    val iconSize = if (isTvOrTablet) 48.dp else 36.dp
    val starSize = if (isTvOrTablet) 36.dp else 32.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .border(if (isFocused) 3.dp else 1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onDetailClick() },
        colors = CardDefaults.cardColors(containerColor = CyberCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 8.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(paddingSize)) {
            // Header: Name + Favorite Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Icon placeholder
                    Box(
                        modifier = Modifier
                            .size(iconSize)
                            .background(CyberSurfaceDark, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isNuvio) "🟣" else "📦", fontSize = if (isTvOrTablet) 24.sp else 16.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = repo.name,
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = titleTextSize,
                            maxLines = 1
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "[ ${repo.category.uppercase()} ]",
                                color = CyberCyan,
                                fontSize = baseTextSize,
                                fontWeight = FontWeight.Bold
                            )
                            Text("•", color = CyberTextSecondary, fontSize = baseTextSize)
                            Text(
                                text = "[ $platformName ]",
                                color = platformColor,
                                fontSize = baseTextSize,
                                fontWeight = FontWeight.Bold
                            )
                            if (ENABLE_ADMIN_PANEL_FEATURE && repo.code.isNotBlank()) {
                                Text("•", color = CyberTextSecondary, fontSize = baseTextSize)
                                Text(
                                    text = "CODE: ${repo.code}",
                                    color = CyberYellow,
                                    fontSize = baseTextSize,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..3).forEach { starIndex ->
                        TvIconButton(
                            onClick = { 
                                val newStars = if (repo.stars == starIndex) 0 else starIndex
                                onStarsChange(newStars) 
                            },
                            modifier = Modifier.size(starSize)
                        ) {
                            Text(if (repo.stars >= starIndex) "★" else "☆", color = CyberYellow, fontSize = if (isTvOrTablet) 24.sp else 20.sp)
                        }
                    }
                }
            }

            // URL (ADMIN ONLY - Hide completely in User APK)
            if (ENABLE_ADMIN_PANEL_FEATURE) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = repo.url,
                    color = CyberTextSecondary,
                    fontSize = if (isTvOrTablet) 14.sp else 12.sp,
                    maxLines = 1
                )
            }

            if (checkResult != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = checkResult,
                    fontSize = buttonTextSize,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        checkResult.startsWith("🟢") -> CyberGreen
                        checkResult.startsWith("🟡") -> CyberYellow
                        else -> CyberPink
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TvFilledTonalButton(onClick = onAddToCloudStream, modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = if (isNuvio) "Nuvio'ya Aktar" else "CS'ye Aktar",
                        fontSize = buttonTextSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                TvOutlinedButton(onClick = onCheck, modifier = Modifier.weight(1f)) {
                    Text("Kontrol Et", fontSize = buttonTextSize, maxLines = 1)
                }

                // ADMIN ONLY Actions
                if (ENABLE_ADMIN_PANEL_FEATURE) {
                    TvOutlinedButton(
                        onClick = {
                            if (repo.code.isNotBlank()) onCopyCode() else onCopyLink()
                        },
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text(if (repo.code.isNotBlank()) "Kod" else "Link", fontSize = buttonTextSize, maxLines = 1)
                    }
                    TvOutlinedButton(onClick = onEdit, modifier = Modifier.weight(0.5f)) {
                        Text("✏️", fontSize = buttonTextSize, maxLines = 1)
                    }
                    TvOutlinedButton(onClick = onDelete, modifier = Modifier.weight(0.5f)) {
                        Text("🗑️", fontSize = buttonTextSize, maxLines = 1)
                    }
                }
            }
        }
    }
}

/* =========================================================
   AYARLAR
   ========================================================= */

@Composable
fun SettingsDialog(

    onDismiss: () -> Unit,

    onAnnouncements: () -> Unit,

    onCheckAll: () -> Unit,

    onBackup: () -> Unit,

    onRestore: () -> Unit,

    onRestoreDefaults: () -> Unit,

    onCheckAppUpdate: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(
                "⚙️ Ayarlar",
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text("Repo yönetimi ve uygulama işlemleri")

                TvOutlinedButton(
                    onClick = onAnnouncements,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📢 Duyurular")
                }

                TvOutlinedButton(
                    onClick = onCheckAppUpdate,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔄 Güncellemeleri Kontrol Et")
                }

                TvOutlinedButton(
                    onClick = onRestoreDefaults,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✨ Hazır Repoları Yükle / Sıfırla")
                }

                TvOutlinedButton(
                    onClick = onCheckAll,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔎 Tüm Linkleri Kontrol Et")
                }

                TvOutlinedButton(
                    onClick = onBackup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💾 Yedekle")
                }

                TvOutlinedButton(
                    onClick = onRestore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📥 Yedeği Geri Yükle")
                }

                val telegramContext = LocalContext.current
                TvOutlinedButton(
                    onClick = {
                        openTelegramChannel(telegramContext)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✈️ Telegram Kanalımız")
                }

                HorizontalDivider()

                Text("Panel Teması", fontWeight = FontWeight.Bold)

                val themes = listOf(
                    "Cyber" to "Cyber Neon (Varsayılan)",
                    "DeepOcean" to "Okyanus Mavisi",
                    "Crimson" to "Kızıl Gece",
                    "Emerald" to "Zümrüt Yeşili"
                )
                
                val currentSettingsTheme = LocalContext.current.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString("theme_mode", "Cyber") ?: "Cyber"
                
                val context = LocalContext.current
                
                themes.forEach { (themeKey, themeName) ->
                    val isSelected = currentSettingsTheme == themeKey
                    TvButton(
                        onClick = {
                            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                .edit().putString("theme_mode", themeKey).apply()
                            applyThemeColors(themeKey)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isSelected) "✅ " else "⬜ ", fontSize = 16.sp)
                            Text(themeName, color = if (isSelected) CyberYellow else CyberTextPrimary)
                        }
                    }
                }
                
                HorizontalDivider()
                
                Text(
                    "Hakkında",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Repo ekleme, düzenleme, favoriler, link kontrolü ve yedekleme araçları."
                )
            }
        },

        confirmButton = {
            TvButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

/* =========================================================
   DUYURU DİYALOGLARI
   ========================================================= */

@Composable
fun AnnouncementPopupDialog(
    announcement: Announcement,
    onOpenLink: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "📢 " + announcement.title.ifBlank { "Duyuru" },
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(announcement.message)
                if (announcement.date.isNotBlank()) {
                    Text(announcement.date, fontSize = 12.sp, color = CyberTextSecondary)
                }
                if (announcement.link.isNotBlank()) {
                    Button(onClick = onOpenLink, modifier = Modifier.fillMaxWidth()) {
                        Text("🔗 Bağlantıyı Aç")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam", color = CyberYellow, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun AnnouncementsListDialog(
    announcements: List<Announcement>,
    onOpenLink: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📢 Duyurular", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (announcements.isEmpty()) {
                    Text("Henüz duyuru yok.", color = CyberTextSecondary)
                }
                announcements.forEach { a ->
                    Surface(
                        color = CyberCardDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (a.title.isNotBlank()) {
                                Text(a.title, fontWeight = FontWeight.Bold)
                            }
                            Text(a.message)
                            if (a.date.isNotBlank()) {
                                Text(a.date, fontSize = 12.sp, color = CyberTextSecondary)
                            }
                            if (a.link.isNotBlank()) {
                                TvOutlinedButton(
                                    onClick = { onOpenLink(a.link) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("🔗 Bağlantıyı Aç")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TvButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun AdminAnnouncementsDialog(
    announcements: List<Announcement>,
    publishing: Boolean,
    onPublish: (List<Announcement>) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📢 Duyuru Yönetimi", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Yeni duyuru", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık (isteğe bağlı)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mesaj") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Bağlantı (isteğe bağlı)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                TvButton(
                    onClick = {
                        val newItem = AnnouncementManager.create(title, message, link)
                        onPublish(listOf(newItem) + announcements)
                        title = ""
                        message = ""
                        link = ""
                    },
                    enabled = message.isNotBlank() && !publishing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (publishing) "⏳ Yayınlanıyor..." else "📤 Herkese Yayınla")
                }

                HorizontalDivider()

                Text("Yayındaki duyurular (${announcements.size})", fontWeight = FontWeight.Bold)

                if (announcements.isEmpty()) {
                    Text("Henüz duyuru yok.", color = CyberTextSecondary)
                }

                announcements.forEach { a ->
                    Surface(
                        color = CyberCardDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (a.title.isNotBlank()) {
                                    Text(a.title, fontWeight = FontWeight.Bold)
                                }
                                Text(a.message, maxLines = 3)
                                if (a.date.isNotBlank()) {
                                    Text(a.date, fontSize = 12.sp, color = CyberTextSecondary)
                                }
                            }
                            TextButton(
                                onClick = { onPublish(announcements.filter { it.id != a.id }) },
                                enabled = !publishing
                            ) {
                                Text("🗑️ Sil")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TvButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

/* =========================================================
   TELEGRAM DAVET DİYALOĞU (açılışta)
   ========================================================= */

@Composable
fun TelegramInviteDialog(
    onJoin: () -> Unit,
    onLater: () -> Unit,
    onNeverShow: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onLater,
        title = {
            Text("✈️ Telegram Kanalımıza Katılın", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yeni repolar, güncellemeler ve duyurular için Telegram kanalımıza katılın!")
                TvButton(onClick = onJoin, modifier = Modifier.fillMaxWidth()) {
                    Text("✈️ Kanala Katıl")
                }
                TvOutlinedButton(onClick = onLater, modifier = Modifier.fillMaxWidth()) {
                    Text("Daha Sonra")
                }
                TvOutlinedButton(onClick = onNeverShow, modifier = Modifier.fillMaxWidth()) {
                    Text("Bir Daha Gösterme")
                }
            }
        },
        confirmButton = {}
    )
}

/* =========================================================
   EKLEME DİYALOĞU
   ========================================================= */

@Composable
fun AddRepoDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Genel") }
    var type by remember { mutableStateOf("cloudstream") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Yeni Repo Ekle", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo adı") },
                    placeholder = { Text("Örn. Kraptor Repo") },
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo linki (Uzun URL)") },
                    placeholder = { Text("https://...") },
                    singleLine = true
                )

                if (type != "nuvio") {
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kısa kod (Opsiyonel)") },
                        placeholder = { Text("Örn. kraptorcs") },
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kategori") },
                    placeholder = { Text("Örn. Türkçe") },
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                Text("Platform / Tür Seçimi:", fontSize = 12.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (type == "cloudstream") {
                        TvButton(
                            onClick = { type = "cloudstream" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("☁️ CloudStream", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TvOutlinedButton(
                            onClick = { type = "cloudstream" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("☁️ CloudStream", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (type == "nuvio") {
                        TvButton(
                            onClick = { type = "nuvio" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🟣 Nuvio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TvOutlinedButton(
                            onClick = { type = "nuvio" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🟣 Nuvio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TvButton(
                onClick = {
                    onAdd(name, url, if (type == "nuvio") "" else code, category, type)
                }
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TvOutlinedButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

/* =========================================================
   DÜZENLEME DİYALOĞU
   ========================================================= */

@Composable
fun EditRepoDialog(
    repo: Repo,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var name by remember(repo) { mutableStateOf(repo.name) }
    var url by remember(repo) { mutableStateOf(repo.url) }
    var code by remember(repo) { mutableStateOf(repo.code) }
    var category by remember(repo) { mutableStateOf(repo.category) }
    var type by remember(repo) { mutableStateOf(repo.type.ifBlank { "cloudstream" }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Repo Düzenle", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo adı") },
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo linki (Uzun URL)") },
                    singleLine = true
                )

                if (type != "nuvio") {
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kısa kod (Opsiyonel)") },
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kategori") },
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                Text("Platform / Tür Seçimi:", fontSize = 12.sp, color = CyberTextSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (type == "cloudstream") {
                        TvButton(
                            onClick = { type = "cloudstream" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("☁️ CloudStream", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TvOutlinedButton(
                            onClick = { type = "cloudstream" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("☁️ CloudStream", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (type == "nuvio") {
                        TvButton(
                            onClick = { type = "nuvio" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🟣 Nuvio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TvOutlinedButton(
                            onClick = { type = "nuvio" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🟣 Nuvio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TvButton(
                onClick = {
                    onSave(name, url, if (type == "nuvio") "" else code, category, type)
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TvOutlinedButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

/* =========================================================
   SİLME ONAYI
   ========================================================= */

@Composable
fun DeleteRepoDialog(

    repo: Repo,

    onDismiss: () -> Unit,

    onConfirm: () -> Unit
) {

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                "Repo silinsin mi?",
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Text(
                "“${repo.name}” reposu kalıcı olarak silinecek."
            )
        },

        confirmButton = {

            TvButton(
                onClick =
                    onConfirm
            ) {

                Text(
                    "Sil"
                )
            }
        },

        dismissButton = {

            TvOutlinedButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    "İptal"
                )
            }
        }
    )
}
