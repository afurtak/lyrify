package com.afurtak.lyrify


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

private const val titleBundleKey = "Title Bundle Key"
private const val lyricsBundleKey = "Lyrics Bundle Key"

class LyricsFragment : Fragment() {

    var lyrics: String = ""
    var title: String = ""

    lateinit var titleView: TextView
    lateinit var lyricsView: TextView

    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            if (containsKey(lyricsBundleKey))
                lyrics = getString(lyricsBundleKey)!!
            if (containsKey(titleBundleKey))
                title = getString(titleBundleKey)!!
        }
    }

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

        titleView.text = title
        lyricsView.text = lyrics

        arguments?.apply {
            putString(titleBundleKey, title)
            putString(lyricsBundleKey, lyrics)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param title .
         * @param lyrics .
         * @return A new instance of fragment LyricsFragment.
         */
        @JvmStatic
        fun newInstance(title: String, lyrics: String) =
                LyricsFragment().apply {
                    arguments = Bundle().apply {
                        putString(titleBundleKey, title)
                        putString(lyricsBundleKey, lyrics)
                    }
                }
    }
}
