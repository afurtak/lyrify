package com.afurtak.lyrify

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.*
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE
import android.content.Intent
import com.afurtak.lyrify.spotifyapiutils.*

class MainActivity : AppCompatActivity(), SearchSongFormFragmentListener, GetSpotifyLricsFragmentListener {
    private lateinit var fab : FloatingActionButton

    private lateinit var getSpotifySongLyricsFragment: GetSpotifySongLyricsFragment
    private lateinit var searchSongFormFragment: SearchSongFormFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addGetSpotifySongLyricsFragment()

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (!::searchSongFormFragment.isInitialized)
                searchSongFormFragment = SearchSongFormFragment()

            supportFragmentManager.beginTransaction()
                    .replace(R.id.search_form_container, searchSongFormFragment)
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


    override fun onGetLyrics() {

    }

    override fun onSongFormSubmit(song: Song) {

    }

//    fun searchForLyrics() {
//            return
//        }
//
//        if (artist.isEmpty()) {
//            Toast.makeText(this, "Please, enter the name of artist", Toast.LENGTH_LONG).show()
//            return
//        }
//        val song = Song(title, artist)
//        LyricsDownloader().execute(song)
//    }

//    inner class LyricsDownloader : AsyncTask<Song, Any, String?>() {
//
//        override fun onPreExecute() {
//            inputFormLayout.visibility = View.GONE
//            downloadingProgressBar.visibility = View.VISIBLE
//        }
//
//        override fun doInBackground(vararg songs: Song): String? {
//            return songs[0].getLyrics()
//        }
//
//        override fun onPostExecute(result: String?) {
//            downloadingProgressBar.visibility = View.GONE
//            resultLayout.visibility = View.VISIBLE
//            if (result != null)
//                resultTextView.text = result
//            else
//                resultTextView.text = "Lyrics did not find"
//        }
//
//    }
//
//    inner class SpotifyListener : AsyncTask<Unit, String, Unit>() {
//
//        override fun doInBackground(vararg p0: Unit?) {
//            var prev: Song? = null
//            while (true) {
//                val song = CurrentlyPlaying.getCurrentluPlaying(CurrentlyPlaying.spotifyAccesToken)
//                if (song != prev) {
//                    prev = song
//                    if (song != null) {
//                        val lyrics = song.getLyrics()
//                        if (lyrics != null)
//                            publishProgress(lyrics)
//                        else
//                            publishProgress("No lyrics found.")
//                    } else {
//                        publishProgress("Nothing is playing now.")
//                    }
//                }
//                Thread.sleep(1000)
//            }
//        }
//
//        override fun onProgressUpdate(vararg values: String?) {
//            super.onProgressUpdate(*values)
//            downloadingProgressBar.visibility = View.GONE
//            inputFormLayout.visibility = View.GONE
//            resultLayout.visibility = View.VISIBLE
//            resultTextView.text = values[0]
//        }
//    }
}
