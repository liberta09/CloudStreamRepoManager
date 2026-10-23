package com.kaan.cloudstreamrepomanager

import org.json.JSONObject
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class PluginTransferUrlTest {

    @Test
    fun testAllPluginTransferUrlsExistLocally() {
        val jsonFile = File("../central_repos.json")
        val content = if (jsonFile.exists()) {
            jsonFile.readText(Charsets.UTF_8)
        } else {
            // Fallback for different test working directories
            File("../../central_repos.json").readText(Charsets.UTF_8)
        }
        
        val json = JSONObject(content)
        val repos = json.optJSONArray("repos") ?: throw Exception("Invalid JSON structure")
        
        var pluginCount = 0
        for (i in 0 until repos.length()) {
            val r = repos.getJSONObject(i)
            if (r.optString("type").lowercase() == "nuvio" && r.optString("nuvioKind").lowercase() == "plugin") {
                pluginCount++
                val name = r.optString("name", "")
                val url = r.optString("url", "")
                
                val transferUrl = CentralRepoApiManager.getRepoNuvioManifestUrl(name, url)
                
                // If it's a self-referencing URL, it should return its own URL.
                // Otherwise, it must be a slug URL and not the global bundle.
                if (url.contains("/liberta09/cloudstreamrepomanager/", ignoreCase = true) &&
                    url.contains("/nuvio/", ignoreCase = true)) {
                    org.junit.Assert.assertEquals("Self-referencing should return its own URL", url, transferUrl)
                } else {
                    org.junit.Assert.assertNotEquals("Should not return global bundle URL for external repos", 
                        CentralRepoApiManager.NUVIO_PLUGIN_BUNDLE_URL, 
                        transferUrl)
                }
                
                // Construct expected local file path from transferUrl
                // e.g. https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/wio-sinema-plugin/manifest.json
                // local path: nuvio/wio-sinema-plugin/manifest.json
                val localPathSuffix = transferUrl.substringAfter("/main/")
                val localFile1 = File("../$localPathSuffix")
                val localFile2 = File("../../$localPathSuffix")
                val localFile3 = File(localPathSuffix)
                
                assertTrue("Local manifest file should exist for plugin: $name at path $localPathSuffix", 
                    localFile1.exists() || localFile2.exists() || localFile3.exists())
            }
        }
        
        // Ensure we tested at least some plugins
        assertTrue("Expected at least 10 plugins to test, found: $pluginCount", pluginCount >= 10)
    }
}
