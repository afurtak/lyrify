package com.afurtak.lyrify.data

import okhttp3.*
import java.io.IOException
import java.util.concurrent.CountDownLatch

data class Song(val title: String, val artist: String) {

    fun getLyrics(): String? {
        val url = createUrlForSong()
        val htmlCode = getWebPage(url) ?: return null
        return parseLyrics(htmlCode)
    }

    private fun createUrlForSong(): String {
        val artist = this.artist
                .replace(' ', '_')
                .replace('&', '_')
                .toLowerCase()
        val title = this.title
                .replace(' ', '_')
                .replace('&', '_')
                .toLowerCase()

        println("$artist, $title")
        return "https://www.tekstowo.pl/piosenka,$artist,$title.html"
    }

    private fun getWebPage(url: String) : String? {
        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .build()

        var result: String? = null
        val countDownLatch = CountDownLatch(1)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = null
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                result = response.body()!!.string()
                countDownLatch.countDown()
            }
        })

        countDownLatch.await()

        return result
    }

    private fun parseLyrics(fullPageCode: String) : String? {
        return fullPageCode
                .substringAfter("Tekst piosenki:</h2><br />")
                .substringBefore("<p>&nbsp;</p>")
                .trim()
                .replace("<br />", "")
    }
}