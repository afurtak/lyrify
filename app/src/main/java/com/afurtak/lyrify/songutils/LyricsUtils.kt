package com.afurtak.lyrify.songutils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import androidx.work.*
import com.afurtak.lyrify.data.Song


object LyricsUtils {
    fun getLyrics(owner: LifecycleOwner, song: Song, onPost: (WorkInfo) -> Unit) {
        val inputData = Data.Builder()
                .putString("title", song.title)
                .putString("artist", song.artist)
                .build()

        val task = OneTimeWorkRequest
                .Builder(LyricsDownloader::class.java)
                .setInputData(inputData)
                .build()

        val workManager = WorkManager.getInstance()

        workManager.enqueue(task)
        workManager.getWorkInfoByIdLiveData(task.id).observe(owner, Observer {
            if (it != null)
                onPost(it)
        })
    }
}


private class LyricsDownloader(
        context: Context,
        workerParameters: WorkerParameters
    ) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        val title: String?
        val artist: String?
        inputData.apply {
            title = getString("title")
            artist = getString("artist")
        }

        if (title == null || artist == null)
            return Result.failure()

        val lyrics = Song(title, artist).getLyrics()
        lyrics ?: return Result.failure()

        val outputData = Data.Builder()
                .putString("title", title)
                .putString("lyrics", lyrics)
                .build()

        return Result.success(outputData)
    }
}
