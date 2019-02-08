package com.afurtak.lyrify

import android.os.Handler
import android.util.Log

class ListeningLyricsActivity : LyricsFragment() {

    val delay = 1000L

    val handler = Handler()
    val runnable: Runnable by lazy {
        Runnable {
            Log.d("Song", "Listening for a song.")
            handler.postDelayed(runnable, delay)
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