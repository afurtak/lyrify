package com.afurtak.lyrify.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.work.WorkInfo
import com.afurtak.lyrify.data.Song
import com.afurtak.lyrify.songutils.LyricsUtils


open class DisplayingLyricsFragment : LyricsFragment() {

    var song: Song = Song("", "")
    set(value) {
        field = value
        dataWasChanged = true
    }

    var dataWasChanged: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val tmpRoot = super.onCreateView(inflater, container, savedInstanceState)

        if (!dataWasChanged && savedInstanceState != null)
            restoreData(savedInstanceState)

        if (dataWasChanged)
            setContent(song)

        return tmpRoot
    }

    fun setContent(song: Song) {
        LyricsUtils.getLyrics(this, song) {
            if (it.state == WorkInfo.State.SUCCEEDED) {
                val lyrics = it.outputData.getString("lyrics")

                titleView.text = song.title
                lyricsView.text = lyrics

                dataWasChanged = false
            }
        }
    }
}
