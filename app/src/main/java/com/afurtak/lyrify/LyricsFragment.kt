package com.afurtak.lyrify

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.work.WorkInfo
import com.afurtak.lyrify.songutils.LyricsUtils

private const val titleBundleKey = "Title Bundle Key"
private const val lyricsBundleKey = "Lyrics Bundle Key"


open class LyricsFragment : Fragment() {

    var song: Song = Song("", "")
    set(value) {
        field = value
        dataWasChanged = true
    }

    lateinit var titleView: TextView
    lateinit var lyricsView: TextView

    var dataWasChanged: Boolean = false

    lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_lyrics, container, false)
        titleView = root.findViewById(R.id.title)
        lyricsView = root.findViewById(R.id.lyrics)

        if (!dataWasChanged)
            savedInstanceState?.apply {
                if (containsKey(lyricsBundleKey))
                    lyricsView.text = getString(lyricsBundleKey)!!
                if (containsKey(titleBundleKey))
                    titleView.text = getString(titleBundleKey)!!
            }


        if (dataWasChanged)
            setContent(song)


        return root
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with (outState) {
            putString(titleBundleKey, titleView.text.toString())
            putString(lyricsBundleKey, lyricsView.text.toString())
        }
    }
}
