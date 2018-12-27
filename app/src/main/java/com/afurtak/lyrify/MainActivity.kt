package com.afurtak.lyrify

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.*


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

        fab.setOnClickListener {
            titleInput.setText("")
            artistInput.setText("")

            resultLayout.visibility = View.GONE
            inputFormLayout.visibility = View.VISIBLE
        }
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
}
