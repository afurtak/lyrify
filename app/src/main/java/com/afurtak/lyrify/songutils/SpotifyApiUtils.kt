package com.afurtak.lyrify.songutils

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import androidx.work.*
import com.afurtak.lyrify.data.Song
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CountDownLatch


object SpotifyUtils {

    var spotifyAccessToken = ""
    const val url = "https://api.spotify.com/v1/me/player/currently-playing"
    const val tokenHeader = "Authorization"
    const val spotifyClientId = "1a9664b8e378430285f036a4783b1ac4"
    const val spotifyRedirectUri = "https://example.com/callback/"

    fun getCurrentlyPlaying(owner: LifecycleOwner, onPost: (WorkInfo) -> Unit) {
        val task = OneTimeWorkRequest.Builder(SpotifyCurrentlyPlayingWorker::class.java)
                .build()

        WorkManager
                .getInstance()
                .enqueue(task)

        WorkManager
                .getInstance()
                .getWorkInfoByIdLiveData(task.id)
                .observe(owner, Observer {
                    if (it != null)
                        onPost(it)
                })
    }

    fun hasSpotifyAccessToken() = spotifyAccessToken != ""

    fun openSpotifyAuthorization(activity: Activity) {
        val builder = AuthenticationRequest.Builder(SpotifyUtils.spotifyClientId, AuthenticationResponse.Type.TOKEN, SpotifyUtils.spotifyRedirectUri)
        builder.setScopes(arrayOf("user-read-currently-playing"))
        val request = builder.build()
        AuthenticationClient.openLoginActivity(activity, LoginActivity.REQUEST_CODE, request)
    }
}


private class SpotifyCurrentlyPlayingWorker(
        context: Context,
        workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val song = getCurrentlyPlaying()
        song ?: return Result.failure()

        val lyrics = song.getLyrics()
        lyrics ?: return Result.failure()

        val outputData = Data.Builder()
                .putString("title", song.title)
                .putString("artist", song.artist)
                .putString("lyrics", lyrics)
                .build()

        return Result.success(outputData)
    }

    private fun getCurrentlyPlaying(): Song? {
        val responseFromSpotify = getResponseFromSPotify(SpotifyUtils.spotifyAccessToken)
        responseFromSpotify ?: return null

        return parseResponse(responseFromSpotify)
    }

    private fun getResponseFromSPotify(spotifyAccessToken: String): JSONObject? {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(SpotifyUtils.url)
                .addHeader(SpotifyUtils.tokenHeader, "Bearer $spotifyAccessToken")
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
