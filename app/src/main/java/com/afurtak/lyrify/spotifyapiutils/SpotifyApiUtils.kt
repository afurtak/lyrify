package com.afurtak.lyrify.spotifyapiutils

import com.afurtak.lyrify.Song
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CountDownLatch

object CurrentlyPlaying {

    private val url = "https://api.spotify.com/v1/me/player/currently-playing"
    private val tokenHeader = "Authorization"

    fun getCurrentluPlaying(accesToken: String): Song? {
        val responseFromSpotify = getResponseFromSPotify(accesToken)
        responseFromSpotify ?: return null

        return parseResponse(responseFromSpotify)
    }

    fun getResponseFromSPotify(accesToken: String): JSONObject? {

        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .addHeader(tokenHeader, "Bearer $accesToken")
                .build()

        val countDownLatch = CountDownLatch(1)
        var result: String? = null

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = null
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                result = response.body()!!.string()
                countDownLatch.countDown()
            }

        })

        countDownLatch.await()
        result ?: return null
        return JSONObject(result)
    }

    fun parseResponse(response: JSONObject) : Song? {
        return try {
            val track = response.getJSONObject("item")
            val artists = track.getJSONArray("artists")
            val artist = artists.getJSONObject(0)
            val artistName = artist.getString("name")
            val title = track.getString("name")
            Song(title, artistName)
        }
        catch (e: Exception) {
            null
        }
    }

    val tmpRespond = "{\n" +
            "  \"timestamp\": 1545932059341,\n" +
            "  \"context\": {\n" +
            "    \"external_urls\": {\n" +
            "      \"spotify\": \"https://open.spotify.com/artist/4tvKz56Tr39bkhcQUTO0Xr\"\n" +
            "    },\n" +
            "    \"href\": \"https://api.spotify.com/v1/artists/4tvKz56Tr39bkhcQUTO0Xr\",\n" +
            "    \"type\": \"artist\",\n" +
            "    \"uri\": \"spotify:artist:4tvKz56Tr39bkhcQUTO0Xr\"\n" +
            "  },\n" +
            "  \"progress_ms\": 106413,\n" +
            "  \"item\": {\n" +
            "    \"album\": {\n" +
            "      \"album_type\": \"single\",\n" +
            "      \"artists\": [\n" +
            "        {\n" +
            "          \"external_urls\": {\n" +
            "            \"spotify\": \"https://open.spotify.com/artist/4tvKz56Tr39bkhcQUTO0Xr\"\n" +
            "          },\n" +
            "          \"href\": \"https://api.spotify.com/v1/artists/4tvKz56Tr39bkhcQUTO0Xr\",\n" +
            "          \"id\": \"4tvKz56Tr39bkhcQUTO0Xr\",\n" +
            "          \"name\": \"Angus & Julia Stone\",\n" +
            "          \"type\": \"artist\",\n" +
            "          \"uri\": \"spotify:artist:4tvKz56Tr39bkhcQUTO0Xr\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"available_markets\": [\n" +
            "        \"AD\",\n" +
            "        \"AE\",\n" +
            "        \"AR\",\n" +
            "        \"AT\",\n" +
            "        \"AU\",\n" +
            "        \"BE\",\n" +
            "        \"BG\",\n" +
            "        \"BH\",\n" +
            "        \"BO\",\n" +
            "        \"BR\",\n" +
            "        \"CA\",\n" +
            "        \"CH\",\n" +
            "        \"CL\",\n" +
            "        \"CO\",\n" +
            "        \"CR\",\n" +
            "        \"CY\",\n" +
            "        \"CZ\",\n" +
            "        \"DE\",\n" +
            "        \"DK\",\n" +
            "        \"DO\",\n" +
            "        \"DZ\",\n" +
            "        \"EC\",\n" +
            "        \"EE\",\n" +
            "        \"EG\",\n" +
            "        \"ES\",\n" +
            "        \"FI\",\n" +
            "        \"FR\",\n" +
            "        \"GB\",\n" +
            "        \"GR\",\n" +
            "        \"GT\",\n" +
            "        \"HK\",\n" +
            "        \"HN\",\n" +
            "        \"HU\",\n" +
            "        \"ID\",\n" +
            "        \"IE\",\n" +
            "        \"IL\",\n" +
            "        \"IS\",\n" +
            "        \"IT\",\n" +
            "        \"JO\",\n" +
            "        \"JP\",\n" +
            "        \"KW\",\n" +
            "        \"LB\",\n" +
            "        \"LI\",\n" +
            "        \"LT\",\n" +
            "        \"LU\",\n" +
            "        \"LV\",\n" +
            "        \"MA\",\n" +
            "        \"MC\",\n" +
            "        \"MT\",\n" +
            "        \"MX\",\n" +
            "        \"MY\",\n" +
            "        \"NI\",\n" +
            "        \"NL\",\n" +
            "        \"NO\",\n" +
            "        \"NZ\",\n" +
            "        \"OM\",\n" +
            "        \"PA\",\n" +
            "        \"PE\",\n" +
            "        \"PH\",\n" +
            "        \"PL\",\n" +
            "        \"PS\",\n" +
            "        \"PT\",\n" +
            "        \"PY\",\n" +
            "        \"QA\",\n" +
            "        \"RO\",\n" +
            "        \"SA\",\n" +
            "        \"SE\",\n" +
            "        \"SG\",\n" +
            "        \"SK\",\n" +
            "        \"SV\",\n" +
            "        \"TH\",\n" +
            "        \"TN\",\n" +
            "        \"TR\",\n" +
            "        \"TW\",\n" +
            "        \"US\",\n" +
            "        \"UY\",\n" +
            "        \"VN\",\n" +
            "        \"ZA\"\n" +
            "      ],\n" +
            "      \"external_urls\": {\n" +
            "        \"spotify\": \"https://open.spotify.com/album/3Uy10osg3jHEJlJjC39WuL\"\n" +
            "      },\n" +
            "      \"href\": \"https://api.spotify.com/v1/albums/3Uy10osg3jHEJlJjC39WuL\",\n" +
            "      \"id\": \"3Uy10osg3jHEJlJjC39WuL\",\n" +
            "      \"images\": [\n" +
            "        {\n" +
            "          \"height\": 640,\n" +
            "          \"url\": \"https://i.scdn.co/image/bd753926a8cac3dbbdde93709657650de545f4ea\",\n" +
            "          \"width\": 640\n" +
            "        },\n" +
            "        {\n" +
            "          \"height\": 300,\n" +
            "          \"url\": \"https://i.scdn.co/image/5111162c50b0fb38439a66e0e1c28b2fb92e90b6\",\n" +
            "          \"width\": 300\n" +
            "        },\n" +
            "        {\n" +
            "          \"height\": 64,\n" +
            "          \"url\": \"https://i.scdn.co/image/02d3fd9ecf4b745db58535d21fe51b638cd6bf98\",\n" +
            "          \"width\": 64\n" +
            "        }\n" +
            "      ],\n" +
            "      \"name\": \"Youngblood\",\n" +
            "      \"release_date\": \"2018-11-02\",\n" +
            "      \"release_date_precision\": \"day\",\n" +
            "      \"total_tracks\": 1,\n" +
            "      \"type\": \"album\",\n" +
            "      \"uri\": \"spotify:album:3Uy10osg3jHEJlJjC39WuL\"\n" +
            "    },\n" +
            "    \"artists\": [\n" +
            "      {\n" +
            "        \"external_urls\": {\n" +
            "          \"spotify\": \"https://open.spotify.com/artist/4tvKz56Tr39bkhcQUTO0Xr\"\n" +
            "        },\n" +
            "        \"href\": \"https://api.spotify.com/v1/artists/4tvKz56Tr39bkhcQUTO0Xr\",\n" +
            "        \"id\": \"4tvKz56Tr39bkhcQUTO0Xr\",\n" +
            "        \"name\": \"angus   julia stone\",\n" +
            "        \"type\": \"artist\",\n" +
            "        \"uri\": \"spotify:artist:4tvKz56Tr39bkhcQUTO0Xr\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"available_markets\": [\n" +
            "      \"AD\",\n" +
            "      \"AE\",\n" +
            "      \"AR\",\n" +
            "      \"AT\",\n" +
            "      \"AU\",\n" +
            "      \"BE\",\n" +
            "      \"BG\",\n" +
            "      \"BH\",\n" +
            "      \"BO\",\n" +
            "      \"BR\",\n" +
            "      \"CA\",\n" +
            "      \"CH\",\n" +
            "      \"CL\",\n" +
            "      \"CO\",\n" +
            "      \"CR\",\n" +
            "      \"CY\",\n" +
            "      \"CZ\",\n" +
            "      \"DE\",\n" +
            "      \"DK\",\n" +
            "      \"DO\",\n" +
            "      \"DZ\",\n" +
            "      \"EC\",\n" +
            "      \"EE\",\n" +
            "      \"EG\",\n" +
            "      \"ES\",\n" +
            "      \"FI\",\n" +
            "      \"FR\",\n" +
            "      \"GB\",\n" +
            "      \"GR\",\n" +
            "      \"GT\",\n" +
            "      \"HK\",\n" +
            "      \"HN\",\n" +
            "      \"HU\",\n" +
            "      \"ID\",\n" +
            "      \"IE\",\n" +
            "      \"IL\",\n" +
            "      \"IS\",\n" +
            "      \"IT\",\n" +
            "      \"JO\",\n" +
            "      \"JP\",\n" +
            "      \"KW\",\n" +
            "      \"LB\",\n" +
            "      \"LI\",\n" +
            "      \"LT\",\n" +
            "      \"LU\",\n" +
            "      \"LV\",\n" +
            "      \"MA\",\n" +
            "      \"MC\",\n" +
            "      \"MT\",\n" +
            "      \"MX\",\n" +
            "      \"MY\",\n" +
            "      \"NI\",\n" +
            "      \"NL\",\n" +
            "      \"NO\",\n" +
            "      \"NZ\",\n" +
            "      \"OM\",\n" +
            "      \"PA\",\n" +
            "      \"PE\",\n" +
            "      \"PH\",\n" +
            "      \"PL\",\n" +
            "      \"PS\",\n" +
            "      \"PT\",\n" +
            "      \"PY\",\n" +
            "      \"QA\",\n" +
            "      \"RO\",\n" +
            "      \"SA\",\n" +
            "      \"SE\",\n" +
            "      \"SG\",\n" +
            "      \"SK\",\n" +
            "      \"SV\",\n" +
            "      \"TH\",\n" +
            "      \"TN\",\n" +
            "      \"TR\",\n" +
            "      \"TW\",\n" +
            "      \"US\",\n" +
            "      \"UY\",\n" +
            "      \"VN\",\n" +
            "      \"ZA\"\n" +
            "    ],\n" +
            "    \"disc_number\": 1,\n" +
            "    \"duration_ms\": 232746,\n" +
            "    \"explicit\": false,\n" +
            "    \"external_ids\": {\n" +
            "      \"isrc\": \"CAN111800338\"\n" +
            "    },\n" +
            "    \"external_urls\": {\n" +
            "      \"spotify\": \"https://open.spotify.com/track/2kDfaiQeUWVQLl08whGjB9\"\n" +
            "    },\n" +
            "    \"href\": \"https://api.spotify.com/v1/tracks/2kDfaiQeUWVQLl08whGjB9\",\n" +
            "    \"id\": \"2kDfaiQeUWVQLl08whGjB9\",\n" +
            "    \"is_local\": false,\n" +
            "    \"name\": \"youngblood\",\n" +
            "    \"popularity\": 57,\n" +
            "    \"preview_url\": \"https://p.scdn.co/mp3-preview/4176c856f6d44993dcea9a7f0dbff4911d5a54d2?cid=774b29d4f13844c495f206cafdad9c86\",\n" +
            "    \"track_number\": 1,\n" +
            "    \"type\": \"track\",\n" +
            "    \"uri\": \"spotify:track:2kDfaiQeUWVQLl08whGjB9\"\n" +
            "  },\n" +
            "  \"currently_playing_type\": \"track\",\n" +
            "  \"is_playing\": true\n" +
            "}"
}