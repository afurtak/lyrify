package com.afurtak.lyrify

import android.arch.lifecycle.Observer
import android.os.Handler
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.afurtak.lyrify.spotifyapiutils.CurrentlyPlaying

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
        val task = OneTimeWorkRequest.Builder(CurrentlyPlaying::class.java)
                .build()

        WorkManager
                .getInstance()
                .enqueue(task)

        WorkManager
                .getInstance()
                .getWorkInfoByIdLiveData(task.id)
                .observe(this, Observer {
                    if (it != null) {
                        if (it.state == WorkInfo.State.SUCCEEDED) {
                            val title = it.outputData.getString("title")
                            val lyrics = it.outputData.getString("lyrics")
                            titleView.text = title
                            lyricsView.text = lyrics
                        }
                    }
                })
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