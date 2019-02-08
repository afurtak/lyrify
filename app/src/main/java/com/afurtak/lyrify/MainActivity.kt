package com.afurtak.lyrify

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.*
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE
import android.content.Intent
import android.os.PersistableBundle
import com.afurtak.lyrify.data.Song
import com.afurtak.lyrify.fragments.*
import com.afurtak.lyrify.songutils.*


class MainActivity : AppCompatActivity(), SearchSongFormFragmentListener, GetSpotifyLricsFragmentListener {

    private lateinit var fab : FloatingActionButton
    private lateinit var getSpotifySongLyricsFragment: GetSpotifySongLyricsFragment
    private lateinit var searchSongFormFragment: SearchSongFormFragment
    private lateinit var displayingLyricsFragment: DisplayingLyricsFragment
    private lateinit var listeningSpotifyLyricsFragment: ListeningSpotifyLyricsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!SpotifyUtils.hasSpotifyAccessToken())
            SpotifyUtils.openSpotifyAuthorization(this)

        if (savedInstanceState == null)
            addGetSpotifySongLyricsFragment()

        initializeFab()
    }

    private fun initializeFab() {
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (!::searchSongFormFragment.isInitialized)
                searchSongFormFragment = SearchSongFormFragment()

            supportFragmentManager.beginTransaction()
                    .replace(R.id.search_form_container, searchSongFormFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun addGetSpotifySongLyricsFragment() {
        getSpotifySongLyricsFragment = GetSpotifySongLyricsFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.search_form_container, getSpotifySongLyricsFragment)
                .commit()
    }

    private fun addLyricsFragmentOnStack(song: Song) {
        if (!::displayingLyricsFragment.isInitialized)
            displayingLyricsFragment = DisplayingLyricsFragment()
        displayingLyricsFragment.song = song

        supportFragmentManager.beginTransaction()
                .replace(R.id.search_form_container, displayingLyricsFragment)
                .addToBackStack(null)
                .commit()
    }

    private fun addLyricsFragmentOnStack() {
        if (!::listeningSpotifyLyricsFragment.isInitialized)
            listeningSpotifyLyricsFragment = ListeningSpotifyLyricsFragment()

        supportFragmentManager.beginTransaction()
                .replace(R.id.search_form_container, listeningSpotifyLyricsFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, resultIntent)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    SpotifyUtils.spotifyAccessToken = response.accessToken
                }
                AuthenticationResponse.Type.ERROR ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
                AuthenticationResponse.Type.CODE ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
                AuthenticationResponse.Type.EMPTY ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
                AuthenticationResponse.Type.UNKNOWN ->
                    Toast.makeText(this, "failed authentication", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Calls when button in GetSpotifySongFragment was pressed.
     */
    override fun onGetLyrics() {
        if (!SpotifyUtils.hasSpotifyAccessToken())
            SpotifyUtils.openSpotifyAuthorization(this)
        addLyricsFragmentOnStack()
    }

    /**
     * Calls when button submit was pressed on SearchForSongFragment.
     */
    override fun onSongFormSubmit(song: Song) {
        addLyricsFragmentOnStack(song)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, "GetSpotifySongLyricsFragmentKey", getSpotifySongLyricsFragment)
        supportFragmentManager.putFragment(outState, "SearchSongFormFragmentKey", searchSongFormFragment)
        supportFragmentManager.putFragment(outState, "ListeningSpotifyLyricsFragment", listeningSpotifyLyricsFragment)
        supportFragmentManager.putFragment(outState, "DisplayingLyricsFragment", displayingLyricsFragment)
    }
}
