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

class MainActivity : AppCompatActivity() {

    private lateinit var titleInput : EditText
    private lateinit var artistInput : EditText
    private lateinit var submitButton : Button
    private lateinit var searchForLyricsFromSpotifyButton : Button
    private lateinit var fab : FloatingActionButton

    private lateinit var resultLayout : LinearLayout
    private lateinit var resultTextView : TextView

    private lateinit var inputFormLayout : LinearLayout
    private lateinit var downloadingProgressBar: ProgressBar

    private var spotifyAccesToken = ""
    private val spotifyClientId = "1a9664b8e378430285f036a4783b1ac4"
    private val spotifyRedirectUri = "https://example.com/callback/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputFormLayout = findViewById(R.id.input_form_layout)
        downloadingProgressBar = findViewById(R.id.download_progress_bar)
        fab = findViewById(R.id.fab)

        resultLayout = findViewById(R.id.result_layout)
        resultTextView = findViewById(R.id.lyrics_text_view)

        titleInput = findViewById(R.id.title_input)
        artistInput = findViewById(R.id.artist_input)
        submitButton = findViewById(R.id.search_for_lyrics)
        searchForLyricsFromSpotifyButton = findViewById(R.id.get_spotify_song_lyric)

        submitButton.setOnClickListener {
            searchForLyrics()
        }

        searchForLyricsFromSpotifyButton.setOnClickListener {
            if (spotifyAccesToken == "")
                getSpotifyAuthorizationToken()
            else {
                startListeningForSpotifySongs()
            }
        }

        fab.setOnClickListener {
            titleInput.setText("")
            artistInput.setText("")

            resultLayout.visibility = View.GONE
            inputFormLayout.visibility = View.VISIBLE
        }
    }

    private fun startListeningForSpotifySongs() {
        SpotifyListener().execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, resultIntent)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Toast.makeText(this, "successful authentication", Toast.LENGTH_SHORT).show()
                    spotifyAccesToken = response.accessToken
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

    private fun getSpotifyAuthorizationToken() {
        val builder = AuthenticationRequest.Builder(spotifyClientId, AuthenticationResponse.Type.TOKEN, spotifyRedirectUri)

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    fun searchForLyrics() {
        val title = titleInput.text.toString()
        val artist = artistInput.text.toString()
        if (title.isEmpty()) {
            Toast.makeText(this, "Please, enter the title of song", Toast.LENGTH_LONG).show()
            return
        }

        if (artist.isEmpty()) {
            Toast.makeText(this, "Please, enter the name of artist", Toast.LENGTH_LONG).show()
            return
        }
        val song = Song(title, artist)
        LyricsDownloader().execute(song)
    }

    inner class LyricsDownloader : AsyncTask<Song, Any, String?>() {

        override fun onPreExecute() {
            inputFormLayout.visibility = View.GONE
            downloadingProgressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg songs: Song): String? {
            return songs[0].getLyrics()
        }

        override fun onPostExecute(result: String?) {
            downloadingProgressBar.visibility = View.GONE
            resultLayout.visibility = View.VISIBLE
            if (result != null)
                resultTextView.text = result
            else
                resultTextView.text = "Lyrics did not find"
        }

    }

    inner class SpotifyListener : AsyncTask<Unit, String, Unit>() {

        override fun doInBackground(vararg p0: Unit?) {
            var prev: Song? = null
            while (true) {
                val song = CurrentlyPlaying.getCurrentluPlaying(spotifyAccesToken)
                if (song != prev) {
                    prev = song
                    if (song != null) {
                        val lyrics = song.getLyrics()
                        if (lyrics != null)
                            publishProgress(lyrics)
                        else
                            publishProgress("No lyrics found.")
                    } else {
                        publishProgress("Nothing is playing now.")
                    }
                }
                Thread.sleep(1000)
            }
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            downloadingProgressBar.visibility = View.GONE
            inputFormLayout.visibility = View.GONE
            resultLayout.visibility = View.VISIBLE
            resultTextView.text = values[0]
        }
    }
}
