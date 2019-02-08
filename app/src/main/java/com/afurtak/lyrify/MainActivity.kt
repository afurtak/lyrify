package com.afurtak.lyrify

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.*
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE
import android.content.Intent
import android.os.PersistableBundle
import androidx.work.*
import com.afurtak.lyrify.spotifyapiutils.spotifyAccessToken
import com.spotify.sdk.android.authentication.AuthenticationRequest


class MainActivity : AppCompatActivity(), SearchSongFormFragmentListener, GetSpotifyLricsFragmentListener {
    private lateinit var fab : FloatingActionButton

    private lateinit var getSpotifySongLyricsFragment: GetSpotifySongLyricsFragment
    private lateinit var searchSongFormFragment: SearchSongFormFragment
    private lateinit var lyricsFragment: LyricsFragment
    private lateinit var listeningLyricsFragment: ListeningLyricsFragment

    val spotifyClientId = "1a9664b8e378430285f036a4783b1ac4"
    val spotifyRedirectUri = "https://example.com/callback/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (spotifyAccessToken == "")
            getSpotifyAuthorizationToken()

        if (savedInstanceState == null) {
            addGetSpotifySongLyricsFragment()
        }
        initializeFab()
    }

//    private fun restoreData(savedInstanceState: Bundle) {
//        if (savedInstanceState.containsKey("SearchSongFormFragmentKey")) {
//            searchSongFormFragment = supportFragmentManager.getFragment(savedInstanceState, "SearchSongFormFragmentKey") as SearchSongFormFragment
//        }
//
//        if (savedInstanceState.containsKey("GetSpotifySongLyricsFragmentKey")) {
//            getSpotifySongLyricsFragment = supportFragmentManager.getFragment(savedInstanceState, "GetSpotifySongLyricsFragmentKey") as GetSpotifySongLyricsFragment
//            supportFragmentManager.beginTransaction()
//                    .add(R.id.search_form_container, getSpotifySongLyricsFragment)
//                    .commit()
//        }
//    }

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

    private fun addLyricsFragmentOnStack(title: String, lyrics: String) {
        if (!::lyricsFragment.isInitialized)
            lyricsFragment = LyricsFragment()
        lyricsFragment.setContent(title, lyrics)

        supportFragmentManager.beginTransaction()
                .replace(R.id.search_form_container, lyricsFragment)
                .addToBackStack(null)
                .commit()
    }

    private fun addLyricsFragmentOnStack() {
        if (!::listeningLyricsFragment.isInitialized)
            listeningLyricsFragment = ListeningLyricsFragment()

        supportFragmentManager.beginTransaction()
                .replace(R.id.search_form_container, listeningLyricsFragment)
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
                    spotifyAccessToken = response.accessToken
                    Toast.makeText(this, spotifyAccessToken, Toast.LENGTH_SHORT).show()
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

    fun getSpotifyAuthorizationToken() {
        val builder = AuthenticationRequest.Builder(spotifyClientId, AuthenticationResponse.Type.TOKEN, spotifyRedirectUri)

        builder.setScopes(arrayOf("user-read-currently-playing"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    /**
     * Calls when button in GetSpotifySongFragment was pressed.
     */
    override fun onGetLyrics() {
        if (spotifyAccessToken == "") {
            getSpotifyAuthorizationToken()
        }
        addLyricsFragmentOnStack()
    }

    /**
     * Calls when button submit was pressed on SearchForSongFragment.
     */
    override fun onSongFormSubmit(song: Song) {

        val inputData = Data.Builder()
                .putString("title", song.title)
                .putString("artist", song.artist)
                .build()

        val task = OneTimeWorkRequest
                .Builder(com.afurtak.lyrify.LyricsDownloader::class.java)
                .setInputData(inputData)
                .build()

        val workManager = WorkManager.getInstance()

        workManager.enqueue(task)
        workManager.getWorkInfoByIdLiveData(task.id).observe(this, Observer {
            if (it != null) {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    val title = it.outputData.getString("title")
                    val lyrics = it.outputData.getString("lyrics")
                    addLyricsFragmentOnStack(title!!, lyrics!!)
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, "GetSpotifySongLyricsFragmentKey", getSpotifySongLyricsFragment)
        supportFragmentManager.putFragment(outState, "SearchSongFormFragmentKey", searchSongFormFragment)
        supportFragmentManager.putFragment(outState, "ListeningLyricsFragment", listeningLyricsFragment)
        supportFragmentManager.putFragment(outState, "LyricsFragment", lyricsFragment)
    }
}
