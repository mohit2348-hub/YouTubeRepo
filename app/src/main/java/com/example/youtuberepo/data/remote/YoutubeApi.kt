package com.example.youtuberepo.data.remote

import com.example.youtuberepo.data.model.YoutubeResponse
import com.example.youtuberepo.util.API_KEY
import com.example.youtuberepo.util.QUERY_PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {
    @GET("v3/search")
    suspend fun getVideos(
        @Query("part") part:String = "",
        @Query("type") type:String = "",
        @Query("pageToken") pageToken:String = "",
        @Query("maxResults") maxResults: Int = QUERY_PAGE_SIZE,
        @Query("key") apiKey: String = API_KEY
    ): Response<YoutubeResponse>
}