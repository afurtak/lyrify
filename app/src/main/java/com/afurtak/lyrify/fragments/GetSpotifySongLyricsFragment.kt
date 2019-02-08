package com.afurtak.lyrify.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.afurtak.lyrify.R


class GetSpotifySongLyricsFragment : Fragment() {

    lateinit var root: View
    lateinit var getLyricsButton: Button

    lateinit var listener: GetSpotifyLricsFragmentListener

    /**
     * Inflates root layout and initializes each reference to views in root layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_get_spotify_song_lyrics, container, false)

        getLyricsButton = root.findViewById(R.id.get_spotify_song_lyric)
        getLyricsButton.setOnClickListener {
            listener.onGetLyrics()
        }

        return root
    }

    /**
     * Initialize listener as casted context class.
     * If context class does not implement GetSpotifyLyricsFragmentListener
     * it is initialized as an empty.
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as GetSpotifyLricsFragmentListener
        }
        catch (e: TypeCastException) {
            listener = object : GetSpotifyLricsFragmentListener {
                override fun onGetLyrics() { }
            }
        }
    }
}


/**
 * Listener to implement by context class of fragment.
 */
interface GetSpotifyLricsFragmentListener {
    fun onGetLyrics()
}