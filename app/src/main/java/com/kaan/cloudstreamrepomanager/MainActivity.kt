
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.kaan.cloudstreamrepomanager.ui.theme.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun TvOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) CyberYellow else CyberBorder
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        enabled = enabled,
        border = BorderStroke(1.5.dp, borderColor),
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
    val borderColor = if (focused) CyberYellow else Color.Transparent

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        enabled = enabled,
        border = BorderStroke(1.5.dp, borderColor),
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
    val borderColor = if (focused) CyberYellow else Color.Transparent

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        enabled = enabled,
        border = BorderStroke(1.5.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        content = content
    )
}


data class Repo(
    val name: String,
    val url: String,
    val code: String,
    val category: String,
    val favorite: Boolean = false
)

private const val PREFS_NAME = "cloudstream_repo_manager"
private const val REPOS_KEY = "repos"

// Product Flavors Build Konfigürasyonu (BuildConfig üzerinden otomatik gelir)
val ENABLE_ADMIN_PANEL_FEATURE = BuildConfig.ENABLE_ADMIN_PANEL

/* =========================================================
   ACTIVITY
   ========================================================= */

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CloudStreamRepoManagerTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    CyberSplashScreen(onFinish = { showSplash = false })
                } else {
                    CloudStreamRepoManager()
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
        delay(250)
        progress = 0.35f
        statusText = "[ 35% ] LOADING REPOSITORY DATA..."
        delay(300)
        progress = 0.75f
        statusText = "[ 75% ] VERIFYING CLOUDSTREAM PROTOCOLS..."
        delay(300)
        progress = 1.0f
        statusText = "[ 100% ] SYSTEM ONLINE."
        delay(350)
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
        obj.put("id", (index + 1).toString())
        obj.put("name", repo.name)
        obj.put("url", repo.url)
        obj.put("code", repo.code)
        obj.put("category", repo.category)
        obj.put("favorite", repo.favorite)

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
        result.add(
            Repo(
                name = obj.optString("name"),
                url = obj.optString("url"),
                code = obj.optString("code"),
                category = obj.optString("category"),
                favorite = obj.optBoolean("favorite", false)
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
}

fun getDefaultRepos(): List<Repo> {
    return listOf(
        Repo(
            name = "cs-karma",
            url = "https://raw.githubusercontent.com/Kraptor123/cs-Karma/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = true
        ),
        Repo(
            name = "NeO Eklenti Deposu",
            url = "https://raw.githubusercontent.com/neoser1984/cloudstream-extensions/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = true
        ),
        Repo(
            name = "SafakStream Repository",
            url = "https://raw.githubusercontent.com/SafakStream/SafakStream/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Manitux Cloudstream Plugins",
            url = "https://raw.githubusercontent.com/manitux-app/cs-plugins/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "TurkSinema",
            url = "https://raw.githubusercontent.com/Wiojelt/TurkSinema/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "cstest",
            url = "https://raw.githubusercontent.com/ctnkyaumt/cstest/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "AllForU",
            url = "https://raw.githubusercontent.com/RVRBEAST76/allforu-repo/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "BronzeCloud",
            url = "https://raw.githubusercontent.com/Dr-Octagon/cloudstream-turkish/refs/heads/builds/repo_stable.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Turkish Providers Repository | @feroxxcs3",
            url = "https://raw.githubusercontent.com/liberta09/Kekik-cloudstream/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Phisher Repo",
            url = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/refs/heads/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Mega repository",
            url = "https://raw.githubusercontent.com/self-similarity/MegaRepo/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "mudti",
            url = "https://raw.githubusercontent.com/pltmustafa/plt-stream/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "megaturk",
            url = "https://raw.githubusercontent.com/Kraptor123/TurkMegaRepo/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Latte - Sinetech.TR",
            url = "https://raw.githubusercontent.com/GitLatte/Sinetech/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Makoto'nun Cloudstream Reposu",
            url = "https://raw.githubusercontent.com/Sertel392/Makotogecici/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
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
        val repos = jsonToRepos(json)
        filterDeletedRepos(context, repos)
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

    var connection: HttpURLConnection? = null

    return try {

        val url = URL(urlString)

        connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        connection.instanceFollowRedirects = true

        connection.setRequestProperty(
            "User-Agent",
            "CloudStream-Repo-Manager"
        )

        val responseCode =
            connection.responseCode

        when {

            responseCode in 200..299 -> {

                val body =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                validateCloudStreamRepoJson(body)
            }

            responseCode in 300..399 ->
                "🟡 Yönlendirme — HTTP $responseCode"

            responseCode in 400..499 ->
                "🟠 İstemci hatası — HTTP $responseCode"

            responseCode in 500..599 ->
                "🔴 Sunucu hatası — HTTP $responseCode"

            else ->
                "🟠 Sunucu yanıtı — HTTP $responseCode"
        }

    } catch (_: Exception) {

        "🔴 Bağlantı başarısız"

    } finally {

        connection?.disconnect()
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

    var showFavoritesOnly by remember {
        mutableStateOf(false)
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var checkingAll by remember {
        mutableStateOf(false)
    }

    var checkingMessage by remember {
        mutableStateOf("")
    }

    var showCloudStreamNotInstalledDialog by remember {
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
            if (liveRepos != null && liveRepos.isNotEmpty()) {
                val cleanRepos = filterDeletedRepos(context, liveRepos)
                repos.clear()
                repos.addAll(cleanRepos)
                saveRepos(context, cleanRepos)
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
                repo.name.contains(
                    searchText,
                    ignoreCase = true
                ) ||
                        repo.url.contains(
                            searchText,
                            ignoreCase = true
                        ) ||
                        repo.code.contains(
                            searchText,
                            ignoreCase = true
                        ) ||
                        repo.category.contains(
                            searchText,
                            ignoreCase = true
                        )

            val favoriteMatch =
                !showFavoritesOnly ||
                        repo.favorite

            val categoryMatch =
                selectedCategory == "Tümü" ||
                        repo.category ==
                        selectedCategory

            searchMatch &&
                    favoriteMatch &&
                    categoryMatch
        }

    val favoriteCount =
        repos.count {
            it.favorite
        }

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

    Scaffold(
        containerColor = CyberBgDark,
        topBar = {
            Surface(
                color = CyberSurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. LEFT: Title & Small Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (ENABLE_ADMIN_PANEL_FEATURE && isAdminLoggedIn) "👑 ADMIN" else "⚡ CS REPO",
                            fontWeight = FontWeight.Bold,
                            color = if (ENABLE_ADMIN_PANEL_FEATURE && isAdminLoggedIn) CyberYellow else CyberCyan,
                            fontSize = 12.sp
                        )

                        // Badges
                        Surface(
                            color = CyberCardDark,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${repos.size} Repo",
                                color = CyberTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        if (favoriteCount > 0) {
                            Surface(
                                color = CyberCardDark,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "⭐ $favoriteCount",
                                    color = CyberYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (checkResults.isNotEmpty()) {
                            Surface(
                                color = CyberCardDark,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "🟢 $validRepoCount",
                                    color = CyberGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    // 2. MIDDLE: Search Bar (~32dp)
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        placeholder = { Text("Repo ara...", fontSize = 11.sp, color = CyberTextSecondary) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 11.sp, color = CyberTextPrimary),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = CyberCardDark,
                            unfocusedContainerColor = CyberCardDark,
                        ),
                        leadingIcon = { Text("🔎", fontSize = 11.sp) }
                    )

                    Spacer(Modifier.width(6.dp))

                    // 3. RIGHT: Compact Icon Buttons (32x32dp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Sadece Favoriler Toggle
                        IconButton(
                            onClick = { showFavoritesOnly = !showFavoritesOnly },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text(if (showFavoritesOnly) "★" else "⭐", fontSize = 14.sp, color = CyberYellow)
                        }

                        // Kontrol Et
                        IconButton(
                            onClick = {
                                if (repos.isEmpty()) {
                                    Toast.makeText(context, "Kontrol edilecek repo yok", Toast.LENGTH_SHORT).show()
                                } else if (!checkingAll) {
                                    checkingAll = true
                                    checkResults.clear()
                                    checkAllRepoUrls(
                                        repos = repos.toList(),
                                        onStart = { checkingMessage = it },
                                        onResult = { url, res -> checkResults[url] = res },
                                        onFinished = {
                                            checkingAll = false
                                            Toast.makeText(context, "Tüm linkler kontrol edildi", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            },
                            enabled = !checkingAll,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text(if (checkingAll) "⏳" else "🔍", fontSize = 14.sp)
                        }

                        // Yedekle
                        IconButton(
                            onClick = { backupLauncher.launch("cloudstream_repos_backup.json") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("💾", fontSize = 14.sp)
                        }

                        // Geri Yükle
                        IconButton(
                            onClick = { restoreLauncher.launch(arrayOf("application/json", "text/json", "text/plain", "*/*")) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("📥", fontSize = 14.sp)
                        }

                        // Admin Yetkileri
                        if (ENABLE_ADMIN_PANEL_FEATURE) {
                            if (isAdminLoggedIn) {
                                IconButton(
                                    onClick = { showAddDialog = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("➕", fontSize = 14.sp)
                                }

                                IconButton(
                                    onClick = {
                                        isPublishingToCloud = true
                                        Toast.makeText(context, "Buluta kaydediliyor...", Toast.LENGTH_SHORT).show()
                                        CentralRepoApiManager.publishCentralReposToCloud(context, repos.toList(), "") { _, _ ->
                                            isPublishingToCloud = false
                                            Toast.makeText(context, "💾 Bulut verisi güncellendi!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = !isPublishingToCloud,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text(if (isPublishingToCloud) "⏳" else "☁️", fontSize = 14.sp)
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (isAdminLoggedIn) {
                                        adminAuthManager.logoutAdmin()
                                        isAdminLoggedIn = false
                                        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showAdminLoginDialog = true
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text(if (isAdminLoggedIn) "👑" else "🔑", fontSize = 14.sp)
                            }
                        }

                        // Ayarlar
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("⚙️", fontSize = 14.sp)
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
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            val configuration = LocalConfiguration.current
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
                                        checkResult = checkResults[repo.url],
                                        onFavorite = {
                                            val index = repos.indexOf(repo)
                                            if (index >= 0) {
                                                repos[index] = repo.copy(favorite = !repo.favorite)
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
                                            openCloudStreamAndPrepareRepo(context, repo, onNotInstalled = { showCloudStreamNotInstalledDialog = true })
                                        },
                                        onCheck = {
                                            checkResults[repo.url] = "⏳ Kontrol ediliyor..."
                                            checkRepoUrl(repo.url) { result -> checkResults[repo.url] = result }
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
    }

    /* =========================================================
       EKLE
       ========================================================= */

    if (showSettingsDialog) {

        SettingsDialog(

            onDismiss = {
                showSettingsDialog = false
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
                Toast.makeText(context, "Merkezi katalog ve sürüm güncellemeleri kontrol ediliyor...", Toast.LENGTH_SHORT).show()

                // 1. Merkezi Repo Kataloğu Güncelleme Kontrolü
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

                // 2. Uygulama Versiyon Kontrolü
                AppUpdateManager.checkForUpdates(BuildConfig.VERSION_NAME) { info ->
                    checkingUpdate = false
                    if (info != null && info.isUpdateAvailable) {
                        updateInfo = info
                        showUpdateDialog = true
                        showSettingsDialog = false
                    } else if (info != null) {
                        Toast.makeText(context, "Uygulamanız en güncel sürümde (v${BuildConfig.VERSION_NAME})", Toast.LENGTH_SHORT).show()
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

            onAdd = { name, url, code, category ->

                val cleanName =
                    name.trim()

                val cleanUrl =
                    url.trim()

                val cleanCode =
                    code.trim()

                val cleanCategory =
                    category.trim()
                        .ifBlank {
                            "Genel"
                        }

                when {

                    cleanName.isBlank() -> {

                        Toast.makeText(
                            context,
                            "Repo adı boş bırakılamaz",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    cleanUrl.isBlank() -> {

                        Toast.makeText(
                            context,
                            "Repo linki boş bırakılamaz",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    !cleanUrl.startsWith("http://") &&
                            !cleanUrl.startsWith("https://") -> {

                        Toast.makeText(
                            context,
                            "Link http:// veya https:// ile başlamalı",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    cleanCode.isBlank() -> {

                        Toast.makeText(
                            context,
                            "Kısa kod boş bırakılamaz",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    repos.any {
                        it.url.equals(
                            cleanUrl,
                            ignoreCase = true
                        )
                    } -> {

                        Toast.makeText(
                            context,
                            "Bu repo linki zaten kayıtlı",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {

                        val newRepo = Repo(
                            name = cleanName,
                            url = cleanUrl,
                            code = cleanCode,
                            category = cleanCategory
                        )

                        repos.add(newRepo)

                        saveRepos(
                            context,
                            repos
                        )

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
                            Toast.makeText(
                                context,
                                "Repo eklendi",
                                Toast.LENGTH_SHORT
                            ).show()
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

    if (
        showEditDialog &&
        selectedRepo != null
    ) {

        EditRepoDialog(

            repo =
                selectedRepo!!,

            onDismiss = {

                showEditDialog = false
                selectedRepo = null
            },

            onSave = { name, url, code, category ->

                val oldRepo =
                    selectedRepo

                if (oldRepo != null) {

                    val cleanName =
                        name.trim()

                    val cleanUrl =
                        url.trim()

                    val cleanCode =
                        code.trim()

                    val cleanCategory =
                        category.trim()
                            .ifBlank {
                                "Genel"
                            }

                    when {

                        cleanName.isBlank() -> {

                            Toast.makeText(
                                context,
                                "Repo adı boş bırakılamaz",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        cleanUrl.isBlank() -> {

                            Toast.makeText(
                                context,
                                "Repo linki boş bırakılamaz",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !cleanUrl.startsWith("http://") &&
                                !cleanUrl.startsWith("https://") -> {

                            Toast.makeText(
                                context,
                                "Link http:// veya https:// ile başlamalı",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        cleanCode.isBlank() -> {

                            Toast.makeText(
                                context,
                                "Kısa kod boş bırakılamaz",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        repos.any {

                            it != oldRepo &&
                                    it.url.equals(
                                        cleanUrl,
                                        ignoreCase = true
                                    )
                        } -> {

                            Toast.makeText(
                                context,
                                "Bu repo linki başka bir kayıtta kullanılıyor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {

                            val index =
                                repos.indexOf(oldRepo)

                            if (index >= 0) {

                                checkResults.remove(
                                    oldRepo.url
                                )

                                repos[index] =
                                    oldRepo.copy(

                                        name =
                                            cleanName,

                                        url =
                                            cleanUrl,

                                        code =
                                            cleanCode,

                                        category =
                                            cleanCategory
                                    )

                                saveRepos(
                                    context,
                                    repos
                                )

                                showEditDialog = false
                                selectedRepo = null

                                if (isAdminLoggedIn) {
                                    Toast.makeText(context, "Repo düzenlendi, GitHub'a senkronize ediliyor...", Toast.LENGTH_SHORT).show()
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
                                        "Repo güncellendi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
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
                        "Mevcut Sürüm: v1.0.0",
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
   REPO KARTI
   ========================================================= */

@Composable
fun RepoCard(
    repo: Repo,
    checkResult: String?,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyLink: () -> Unit,
    onCopyCode: () -> Unit,
    onAddToCloudStream: () -> Unit,
    onCheck: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) CyberCyan else CyberBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .size(36.dp)
                            .background(CyberSurfaceDark, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📦", fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = repo.name,
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "[ ${repo.category.uppercase()} ]",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (repo.code.isNotBlank()) {
                                Text("•", color = CyberTextSecondary, fontSize = 10.sp)
                                Text(
                                    text = "CODE: ${repo.code}",
                                    color = CyberPink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(if (repo.favorite) "⭐" else "☆", color = CyberYellow)
                }
            }

            Spacer(Modifier.height(12.dp))

            // URL & Description
            Text(
                text = repo.url,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                maxLines = 1
            )

            if (checkResult != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = checkResult,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        checkResult.startsWith("🟢") -> CyberGreen
                        checkResult.startsWith("🟡") -> CyberYellow
                        else -> CyberPink
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Actions (Compact)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TvFilledTonalButton(onClick = onAddToCloudStream, modifier = Modifier.weight(1.2f)) {
                    Text("CS'ye Aktar", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                TvOutlinedButton(onClick = onCheck, modifier = Modifier.weight(1f)) {
                    Text("Kontrol Et", fontSize = 11.sp, maxLines = 1)
                }
                TvOutlinedButton(
                    onClick = {
                        if (repo.code.isNotBlank()) onCopyCode() else onCopyLink()
                    },
                    modifier = Modifier.weight(0.7f)
                ) {
                    Text(if (repo.code.isNotBlank()) "Kod" else "Link", fontSize = 11.sp, maxLines = 1)
                }
                if (ENABLE_ADMIN_PANEL_FEATURE) {
                    TvOutlinedButton(onClick = onEdit, modifier = Modifier.weight(0.5f)) {
                        Text("✏️", fontSize = 11.sp, maxLines = 1)
                    }
                    TvOutlinedButton(onClick = onDelete, modifier = Modifier.weight(0.5f)) {
                        Text("🗑️", fontSize = 11.sp, maxLines = 1)
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

                HorizontalDivider()

                Text(
                    "CloudStream Repo Manager",
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
   EKLEME DİYALOĞU
   ========================================================= */

@Composable
fun AddRepoDialog(

    onDismiss: () -> Unit,

    onAdd:
        (
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var url by remember {
        mutableStateOf("")
    }

    var code by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("Genel")
    }

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                "Yeni Repo Ekle",
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column(

                modifier =
                    Modifier.verticalScroll(
                        rememberScrollState()
                    )
            ) {

                OutlinedTextField(

                    value =
                        name,

                    onValueChange = {
                        name = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Repo adı")
                    },

                    placeholder = {
                        Text("Örn. Kraptor")
                    },

                    singleLine = true
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        url,

                    onValueChange = {
                        url = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Repo linki")
                    },

                    placeholder = {
                        Text("https://...")
                    },

                    singleLine = true
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        code,

                    onValueChange = {
                        code = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Kısa kod")
                    },

                    placeholder = {
                        Text("Örn. kraptorcs")
                    },

                    singleLine = true
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        category,

                    onValueChange = {
                        category = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Kategori")
                    },

                    placeholder = {
                        Text("Örn. Türkçe")
                    },

                    singleLine = true
                )
            }
        },

        confirmButton = {

            TvButton(

                onClick = {

                    onAdd(
                        name,
                        url,
                        code,
                        category
                    )
                }
            ) {

                Text(
                    "Ekle"
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

/* =========================================================
   DÜZENLEME DİYALOĞU
   ========================================================= */

@Composable
fun EditRepoDialog(

    repo: Repo,

    onDismiss: () -> Unit,

    onSave:
        (
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var name by remember(repo) {
        mutableStateOf(repo.name)
    }

    var url by remember(repo) {
        mutableStateOf(repo.url)
    }

    var code by remember(repo) {
        mutableStateOf(repo.code)
    }

    var category by remember(repo) {
        mutableStateOf(repo.category)
    }

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                "Repo Düzenle",
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column(

                modifier =
                    Modifier.verticalScroll(
                        rememberScrollState()
                    )
            ) {

                OutlinedTextField(

                    value =
                        name,

                    onValueChange = {
                        name = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Repo adı")
                    },

                    singleLine = true
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        url,

                    onValueChange = {
                        url = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Repo linki")
                    },

                    singleLine = true
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        code,

                    onValueChange = {
                        code = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Kısa kod")
                    },

                    singleLine = true
                )

                Spacer(
                    Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        category,

                    onValueChange = {
                        category = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Kategori")
                    },

                    singleLine = true
                )
            }
        },

        confirmButton = {

            TvButton(

                onClick = {

                    onSave(
                        name,
                        url,
                        code,
                        category
                    )
                }
            ) {

                Text(
                    "Kaydet"
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
