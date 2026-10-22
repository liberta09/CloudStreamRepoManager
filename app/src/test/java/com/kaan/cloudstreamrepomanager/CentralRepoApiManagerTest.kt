package com.kaan.cloudstreamrepomanager

import org.junit.Assert.assertEquals
import org.junit.Test

class CentralRepoApiManagerTest {

    @Test
    fun testGetRepoNuvioManifestUrl() {
        // Test 1: Self-referencing URL
        val selfReferencingUrl = "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/manifest.json"
        val result1 = CentralRepoApiManager.getRepoNuvioManifestUrl("mooncrown", selfReferencingUrl)
        assertEquals("Self-referencing URL should return itself", selfReferencingUrl, result1)

        // Test 2: External/Regular Nuvio plugin URL (generates a slug-based path)
        val externalUrl = "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/main/manifest.json"
        val result2 = CentralRepoApiManager.getRepoNuvioManifestUrl("wio sinema plugin", externalUrl)
        val expectedSlugUrl = "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/wio-sinema-plugin/manifest.json"
        assertEquals("External URL should generate a slug-based internal path", expectedSlugUrl, result2)

        // Test 3: Complex name with special characters
        val complexUrl = "https://some-other.com/manifest.json"
        val result3 = CentralRepoApiManager.getRepoNuvioManifestUrl("JUSTEMİR84 plugin!", complexUrl)
        val expectedComplexSlugUrl = "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/justemi-r84-plugin/manifest.json"
        assertEquals("Slug generation should handle special characters correctly", expectedComplexSlugUrl, result3)
    }
}