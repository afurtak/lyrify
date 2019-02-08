package com.afurtak.lyrify.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afurtak.lyrify.R

private const val titleBundleKey = "Title Bundle Key"
private const val lyricsBundleKey = "Lyrics Bundle Key"

abstract class LyricsFragment : Fragment() {

    lateinit var titleView: TextView
    lateinit var lyricsView: TextView
    lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_lyrics, container, false)
        titleView = root.findViewById(R.id.title)
        lyricsView = root.findViewById(R.id.lyrics)

        return root
    }

    fun restoreData(savedInstanceState: Bundle) {
        with (savedInstanceState) {
            titleView.text = getString(titleBundleKey)
            lyricsView.text = getString(lyricsBundleKey)
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
