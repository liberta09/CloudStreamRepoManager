package com.kaan.cloudstreamrepomanager

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayDeepLinkTest {

    private val url = "https://example.com/a/repo.json"
    private val enc = "https%3A%2F%2Fexample.com%2Fa%2Frepo.json"

    @Test
    fun csRepoUsesRepoParam() {
        assertEquals("play://add?repo=$enc", Repo("x", url, category = "c", type = "cloudstream").playDeepLink())
    }

    @Test
    fun nuvioAddonUsesNuvioParam() {
        assertEquals("play://add?nuvio=$enc", Repo("x", url, category = "c", type = "nuvio").playDeepLink())
    }

    @Test
    fun nuvioPluginUsesNuvioPluginParam() {
        val repo = Repo("x", url, category = "c", type = "nuvio", nuvioKind = NUVIO_KIND_PLUGIN)
        assertEquals("play://add?nuvio_plugin=$enc", repo.playDeepLink())
    }
}
