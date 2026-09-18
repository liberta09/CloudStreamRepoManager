
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.kaan.cloudstreamrepomanager.ui.theme.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
private fun TvOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    TvFocusButtonContainer(
        focused = focused,
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
            enabled = enabled,
            content = content
        )
    }
}

@Composable
private fun TvButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    TvFocusButtonContainer(
        focused = focused,
        modifier = modifier
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
            enabled = enabled,
            content = content
        )
    }
}

@Composable
private fun TvFocusButtonContainer(
    focused: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .then(
                if (focused) {
                    Modifier
                        .border(2.dp, CyberYellow, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                } else {
                    Modifier.padding(4.dp)
                }
            )
    ) {
        content()
    }
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
                text = "⚡ CYBER // CS REPO MANAGER",
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

fun reposToJson(repos: List<Repo>): String {

    val array = JSONArray()

    repos.forEach { repo ->
        val obj = JSONObject()

        obj.put("name", repo.name)
        obj.put("url", repo.url)
        obj.put("code", repo.code)
        obj.put("category", repo.category)
        obj.put("favorite", repo.favorite)

        array.put(obj)
    }

    return array.toString(2)
}

fun jsonToRepos(json: String): List<Repo> {

    val array = JSONArray(json)
    val result = mutableListOf<Repo>()

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
            name = "PLT Stream",
            url = "https://raw.githubusercontent.com/pltmustafa/plt-stream/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = true
        ),
        Repo(
            name = "Kraptor CS-TR",
            url = "https://raw.githubusercontent.com/Kraptor/CS-TR/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = true
        ),
        Repo(
            name = "Manitux",
            url = "https://raw.githubusercontent.com/manitux-app/cs-plugins/refs/heads/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "WioSpor",
            url = "https://raw.githubusercontent.com/Wiojelt/WioSpor/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "SafakStream",
            url = "https://raw.githubusercontent.com/SafakStream/SafakStream/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Neoser Extensions",
            url = "https://raw.githubusercontent.com/neoser1984/cloudstream-extensions/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "BTVault",
            url = "https://raw.githubusercontent.com/baristomruk-max/BTVault/main/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "CloudStreamHub",
            url = "https://raw.githubusercontent.com/Emre-Kahveci/CloudStreamHub/builds/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Dr-Octagon Turkish",
            url = "https://github.com/Dr-Octagon/cloudstream-turkish/raw/refs/heads/builds/repo_stable.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "CS Kraptor Aytzey",
            url = "https://raw.githubusercontent.com/aytzey/cs-kraptor/refs/heads/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        ),
        Repo(
            name = "Nik CloudStream",
            url = "https://raw.githubusercontent.com/csprofesor/nik-cloudstream/master/repo.json",
            code = "",
            category = "Türkçe",
            favorite = false
        )
    )
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

    val defaultList = getDefaultRepos()

    if (json.isNullOrBlank()) {
        saveRepos(context, defaultList)
        return defaultList
    }

    return try {
        val savedRepos = jsonToRepos(json)
        val savedUrls = savedRepos.map { it.url.lowercase().trim() }.toSet()

        val missingDefaults = defaultList.filter { !savedUrls.contains(it.url.lowercase().trim()) }

        if (missingDefaults.isNotEmpty()) {
            val mergedList = savedRepos + missingDefaults
            saveRepos(context, mergedList)
            mergedList
        } else {
            savedRepos
        }
    } catch (_: Exception) {
        saveRepos(context, defaultList)
        defaultList
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

    Scaffold(
        containerColor = CyberBgDark,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberSurfaceDark,
                    titleContentColor = CyberYellow
                ),
                title = {
                    Column {
                        Text(
                            text = "⚡ CYBER // CS REPO MANAGER",
                            fontWeight = FontWeight.Bold,
                            color = CyberYellow,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "SYSTEM // REPO_KATALOG_V2.0",
                            color = CyberCyan,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            showSettingsDialog = true
                        }
                    ) {
                        Text("⚙️ AYARLAR", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

    ) { padding ->

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(14.dp)
        ) {

            /* =================================================
               İSTATİSTİKLER
               ================================================= */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                StatCard(
                    emoji = "📦",
                    value = repos.size.toString(),
                    title = "Toplam Repo",
                    modifier =
                        Modifier.weight(1f)
                )

                StatCard(
                    emoji = "⭐",
                    value = favoriteCount.toString(),
                    title = "Favori",
                    modifier =
                        Modifier.weight(1f)
                )
            }

            Spacer(
                Modifier.height(10.dp)
            )

            /* =================================================
               KONTROL ÖZETİ
               ================================================= */

            if (checkResults.isNotEmpty()) {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(10.dp)
                    ) {

                        Text(
                            text =
                                "🔎 Kontrol Sonuçları",
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "🟢 Geçerli: $validRepoCount    🟡 Uyarı: $warningCount    🟠/🔴 Sorunlu: $failedCount"
                        )
                    }
                }

                Spacer(
                    Modifier.height(8.dp)
                )
            }

            /* =================================================
               ARAMA
               ================================================= */

            OutlinedTextField(

                value = searchText,

                onValueChange = {
                    searchText = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("🔎 Repo ara")
                },

                placeholder = {
                    Text(
                        "İsim, link, kod veya kategori"
                    )
                },

                singleLine = true
            )

            Spacer(
                Modifier.height(8.dp)
            )

            /* =================================================
               EKLE / FAVORİ
               ================================================= */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                TvButton(

                    onClick = {
                        showAddDialog = true
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "+ Repo Ekle"
                    )
                }

                TvOutlinedButton(

                    onClick = {

                        showFavoritesOnly =
                            !showFavoritesOnly
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(

                        if (showFavoritesOnly)
                            "Tümünü Göster"
                        else
                            "⭐ Favoriler"
                    )
                }
            }

            Spacer(
                Modifier.height(8.dp)
            )

            /* =================================================
               YEDEK / GERİ YÜKLE
               ================================================= */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                TvOutlinedButton(

                    onClick = {

                        backupLauncher.launch(
                            "cloudstream_repos_backup.json"
                        )
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "💾 Yedekle"
                    )
                }

                TvOutlinedButton(

                    onClick = {

                        restoreLauncher.launch(
                            arrayOf(
                                "application/json",
                                "text/json",
                                "text/plain",
                                "*/*"
                            )
                        )
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "📥 Geri Yükle"
                    )
                }
            }

            Spacer(
                Modifier.height(8.dp)
            )



            /* =================================================
               TÜM LİNKLERİ KONTROL
               ================================================= */

            TvButton(

                onClick = {

                    if (repos.isEmpty()) {

                        Toast.makeText(
                            context,
                            "Kontrol edilecek repo yok",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        checkingAll = true
                        checkResults.clear()

                        checkAllRepoUrls(

                            repos = repos.toList(),

                            onStart = { message ->
                                checkingMessage =
                                    message
                            },

                            onResult = { url, result ->
                                checkResults[url] =
                                    result
                            },

                            onFinished = {

                                checkingAll = false
                                checkingMessage =
                                    "✅ Tüm repo kontrolleri tamamlandı"

                                Toast.makeText(
                                    context,
                                    "Tüm linkler kontrol edildi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                },

                enabled =
                    !checkingAll,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(

                    if (checkingAll)
                        "⏳ $checkingMessage"
                    else
                        "🔎 Tüm Linkleri Kontrol Et"
                )
            }

            if (
                checkingAll &&
                checkingMessage.isNotBlank()
            ) {

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(
                    text =
                        checkingMessage,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )



            Text(

                text =
                    "Gösterilen ${filteredRepos.size} / ${repos.size} repo",

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            HorizontalDivider()

            Spacer(
                Modifier.height(7.dp)
            )

            /* =================================================
               LİSTE
               ================================================= */

            BoxWithConstraints(

                modifier =
                    Modifier.weight(1f)

            ) {

                // Telefon: 1 sütun. Geniş ekran / Android TV: 2 veya 3 sütun.
                val columns =
                    when {
                        maxWidth >= 1100.dp -> 3
                        maxWidth >= 700.dp -> 2
                        else -> 1
                    }

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    if (filteredRepos.isEmpty()) {

                        item {
                            EmptyRepoCard(
                                hasRepos =
                                    repos.isNotEmpty()
                            )
                        }

                    } else {

                        items(
                            filteredRepos.chunked(columns)
                        ) { rowRepos ->

                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {

                                rowRepos.forEach { repo ->

                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {

                                        RepoCard(

                                            repo = repo,

                                            checkResult =
                                                checkResults[repo.url],

                                            onFavorite = {

                                                val index =
                                                    repos.indexOf(repo)

                                                if (index >= 0) {

                                                    repos[index] =
                                                        repo.copy(
                                                            favorite =
                                                                !repo.favorite
                                                        )

                                                    saveRepos(
                                                        context,
                                                        repos
                                                    )
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

                                            onCopyLink = {
                                                copyToClipboard(
                                                    context,
                                                    repo.url,
                                                    "Repo linki kopyalandı"
                                                )
                                            },

                                            onCopyCode = {
                                                copyToClipboard(
                                                    context,
                                                    repo.code,
                                                    "Kısa kod kopyalandı"
                                                )
                                            },

                                            onAddToCloudStream = {
                                                openCloudStreamAndPrepareRepo(
                                                    context,
                                                    repo,
                                                    onNotInstalled = {
                                                        showCloudStreamNotInstalledDialog = true
                                                    }
                                                )
                                            },

                                            onCheck = {

                                                checkResults[repo.url] =
                                                    "⏳ Kontrol ediliyor..."

                                                checkRepoUrl(
                                                    repo.url
                                                ) { result ->
                                                    checkResults[repo.url] = result
                                                }
                                            }
                                        )
                                    }
                                }

                                // Son satır eksik hücre içeriyorsa boşluk bırak.
                                repeat(columns - rowRepos.size) {
                                    Spacer(
                                        modifier =
                                            Modifier.weight(1f)
                                    )
                                }
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

                        repos.add(
                            Repo(
                                name = cleanName,
                                url = cleanUrl,
                                code = cleanCode,
                                category = cleanCategory
                            )
                        )

                        saveRepos(
                            context,
                            repos
                        )

                        showAddDialog = false

                        Toast.makeText(
                            context,
                            "Repo başarıyla eklendi",
                            Toast.LENGTH_SHORT
                        ).show()
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

                    repos.remove(repo)

                    checkResults.remove(
                        repo.url
                    )

                    saveRepos(
                        context,
                        repos
                    )

                    if (
                        selectedCategory != "Tümü" &&
                        repos.none {
                            it.category ==
                                    selectedCategory
                        }
                    ) {
                        selectedCategory =
                            "Tümü"
                    }

                    Toast.makeText(
                        context,
                        "Repo silindi",
                        Toast.LENGTH_SHORT
                    ).show()
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
    val borderColor = if (repo.favorite) CyberYellow else CyberBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (repo.favorite) "⭐ ${repo.name}" else repo.name,
                    fontWeight = FontWeight.Bold,
                    color = if (repo.favorite) CyberYellow else CyberTextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "[ ${repo.category.uppercase()} ]",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (repo.code.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "🔑 CODE // ${repo.code}",
                    color = CyberPink,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = repo.url,
                color = CyberTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TvOutlinedButton(
                    onClick = onFavorite,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (repo.favorite) "★ Favoriden Çıkar" else "⭐ Favori", color = CyberYellow)
                }

                TvOutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("✏️ Düzenle", color = CyberCyan)
                }

                TvOutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🗑️ Sil", color = CyberPink)
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TvOutlinedButton(
                    onClick = onCopyLink,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔗 Linki Kopyala", color = CyberCyan)
                }

                if (repo.code.isNotBlank()) {
                    TvOutlinedButton(
                        onClick = onCopyCode,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🔑 Kodu Kopyala", color = CyberCyan)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            TvButton(
                onClick = onAddToCloudStream,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⚡ CLOUDSTREAM'E AKTAR", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(6.dp))

            TvOutlinedButton(
                onClick = onCheck,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔎 Link Durumunu Kontrol Et", color = CyberTextPrimary)
            }

            if (checkResult != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = checkResult,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        checkResult.startsWith("🟢") -> CyberGreen
                        checkResult.startsWith("🟡") -> CyberYellow
                        else -> CyberPink
                    }
                )
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

    onRestoreDefaults: () -> Unit
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
