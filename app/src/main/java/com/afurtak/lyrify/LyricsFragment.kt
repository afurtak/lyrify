package com.afurtak.lyrify


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

private const val titleBundleKey = "Title Bundle Key"
private const val lyricsBundleKey = "Lyrics Bundle Key"

open class LyricsFragment : Fragment() {

    var lyrics: String = ""
    var title: String = ""

    lateinit var titleView: TextView
    lateinit var lyricsView: TextView

    lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_lyrics, container, false)

        savedInstanceState?.apply {
            if (containsKey(lyricsBundleKey))
                lyrics = getString(lyricsBundleKey)!!
            if (containsKey(titleBundleKey))
                title = getString(titleBundleKey)!!
        }

        titleView = root.findViewById(R.id.title)
        lyricsView = root.findViewById(R.id.lyrics)

        titleView.text = title
        lyricsView.text = lyrics

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with (outState) {
            putString(titleBundleKey, title)
            putString(lyricsBundleKey, lyrics)
        }
    }

    fun setContent(title: String, lyrics: String) {
        this.title = title
        this.lyrics = lyrics

        if (::titleView.isInitialized)
        titleView.text = title
        if (::lyricsView.isInitialized)
        lyricsView.text = lyrics
    }
}
