package com.afurtak.lyrify

import android.content.Context
import android.os.Bundle
import android.support.design.button.MaterialButton
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Bundle keys to save data from fragment
 */
private const val artistInputTextKey = "Artist Input Text Key"
private const val titleInputTextKey = "Title Input Text Key"

class SearchSongFormFragment : Fragment() {

    lateinit var artistInput: TextInputEditText
    lateinit var titleInput: TextInputEditText
    lateinit var submitButton: MaterialButton

    lateinit var listener: SearchSongFormFragmentListener

    /**
     * Inflates root layout and initializes each reference to views in root layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_search_song_form, container, false)

        artistInput = root.findViewById(R.id.artist_input)
        titleInput = root.findViewById(R.id.title_input)
        submitButton = root.findViewById(R.id.submit_button)

        if (savedInstanceState != null) {
            restoreData(savedInstanceState)
        }

        // calls listener's method as respond for clicking submit button
        submitButton.setOnClickListener {
            val title = titleInput.text.toString()
            val artist = artistInput.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(context, "Please, enter the title of song", Toast.LENGTH_LONG)
                        .show()
            }

            if (artist.isEmpty()) {
                Toast.makeText(context, "Please, enter the name of artist", Toast.LENGTH_LONG)
                        .show()
            }
            val song = Song(title, artist)
            listener.onSongFormSubmit(song)
        }

        return root
    }

    /**
     * Initialize listener as casted context class.
     * If context class does not implement SearchSongFormFragmentListener
     * show corresponding log and throws exception
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as SearchSongFormFragmentListener
        }
        catch (e: TypeCastException) {
            Log.d("ERROR", "Context of this fragment must implement SearchSongFormListener.")
            e.printStackTrace()
        }
    }

    /**
     * If each text input contains data, save it in outStateBundle
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (titleInput.text!!.isEmpty())
            outState.putCharSequence(titleInputTextKey, titleInput.text)

        if (artistInput.text!!.isEmpty())
            outState.putCharSequence(artistInputTextKey, artistInput.text)
    }

    /**
     * Restores data from Bundle,
     * if contains saved data from each text input.
     */
    private fun restoreData(savedInstanceState: Bundle) {
        if (savedInstanceState.containsKey(artistInputTextKey))
            artistInput.setText(
                    savedInstanceState.getCharSequence(artistInputTextKey))

        if (savedInstanceState.containsKey(titleInputTextKey))
            titleInput.setText(
                    savedInstanceState.getCharSequence(titleInputTextKey))
    }
}


/**
 * Listener to implement by context class of fragment.
 */
interface SearchSongFormFragmentListener {
    fun onSongFormSubmit(song: Song)
}
