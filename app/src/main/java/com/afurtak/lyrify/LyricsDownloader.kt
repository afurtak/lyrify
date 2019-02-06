package com.afurtak.lyrify

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters


class LyricsDownloader(
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
        Log.d("Adam", lyrics)

        val outputData = Data.Builder()
                .putString("title", title)
                .putString("lyrics", lyrics)
                .build()

        return Result.success(outputData)
    }

}