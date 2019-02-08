package com.afurtak.lyrify.fragments

import android.os.Handler
import androidx.work.WorkInfo
import com.afurtak.lyrify.fragments.LyricsFragment
import com.afurtak.lyrify.songutils.*

class ListeningLyricsFragment : LyricsFragment() {

    val delay = 2000L

    val handler = Handler()
    val runnable: Runnable by lazy {
        Runnable {
            getData()
            handler.postDelayed(runnable, delay)
        }
    }

    fun getData() {
        SpotifyUtils.getCurrentlyPlaying(this) {
            if (it.state == WorkInfo.State.SUCCEEDED) {
                val title = it.outputData.getString("title")
                val lyrics = it.outputData.getString("lyrics")
                titleView.text = title
                lyricsView.text = lyrics
            }
            if (it.state == WorkInfo.State.FAILED) {
                titleView.text = "No name"
                lyricsView.text = "Finding lyrics for this song is not possible"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, delay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}