package com.example.youtuberepo.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtuberepo.data.model.YoutubeResponse
import com.example.youtuberepo.repository.VideosRepository
import com.example.youtuberepo.util.NetworkUtil.Companion.hasInternetConnection
import com.example.youtuberepo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class YoutubeVideosViewModel @Inject constructor(
    private val videosRepository: VideosRepository,
    @ApplicationContext private val context: Context
): ViewModel() {

    val videos: MutableLiveData<Resource<YoutubeResponse>> = MutableLiveData()
    var pageToken = ""
    var videoListPage = 1
    var youtubeListResponse: YoutubeResponse? = null

    init {
        getVideosList()
    }

    fun getVideosList() = viewModelScope.launch {
        safeGetVideosList(pageToken)
    }

    private suspend fun safeGetVideosList(pageToken: String) {
        videos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(context)) {
                val response = videosRepository.getYoutubeVideos(pageToken)
                videos.postValue(handleYoutubeListResponse(response))
            } else {
                videos.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (ex: Exception) {
            when (ex) {
                is IOException -> videos.postValue(Resource.Error("Network Failure"))
                else -> videos.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleYoutubeListResponse(response: Response<YoutubeResponse>): Resource<YoutubeResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                //pagination
                videoListPage++
                pageToken = resultResponse.nextPageToken!!
                if (youtubeListResponse == null)
                    youtubeListResponse = resultResponse
                else {
                    val oldList = youtubeListResponse?.items
                    val newList = resultResponse.items
                    oldList?.addAll(newList!!)
                }
                return Resource.Success(youtubeListResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}