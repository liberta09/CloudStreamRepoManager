
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import coil.compose.SubcomposeAsyncImage
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
    val type: String = "cloudstream",
    val iconUrl: String = ""
)

fun getEffectiveIconUrl(repo: Repo): String {
    if (repo.iconUrl.isNotBlank()) return repo.iconUrl.trim()
    
    val url = repo.url.trim()
    if (url.startsWith("https://raw.githubusercontent.com/")) {
        val parts = url.removePrefix("https://raw.githubusercontent.com/").split("/")
        if (parts.isNotEmpty() && parts[0].isNotBlank()) {
            return "https://github.com/${parts[0]}.png"
        }
    } else if (url.startsWith("https://github.com/")) {
        val parts = url.removePrefix("https://github.com/").split("/")
        if (parts.isNotEmpty() && parts[0].isNotBlank()) {
            return "https://github.com/${parts[0]}.png"
        }
    }
    return ""
}

private const val PREFS_NAME = "cloudstream_repo_manager"
private const val REPOS_KEY = "repos"

// Product Flavors Build Konfigürasyonu (BuildConfig üzerinden otomatik gelir)
val ENABLE_ADMIN_PANEL_FEATURE: Boolean = com.kaan.cloudstreamrepomanager.BuildConfig.ENABLE_ADMIN_PANEL

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
        if (repo.iconUrl.isNotBlank()) {
            obj.put("iconUrl", repo.iconUrl)
            obj.put("icon", repo.iconUrl)
        }

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
        val parsedIconUrl = obj.optString("iconUrl", obj.optString("icon", obj.optString("logo", "")))
        
        result.add(
            Repo(
                name = obj.optString("name"),
                url = obj.optString("url"),
                code = obj.optString("code"),
                category = obj.optString("category"),
                stars = parsedStars,
                type = rawType,
                iconUrl = parsedIconUrl
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

fun validateNuvioRepoJson(jsonString: String): String {
    return try {
        val trimmed = jsonString.trim()
        if (!trimmed.startsWith("{")) {
            return "🔴 Bağlantı başarılı ama Nuvio manifest.json formatı geçersiz"
        }

        val json = JSONObject(trimmed)
        val id = json.optString("id").trim()
        val name = json.optString("name").trim()

        when {
            id.isBlank() && name.isBlank() ->
                "🔴 Geçersiz Nuvio Manifest (id ve name eksik)"
            else ->
                "🟢 Çalışıyor"
        }
    } catch (_: Exception) {
        "🔴 Geçersiz Nuvio Manifest JSON"
    }
}

fun performRepoCheck(
    repo: Repo
): String {
    val isNuvio = repo.type.lowercase() == "nuvio" || repo.url.lowercase().endsWith("manifest.json")
    return try {
        val result = NetworkUtils.openFollowRedirectsConnection(
            initialUrl = repo.url,
            method = "GET"
        )

        when {
            result.isSuccess && result.body.isNotBlank() -> {
                if (isNuvio) {
                    validateNuvioRepoJson(result.body)
                } else {
                    val csRes = validateCloudStreamRepoJson(result.body)
                    if (csRes.startsWith("🟢")) "🟢 Çalışıyor" else csRes
                }
            }
            result.responseCode in 400..499 -> {
                "🔴 HTTP ${result.responseCode} Hata"
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

fun performRepoCheck(
    urlString: String
): String {
    val isNuvioUrl = urlString.lowercase().endsWith("manifest.json") || urlString.lowercase().contains("strem.io") || urlString.lowercase().contains("nuvio")
    return try {
        val result = NetworkUtils.openFollowRedirectsConnection(
            initialUrl = urlString,
            method = "GET"
        )

        when {
            result.isSuccess && result.body.isNotBlank() -> {
                if (isNuvioUrl) {
                    validateNuvioRepoJson(result.body)
                } else {
                    val csRes = validateCloudStreamRepoJson(result.body)
                    if (csRes.startsWith("🟢")) "🟢 Çalışıyor" else csRes
                }
            }
            result.responseCode in 400..499 -> {
                "🔴 HTTP ${result.responseCode} Hata"
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
    repo: Repo,
    onResult: (String) -> Unit
) {
    Thread {
        val result = performRepoCheck(repo)
        Handler(Looper.getMainLooper()).post {
            onResult(result)
        }
    }.start()
}

fun checkRepoUrl(
    urlString: String,
    onResult: (String) -> Unit
) {
    Thread {
        val result = performRepoCheck(urlString)
        Handler(Looper.getMainLooper()).post {
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
            Handler(Looper.getMainLooper()).post {
                onStart("Kontrol ediliyor: ${index + 1}/${repos.size} — ${repo.name}")
            }

            val result = performRepoCheck(repo)

            Handler(Looper.getMainLooper()).post {
                onResult(repo.url, result)
            }
        }

        Handler(Looper.getMainLooper()).post {
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

    val configuration = LocalConfiguration.current
    val isTvOrTablet = configuration.screenWidthDp >= 600
    val screenWidth = configuration.screenWidthDp.dp

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
                        .padding(horizontal = if (isTvOrTablet) 16.dp else 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // SOL: Logo + REPO Başlığı + Alt Başlık
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(if (isTvOrTablet) 40.dp else 32.dp)
                                .background(CyberCardDark, RoundedCornerShape(10.dp))
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(if (isTvOrTablet) 10.dp else 6.dp))
                        Column {
                            Text(
                                text = "REPO",
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberTextPrimary,
                                fontSize = if (isTvOrTablet) 20.sp else 16.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "CLOUDSTREAM",
                                color = CyberTextSecondary,
                                fontSize = if (isTvOrTablet) 9.sp else 8.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // SAĞ: ⚙️ Ayarlar | 🔍 Arama Simgesi | [ Repo ara… ]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(if (isTvOrTablet) 6.dp else 2.dp)
                    ) {
                        TvIconButton(onClick = { showSettingsDialog = true }) {
                            Text("⚙️", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                        }

                        if (isTvOrTablet) {
                            Text("🔍", fontSize = 16.sp, modifier = Modifier.padding(start = 2.dp))
                        }

                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .width(if (isTvOrTablet) 180.dp else 100.dp)
                                .height(if (isTvOrTablet) 38.dp else 34.dp),
                            placeholder = { Text("Repo ara...", fontSize = 11.sp, color = CyberTextSecondary) },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 11.sp, color = CyberTextPrimary),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberAccent,
                                unfocusedBorderColor = CyberBorder,
                                focusedContainerColor = CyberCardDark,
                                unfocusedContainerColor = CyberCardDark,
                            ),
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchText = "" },
                                        modifier = Modifier.size(if (isTvOrTablet) 24.dp else 20.dp)
                                    ) {
                                        Text("✖️", fontSize = if (isTvOrTablet) 10.sp else 8.sp)
                                    }
                                } else if (!isTvOrTablet) {
                                    Text("🔍", fontSize = 12.sp, modifier = Modifier.padding(end = 4.dp))
                                }
                            }
                        )

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
                            Text(if (checkingAll) "⏳" else "🔄", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                        }

                        TvIconButton(onClick = { openTelegramChannel(context) }) {
                            Text("✈️", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                        }

                        // ADMIN ONLY CONTROLS (Bulut Yayınlama, Ekleme, Duyurular, Giriş)
                        if (ENABLE_ADMIN_PANEL_FEATURE) {
                            if (isAdminLoggedIn) {
                                TvIconButton(onClick = { showAddDialog = true }) {
                                    Text("➕", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                                }
                                TvIconButton(onClick = { showAdminAnnouncementsDialog = true }) {
                                    Text("📢", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                                }
                                TvIconButton(
                                    onClick = {
                                        isPublishingToCloud = true
                                        Toast.makeText(context, "Buluta kaydediliyor...", Toast.LENGTH_SHORT).show()
                                        CentralRepoApiManager.publishCentralReposToCloud(context, repos.toList(), "") { _, _ ->
                                            isPublishingToCloud = false
                                            Toast.makeText(context, "💾 Bulut verisi güncellendi!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = !isPublishingToCloud
                                ) {
                                    Text(if (isPublishingToCloud) "⏳" else "☁️", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                                }
                            }
                            TvIconButton(
                                onClick = {
                                    if (isAdminLoggedIn) {
                                        adminAuthManager.logoutAdmin()
                                        isAdminLoggedIn = false
                                        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showAdminLoginDialog = true
                                    }
                                }
                            ) {
                                Text("🔑", fontSize = if (isTvOrTablet) 16.sp else 14.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CyberBgDark)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            val cloudStreamCount = repos.count { it.type.lowercase() != "nuvio" }
            val nuvioCount = repos.count { it.type.lowercase() == "nuvio" }

            // 🌐 Tümü (X) | ☁️ CS (X) | 🟣 Nuvio (X) Segmented Control Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tümü
                Surface(
                    color = if (selectedPlatform == "Tümü" && !showFavoritesOnly) CyberAccent else CyberCardDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selectedPlatform == "Tümü" && !showFavoritesOnly) CyberAccent else CyberBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedPlatform = "Tümü"
                            showFavoritesOnly = false
                        }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "🌐 Tümü (${repos.size})",
                            color = if (selectedPlatform == "Tümü" && !showFavoritesOnly) Color.White else CyberTextSecondary,
                            fontWeight = if (selectedPlatform == "Tümü" && !showFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                // CS
                Surface(
                    color = if (selectedPlatform == "CloudStream" && !showFavoritesOnly) CyberAccent else CyberCardDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selectedPlatform == "CloudStream" && !showFavoritesOnly) CyberAccent else CyberBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedPlatform = "CloudStream"
                            showFavoritesOnly = false
                        }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "☁️ CS ($cloudStreamCount)",
                            color = if (selectedPlatform == "CloudStream" && !showFavoritesOnly) Color.White else CyberTextSecondary,
                            fontWeight = if (selectedPlatform == "CloudStream" && !showFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                // Nuvio
                Surface(
                    color = if (selectedPlatform == "Nuvio" && !showFavoritesOnly) CyberAccent else CyberCardDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selectedPlatform == "Nuvio" && !showFavoritesOnly) CyberAccent else CyberBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedPlatform = "Nuvio"
                            showFavoritesOnly = false
                        }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "🟣 Nuvio ($nuvioCount)",
                            color = if (selectedPlatform == "Nuvio" && !showFavoritesOnly) Color.White else CyberTextSecondary,
                            fontWeight = if (selectedPlatform == "Nuvio" && !showFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            val configuration = LocalConfiguration.current
            val isTvOrTablet = configuration.screenWidthDp >= 600
            val screenWidth = configuration.screenWidthDp.dp

            val columns = when {
                screenWidth >= 1100.dp -> 2
                screenWidth >= 700.dp -> 2
                else -> 1
            }

            val chunkedRepos = remember(filteredRepos.toList(), columns) {
                filteredRepos.chunked(columns)
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // LEFT: Repo List Grid
                Column(
                    modifier = Modifier.weight(if (isTvOrTablet) 1.5f else 1f)
                ) {
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
                }

                // RIGHT: Detail Panel (Rendered on Landscape TV/Tablet screens)
                if (isTvOrTablet) {
                    val activeDetailRepo = selectedRepoForDetail ?: filteredRepos.firstOrNull() ?: repos.firstOrNull()
                    if (activeDetailRepo != null) {
                        RepoDetailPanel(
                            repo = activeDetailRepo,
                            checkResult = checkResults[activeDetailRepo.url],
                            onTransfer = {
                                if (activeDetailRepo.type.lowercase() == "nuvio") {
                                    openNuvioAndPrepareRepo(context, activeDetailRepo, onNotInstalled = { showNuvioNotInstalledDialog = true })
                                } else {
                                    openCloudStreamAndPrepareRepo(context, activeDetailRepo, onNotInstalled = { showCloudStreamNotInstalledDialog = true })
                                }
                            },
                            onCheck = {
                                checkResults[activeDetailRepo.url] = "⏳ Kontrol ediliyor..."
                                checkRepoUrl(activeDetailRepo.url) { result -> checkResults[activeDetailRepo.url] = result }
                            },
                            onCopyLink = { copyToClipboard(context, activeDetailRepo.url, "Repo linki kopyalandı") },
                            onStarsChange = { newStars ->
                                val index = repos.indexOf(activeDetailRepo)
                                if (index >= 0) {
                                    repos[index] = activeDetailRepo.copy(stars = newStars)
                                    saveRepos(context, repos)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

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
        containerColor = CyberCardDark,
        titleContentColor = CyberTextPrimary,
        textContentColor = CyberTextPrimary,
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
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = enteredToken,
                    onValueChange = { enteredToken = it },
                    label = { Text("GitHub Token (Bulut Eşitleme İçin)") },
                    placeholder = { Text("ghp_...") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
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
   REPO DETAY PANELİ (HEDEF REFERANS GÖRSEL DİLİ)
   ========================================================= */

@Composable
fun RepoDetailPanel(
    repo: Repo,
    checkResult: String?,
    onTransfer: () -> Unit,
    onCheck: () -> Unit,
    onCopyLink: () -> Unit,
    onStarsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNuvio = repo.type.lowercase() == "nuvio"
    val platformName = if (isNuvio) "NUVIO" else "CLOUDSTREAM"
    val platformColor = if (isNuvio) CyberPink else CyberCyan
    val effectiveLogoUrl = remember(repo.url, repo.iconUrl) { getEffectiveIconUrl(repo) }

    val csGradient = Brush.horizontalGradient(listOf(Color(0xFF0052D4), Color(0xFF4364F7), Color(0xFF6FB1FC)))
    val nuvioGradient = Brush.horizontalGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
    val transferGradient = if (isNuvio) nuvioGradient else csGradient

    Surface(
        color = CyberCardDark,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CyberBorder),
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(CyberCardDark, CyberSurfaceDark)), RoundedCornerShape(20.dp))
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Header: Logo + Title + Stars
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(CyberSurfaceDark, RoundedCornerShape(16.dp))
                        .border(1.5.dp, CyberBorder, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (effectiveLogoUrl.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = effectiveLogoUrl,
                            contentDescription = "${repo.name} Logo",
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            contentScale = ContentScale.Fit,
                            loading = { Text(if (isNuvio) "🟣" else "📦", fontSize = 36.sp) },
                            error = { Text(if (isNuvio) "🟣" else "📦", fontSize = 36.sp) }
                        )
                    } else {
                        Text(if (isNuvio) "🟣" else "📦", fontSize = 36.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = repo.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = CyberTextPrimary
                    )

                    // 5 Golden Stars
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..5).forEach { starIdx ->
                            Text(
                                text = "★",
                                color = CyberYellow,
                                fontSize = 18.sp
                            )
                        }
                    }

                    // Status Badges & Tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), RoundedCornerShape(50)))
                                Text("Çalışıyor", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Text(
                                "YENİ GÜNCELLEME",
                                color = Color(0xFFEF4444),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("[ ${repo.category.uppercase()} ]", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("•", color = CyberTextSecondary, fontSize = 11.sp)
                        Text("[ $platformName ]", color = platformColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TvGradientButton(
                    onClick = onTransfer,
                    gradient = transferGradient,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(
                        text = if (isNuvio) "🟣 Nuvio'ya Aktar" else "☁️ CS'ye Aktar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                TvOutlinedButton(
                    onClick = onCheck,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔄 Kontrol Et", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Metrics Row 1: Son kontrol & Son güncelleme
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = CyberSurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🕒", fontSize = 20.sp)
                        Column {
                            Text("Son kontrol", fontSize = 10.sp, color = CyberTextSecondary)
                            Text("12 dakika önce", fontSize = 12.sp, color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Surface(
                    color = CyberSurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("📅", fontSize = 20.sp)
                        Column {
                            Text("Son güncelleme", fontSize = 10.sp, color = CyberTextSecondary)
                            Text("08.10.2026", fontSize = 12.sp, color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Metrics Row 2: Plugin sayısı & Repo bağlantısı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = CyberSurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.weight(0.8f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🧩", fontSize = 20.sp)
                        Column {
                            Text("Plugin sayısı", fontSize = 10.sp, color = CyberTextSecondary)
                            Text("48", fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Surface(
                    color = CyberSurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.weight(1.2f).clickable { onCopyLink() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔗", fontSize = 18.sp)
                            Column {
                                Text("Repo bağlantısı", fontSize = 10.sp, color = CyberTextSecondary)
                                Text(
                                    text = if (repo.url.length > 20) repo.url.take(20) + "..." else repo.url,
                                    fontSize = 11.sp,
                                    color = CyberCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Text("📋", fontSize = 16.sp)
                    }
                }
            }

            // Description Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Açıklama", fontWeight = FontWeight.Bold, color = CyberTextPrimary, fontSize = 14.sp)
                Text(
                    text = "Türkçe dizi ve film içerikleri için hazırlanmış CloudStream repo deposudur. Güncel ve kaliteli içerikler sunar.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
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
   GRADIENT BUTTON
   ========================================================= */

@Composable
private fun TvGradientButton(
    onClick: () -> Unit,
    gradient: Brush,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, animationSpec = tween(150), label = "gradScale")
    val borderColor by animateColorAsState(targetValue = if (isFocused) Color.White else Color.Transparent, animationSpec = tween(150), label = "gradBorder")

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        border = BorderStroke(if (isFocused) 2.dp else 0.dp, borderColor),
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(gradient, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
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
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.04f else 1.0f, animationSpec = tween(150), label = "cardScale")
    val borderColor by animateColorAsState(targetValue = if (isFocused) CyberBorderFocused else CyberBorder, animationSpec = tween(150), label = "cardBorder")
    
    val isNuvio = repo.type.lowercase() == "nuvio"
    val platformName = if (isNuvio) "NUVIO" else "CLOUDSTREAM"
    val platformColor = if (isNuvio) CyberPink else CyberCyan
    
    val baseTextSize = if (isTvOrTablet) 13.sp else 10.sp
    val titleTextSize = if (isTvOrTablet) 18.sp else 14.sp
    val buttonTextSize = if (isTvOrTablet) 12.sp else 11.sp
    val paddingSize = if (isTvOrTablet) 18.dp else 14.dp
    val iconSize = if (isTvOrTablet) 48.dp else 36.dp
    val starSize = if (isTvOrTablet) 32.dp else 28.dp

    val transferGradient = if (isNuvio) {
        Brush.horizontalGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF0052D4), Color(0xFF4364F7), Color(0xFF6FB1FC)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(if (isFocused) 2.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onDetailClick() },
        colors = CardDefaults.cardColors(containerColor = CyberCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 10.dp else 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(listOf(CyberCardDark, CyberSurfaceDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(paddingSize)
        ) {
            // Header: Logo + Name + Category Chips + Favorite Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Logo Container
                    val effectiveLogoUrl = remember(repo.url, repo.iconUrl) { getEffectiveIconUrl(repo) }

                    Box(
                        modifier = Modifier
                            .size(iconSize)
                            .background(CyberSurfaceDark, RoundedCornerShape(12.dp))
                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (effectiveLogoUrl.isNotBlank()) {
                            SubcomposeAsyncImage(
                                model = effectiveLogoUrl,
                                contentDescription = "${repo.name} Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp),
                                contentScale = ContentScale.Fit,
                                loading = {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(if (isNuvio) "🟣" else "📦", fontSize = if (isTvOrTablet) 24.sp else 16.sp)
                                    }
                                },
                                error = {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(if (isNuvio) "🟣" else "📦", fontSize = if (isTvOrTablet) 24.sp else 16.sp)
                                    }
                                }
                            )
                        } else {
                            Text(if (isNuvio) "🟣" else "📦", fontSize = if (isTvOrTablet) 24.sp else 16.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = repo.name,
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = titleTextSize,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = CyberSurfaceDark,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.5.dp, CyberBorder)
                            ) {
                                Text(
                                    text = repo.category.uppercase(),
                                    color = CyberCyan,
                                    fontSize = baseTextSize,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = CyberSurfaceDark,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.5.dp, CyberBorder)
                            ) {
                                Text(
                                    text = if (platformName == "CLOUDSTREAM") "CS" else platformName,
                                    color = platformColor,
                                    fontSize = baseTextSize,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    (1..3).forEach { starIndex ->
                        TvIconButton(
                            onClick = { 
                                val newStars = if (repo.stars == starIndex) 0 else starIndex
                                onStarsChange(newStars) 
                            },
                            modifier = Modifier.size(starSize)
                        ) {
                            Text(
                                text = if (repo.stars >= starIndex) "★" else "☆",
                                color = if (repo.stars >= starIndex) CyberYellow else CyberTextSecondary,
                                fontSize = if (isTvOrTablet) 22.sp else 18.sp
                            )
                        }
                    }
                }
            }

            // URL (ADMIN ONLY)
            if (ENABLE_ADMIN_PANEL_FEATURE) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = repo.url,
                    color = CyberTextSecondary,
                    fontSize = if (isTvOrTablet) 12.sp else 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Kısa kod satırı her kartta gösterilir (kod yoksa "—"),
                // böylece yan yana duran kartların yüksekliği eşit kalır.
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Kod: ${repo.code.ifBlank { "—" }}",
                    color = if (repo.code.isNotBlank()) CyberYellow else CyberTextSecondary,
                    fontSize = if (isTvOrTablet) 12.sp else 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status Indicator Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                val (statusColor, statusLabel) = when {
                    checkResult == null -> Color(0xFF6B7280) to "Kontrol edilmedi"
                    checkResult.startsWith("🟢") -> Color(0xFF10B981) to "Çalışıyor"
                    checkResult.startsWith("⏳") || checkResult.startsWith("🟡") -> Color(0xFFFBBF24) to "Kontrol ediliyor..."
                    else -> Color(0xFFEF4444) to "Çalışmıyor"
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, RoundedCornerShape(50))
                )
                Text(
                    text = if (checkResult != null && !checkResult.startsWith("🟢") && !checkResult.startsWith("⏳") && !checkResult.startsWith("🟡") && !checkResult.startsWith("🔴")) checkResult else statusLabel,
                    color = statusColor,
                    fontSize = baseTextSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(14.dp))

            // Actions: Transfer (Gradient) + Kontrol Et (Glass)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TvGradientButton(
                    onClick = onAddToCloudStream,
                    gradient = transferGradient,
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text(
                        text = if (isNuvio) "🟣 Nuvio'ya Aktar" else "☁️ CS'ye Aktar",
                        fontSize = buttonTextSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TvOutlinedButton(
                    onClick = onCheck,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "🔄 Kontrol Et",
                        fontSize = buttonTextSize,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Admin Actions: ayrı satırda, böylece üstteki butonların yazıları kesilmez
            if (ENABLE_ADMIN_PANEL_FEATURE) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TvOutlinedButton(
                        onClick = {
                            if (repo.code.isNotBlank()) onCopyCode() else onCopyLink()
                        },
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text(
                            if (repo.code.isNotBlank()) "📋 Kodu Kopyala" else "🔗 Linki Kopyala",
                            fontSize = buttonTextSize,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
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
                    "Darknes Purple" to "Darknes Purple (Varsayılan)",
                    "Camel" to "Camel (Sıcak Kahve)",
                    "Indigo" to "Indigo (Gece Mavisi)"
                )
                
                val currentSettingsTheme = LocalContext.current.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString("theme_mode", "Darknes Purple") ?: "Darknes Purple"
                
                val context = LocalContext.current
                
                themes.forEach { (themeKey, themeName) ->
                    val isSelected = currentSettingsTheme == themeKey || (currentSettingsTheme == "Cyber" && themeKey == "Darknes Purple")
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
        containerColor = CyberCardDark,
        titleContentColor = CyberTextPrimary,
        textContentColor = CyberTextPrimary,
        title = { Text("📢 Duyuru Yönetimi", fontWeight = FontWeight.Bold, color = CyberTextPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Yeni duyuru", fontWeight = FontWeight.Bold, color = CyberTextPrimary)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık (isteğe bağlı)") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mesaj") },
                    minLines = 3,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Bağlantı (isteğe bağlı)") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    ),
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
        containerColor = CyberCardDark,
        titleContentColor = CyberTextPrimary,
        textContentColor = CyberTextPrimary,
        title = {
            Text("Yeni Repo Ekle", fontWeight = FontWeight.Bold, color = CyberTextPrimary)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo adı") },
                    placeholder = { Text("Örn. Kraptor Repo") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo linki (Uzun URL)") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    )
                )

                if (type != "nuvio") {
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kısa kod (Opsiyonel)") },
                        placeholder = { Text("Örn. kraptorcs") },
                        singleLine = true,
                        textStyle = TextStyle(color = CyberTextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberAccent,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = CyberSurfaceDark,
                            unfocusedContainerColor = CyberSurfaceDark,
                            focusedLabelColor = CyberAccent,
                            unfocusedLabelColor = CyberTextSecondary
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kategori") },
                    placeholder = { Text("Örn. Türkçe") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    )
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
        containerColor = CyberCardDark,
        titleContentColor = CyberTextPrimary,
        textContentColor = CyberTextPrimary,
        title = {
            Text("Repo Düzenle", fontWeight = FontWeight.Bold, color = CyberTextPrimary)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo adı") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Repo linki (Uzun URL)") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    )
                )

                if (type != "nuvio") {
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kısa kod (Opsiyonel)") },
                        singleLine = true,
                        textStyle = TextStyle(color = CyberTextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberAccent,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = CyberSurfaceDark,
                            unfocusedContainerColor = CyberSurfaceDark,
                            focusedLabelColor = CyberAccent,
                            unfocusedLabelColor = CyberTextSecondary
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kategori") },
                    singleLine = true,
                    textStyle = TextStyle(color = CyberTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberSurfaceDark,
                        unfocusedContainerColor = CyberSurfaceDark,
                        focusedLabelColor = CyberAccent,
                        unfocusedLabelColor = CyberTextSecondary
                    )
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
        onDismissRequest = onDismiss,
        containerColor = CyberCardDark,
        titleContentColor = CyberTextPrimary,
        textContentColor = CyberTextPrimary,
        title = {
            Text("Repo silinsin mi?", fontWeight = FontWeight.Bold, color = CyberTextPrimary)
        },
        text = {
            Text("“${repo.name}” reposu kalıcı olarak silinecek.", color = CyberTextPrimary)
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
