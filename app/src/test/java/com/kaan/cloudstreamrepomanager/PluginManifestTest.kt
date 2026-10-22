package com.kaan.cloudstreamrepomanager

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PluginManifestTest {

    @Test
    fun testManifestDeduplicationLogic() {
        val scrapersFromRepoA = JSONArray().apply {
            put(JSONObject().put("id", "scraper1").put("filename", "http://example.com/s1.js").put("name", "S1").put("version", "1.0"))
            put(JSONObject().put("id", "scraper2").put("filename", "http://example.com/s2.js").put("name", "S2").put("version", "1.0"))
        }

        val scrapersFromRepoB = JSONArray().apply {
            // Duplicate filename as RepoA's scraper1
            put(JSONObject().put("id", "scraper1_b").put("filename", "http://example.com/s1.js").put("name", "S1_B").put("version", "1.0"))
            // Unique scraper
            put(JSONObject().put("id", "scraper3").put("filename", "http://example.com/s3.js").put("name", "S3").put("version", "1.0"))
        }

        val allScrapers = listOf(scrapersFromRepoA, scrapersFromRepoB)
        
        val globalScrapers = JSONArray()
        val usedFilenames = mutableSetOf<String>()
        val usedIds = mutableSetOf<String>()

        for ((repoIndex, repoScrapers) in allScrapers.withIndex()) {
            val slug = "repo${repoIndex}"
            for (i in 0 until repoScrapers.length()) {
                val src = repoScrapers.getJSONObject(i)
                val originalId = src.getString("id")
                val absoluteFilename = src.getString("filename")
                val newId = "${slug}__${originalId}"

                if (usedFilenames.add(absoluteFilename.lowercase())) {
                    val globalScraper = JSONObject(src.toString())
                    var finalId = newId
                    var counter = 1
                    while (!usedIds.add(finalId)) {
                        finalId = "${newId}_$counter"
                        counter++
                    }
                    globalScraper.put("id", finalId)
                    globalScrapers.put(globalScraper)
                }
            }
        }

        // We expect only 3 scrapers in the global bundle because s1.js is duplicated
        assertEquals(3, globalScrapers.length())
        
        val firstGlobal = globalScrapers.getJSONObject(0)
        assertEquals("repo0__scraper1", firstGlobal.getString("id"))
        assertEquals("http://example.com/s1.js", firstGlobal.getString("filename"))

        val secondGlobal = globalScrapers.getJSONObject(1)
        assertEquals("repo0__scraper2", secondGlobal.getString("id"))
        assertEquals("http://example.com/s2.js", secondGlobal.getString("filename"))

        val thirdGlobal = globalScrapers.getJSONObject(2)
        assertEquals("repo1__scraper3", thirdGlobal.getString("id"))
        assertEquals("http://example.com/s3.js", thirdGlobal.getString("filename"))
        
        assertTrue("Scrapers array should not be empty", globalScrapers.length() > 0)
    }
}
