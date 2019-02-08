package com.afurtak.lyrify

import android.arch.lifecycle.Observer
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.*
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE
import android.content.Intent
import android.os.PersistableBundle
import androidx.work.*
import com.afurtak.lyrify.spotifyapiutils.*
import com.spotify.sdk.android.authentication.AuthenticationRequest


class MainActivity : AppCompatActivity(), SearchSongFormFragmentListener, GetSpotifyLricsFragmentListener {
    private lateinit var fab : FloatingActionButton

    private lateinit var getSpotifySongLyricsFragment: GetSpotifySongLyricsFragment
    private lateinit var searchSongFormFragment: SearchSongFormFragment
    private lateinit var lyricsFragment: LyricsFragment

    var spotifyAccesToken = ""
    val spotifyClientId = "1a9664b8e378430285f036a4783b1ac4"
    val spotifyRedirectUri = "https://example.com/callback/"

    var isSpotifyListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSpotifyAuthorizationToken()

        if (savedInstanceState != null) {
            restoreData(savedInstanceState)
        }
        else {
            addGetSpotifySongLyricsFragment()
        }
        initializeFab()
    }

    private fun restoreData(savedInstanceState: Bundle) {
        if (savedInstanceState.containsKey("SearchSongFormFragmentKey")) {
            searchSongFormFragment = supportFragmentManager.getFragment(savedInstanceState, "SearchSongFormFragmentKey") as SearchSongFormFragment
        }

        if (savedInstanceState.containsKey("GetSpotifySongLyricsFragmentKey")) {
            getSpotifySongLyricsFragment = supportFragmentManager.getFragment(savedInstanceState, "GetSpotifySongLyricsFragmentKey") as GetSpotifySongLyricsFragment
            supportFragmentManager.beginTransaction()
                    .add(R.id.search_form_container, getSpotifySongLyricsFragment)
                    .commit()
        }
    }

    private fun initializeFab() {
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (!::searchSongFormFragment.isInitialized)
                searchSongFormFragment = SearchSongFormFragment()

            supportFragmentManager.beginTransaction()
                    .replace(R.id.search_form_container, searchSongFormFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun addGetSpotifySongLyricsFragment() {
        getSpotifySongLyricsFragment = GetSpotifySongLyricsFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.search_form_container, getSpotifySongLyricsFragment)
                .commit()
    }

    private fun startListeningForSpotifySongs() {
        if (!::lyricsFragment.isInitialized)
            lyricsFragment = LyricsFragment()
        if(!isSpotifyListening) {
            SpotifyListener().execute()
            isSpotifyListening = true
        }
    }

    private fun addLyricsFragmentOnStack(title: String, lyrics: String) {
        if (!::lyricsFragment.isInitialized)
            lyricsFragment = LyricsFragment.newInstance(title, lyrics)
        else {
            lyricsFragment.setContent(title, lyrics)
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.search_form_container, lyricsFragment)
                .addToBackStack(null)
                .commit()
    }

    private fun addLyricsFragmentOnStack() {
        if (!::lyricsFragment.isInitialized)
            lyricsFragment = LyricsFragment()

        supportFragmentManager.beginTransaction()
                .replace(R.id.search_form_container, lyricsFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, resultIntent)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    spotifyAccesToken = response.accessToken
                    Toast.makeText(this, spotifyAccesToken, Toast.LENGTH_SHORT).show()
                }
                AuthenticationResponse.Type.ERROR ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
                AuthenticationResponse.Type.CODE ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
                AuthenticationResponse.Type.EMPTY ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
                AuthenticationResponse.Type.UNKNOWN ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getSpotifyAuthorizationToken() {
        val builder = AuthenticationRequest.Builder(spotifyClientId, AuthenticationResponse.Type.TOKEN, spotifyRedirectUri)

        builder.setScopes(arrayOf("user-read-currently-playing"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)

    }

    /**
     * Calls when button in GetSpotifySongFragment was pressed.
     */
    override fun onGetLyrics() {
        if (spotifyAccesToken == "") {
            getSpotifyAuthorizationToken()
        }
        addLyricsFragmentOnStack()
        startListeningForSpotifySongs()
    }

    /**
     * Calls when button submit was pressed on SearchForSongFragment.
     */
    override fun onSongFormSubmit(song: Song) {

        val inputData = Data.Builder()
                .putString("title", song.title)
                .putString("artist", song.artist)
                .build()

        val task = OneTimeWorkRequest
                .Builder(com.afurtak.lyrify.LyricsDownloader::class.java)
                .setInputData(inputData)
                .build()

        val workManager = WorkManager.getInstance()

        workManager.enqueue(task)
        workManager.getWorkInfoByIdLiveData(task.id).observe(this, Observer {
            if (it != null) {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    val title = it.outputData.getString("title")
                    val lyrics = it.outputData.getString("lyrics")
                    addLyricsFragmentOnStack(title!!, lyrics!!)
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, "GetSpotifySongLyricsFragmentKey", getSpotifySongLyricsFragment)
        supportFragmentManager.putFragment(outState, "SearchSongFormFragmentKey", searchSongFormFragment)
    }

    inner class SpotifyListener : AsyncTask<Unit, String, Unit>() {

        override fun doInBackground(vararg p0: Unit?) {
            var prev: Song? = null
            while (true) {
                val song = CurrentlyPlaying.getCurrentlyPlaying(spotifyAccesToken)
                if (song != prev) {
                    prev = song
                    if (song != null) {
                        val lyrics = song.getLyrics()
                        if (lyrics != null)
                            publishProgress(song.title, lyrics)
                        else
                            publishProgress(song.title, "No lyrics found.")
                    } else {
                        publishProgress("No title", "Nothing is playing now.")
                    }
                }
                Thread.sleep(1000)
            }
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            if (lyricsFragment.isVisible) {
                lyricsFragment.setContent(values[0]!!, values[1]!!)
            }
            else {
                Toast.makeText(this@MainActivity, "is cancelled", Toast.LENGTH_LONG).show()
                this.cancel(false)
                isSpotifyListening = false
            }
        }
    }
}
