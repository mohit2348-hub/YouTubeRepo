package com.example.youtuberepo.repository

import com.example.youtuberepo.data.model.YoutubeResponse
import com.example.youtuberepo.data.remote.YoutubeApi
import com.example.youtuberepo.util.PART
import com.example.youtuberepo.util.TYPE
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideosRepository  @Inject constructor(
    private val youtubeApi: YoutubeApi,
) {

    suspend fun getYoutubeVideos(pageToken: String): Response<YoutubeResponse> {
        return youtubeApi.getVideos(PART, TYPE, pageToken)
    }
}