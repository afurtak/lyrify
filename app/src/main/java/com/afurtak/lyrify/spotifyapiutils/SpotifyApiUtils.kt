package com.afurtak.lyrify.spotifyapiutils

import com.afurtak.lyrify.Song
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CountDownLatch

object CurrentlyPlaying {

    private val url = "https://api.spotify.com/v1/me/player/currently-playing"
    private val tokenHeader = "Authorization"

    fun getCurrentlyPlaying(spotifyAccessToken: String): Song? {
        val responseFromSpotify = getResponseFromSPotify(spotifyAccessToken)
        responseFromSpotify ?: return null

        return parseResponse(responseFromSpotify)
    }

    private fun getResponseFromSPotify(spotifyAccessToken: String): JSONObject? {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .addHeader(tokenHeader, "Bearer $spotifyAccessToken")
                .build()

        val countDownLatch = CountDownLatch(1)
        var result: String? = null

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
        result ?: return null
        if (result == "")
            return null
        return JSONObject(result)
    }

    private fun parseResponse(response: JSONObject) : Song? {
        return try {
            val track = response.getJSONObject("item")
            val artists = track.getJSONArray("artists")
            val artist = artists.getJSONObject(0)
            val artistName = artist.getString("name")
            val title = track.getString("name")
            Song(title, artistName)
        }
        catch (e: Exception) {
            null
        }
    }
}
