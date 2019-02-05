package com.afurtak.lyrify

import android.content.Context
import android.os.Bundle
import android.support.design.button.MaterialButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class GetSpotifySongLyricsFragment : Fragment() {

    lateinit var root: View
    lateinit var getLyricsButton: MaterialButton

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
     * show corresponding log and throws exception
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as GetSpotifyLricsFragmentListener
        }
        catch (e: TypeCastException) {
            Log.d("ERROR", "Context of this fragment must implement GetSpotifyLyricsFragmentListener.")
            e.printStackTrace()
        }
    }
}


/**
 * Listener to implement by context class of fragment.
 */
interface GetSpotifyLricsFragmentListener {
    fun onGetLyrics()
}