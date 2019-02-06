package com.afurtak.lyrify

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
import com.afurtak.lyrify.spotifyapiutils.*

class MainActivity : AppCompatActivity(), SearchSongFormFragmentListener, GetSpotifyLricsFragmentListener {
    private lateinit var fab : FloatingActionButton

    private lateinit var getSpotifySongLyricsFragment: GetSpotifySongLyricsFragment
    private lateinit var searchSongFormFragment: SearchSongFormFragment
    private lateinit var lyricsFragment: LyricsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        SpotifyListener().execute()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, resultIntent)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Toast.makeText(this, "successful authentication", Toast.LENGTH_SHORT).show()
                    CurrentlyPlaying.spotifyAccesToken = response.accessToken
                    startListeningForSpotifySongs()
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

    /**
     * Calls when button in GetSpotifySongFragment was pressed.
     */
    override fun onGetLyrics() {
        if (CurrentlyPlaying.spotifyAccesToken == "") {
            SpotifyAuthorizationUtils.getSpotifyAuthorizationToken(this)
        }
        startListeningForSpotifySongs()
    }

    /**
     * Calls when button submit was pressed on SearchForSongFragment.
     */
    override fun onSongFormSubmit(song: Song) {
        LyricsDownloader().execute(song)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, "GetSpotifySongLyricsFragmentKey", getSpotifySongLyricsFragment)
        supportFragmentManager.putFragment(outState, "SearchSongFormFragmentKey", searchSongFormFragment)
    }



    inner class LyricsDownloader : AsyncTask<Song, Any, String?>() {

        var title: String = "no title"

        override fun doInBackground(vararg songs: Song): String? {
            title = songs[0].title
            return songs[0].getLyrics()
        }

        override fun onPostExecute(result: String?) {
            addLyricsFragmentOnStack(title, result ?: "no lyrics")
        }
    }

    inner class SpotifyListener : AsyncTask<Unit, String, Unit>() {

        override fun doInBackground(vararg p0: Unit?) {
            var prev: Song? = null
            while (true) {
                val song = CurrentlyPlaying.getCurrentluPlaying(CurrentlyPlaying.spotifyAccesToken)
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
            lyricsFragment.setContent(values[0]!!, values[1]!!)
        }
    }
}
