package com.afurtak.lyrify.spotifyapiutils

import android.app.Activity
import android.content.Context
import com.afurtak.lyrify.Song
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CountDownLatch

object CurrentlyPlaying {

    private val url = "https://api.spotify.com/v1/me/player/currently-playing"
    private val tokenHeader = "Authorization"

    var spotifyAccesToken = ""
    val spotifyClientId = "1a9664b8e378430285f036a4783b1ac4"
    val spotifyRedirectUri = "https://example.com/callback/"

    fun getCurrentluPlaying(accesToken: String): Song? {
        val responseFromSpotify = getResponseFromSPotify(accesToken)
        responseFromSpotify ?: return null

        return parseResponse(responseFromSpotify)
    }

    fun getResponseFromSPotify(accesToken: String): JSONObject? {

        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .addHeader(tokenHeader, "Bearer $accesToken")
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
        return JSONObject(result)
    }

    fun parseResponse(response: JSONObject) : Song? {
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

object SpotifyAuthorizationUtils {
    fun getSpotifyAuthorizationToken(activity: Activity) {
        val builder = AuthenticationRequest.Builder(CurrentlyPlaying.spotifyClientId, AuthenticationResponse.Type.TOKEN, CurrentlyPlaying.spotifyRedirectUri)

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(activity, LoginActivity.REQUEST_CODE, request)
    }
}