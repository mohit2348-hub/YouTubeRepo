package com.example.youtuberepo.data.model

data class YoutubeResponse(
    val regionCode: String? = null,
    val kind: String? = null,
    val nextPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val etag: String? = null,
    val items: MutableList<Items?>? = null
)

data class Medium(
    val width: Int? = null,
    val url: String? = null,
    val height: Int? = null
)

data class Snippet(
    val publishTime: String? = null,
    val publishedAt: String? = null,
    val description: String? = null,
    val title: String? = null,
    val thumbnails: Thumbnails? = null,
    val channelId: String? = null,
    val channelTitle: String? = null,
    val liveBroadcastContent: String? = null
)

data class Id(
    val kind: String? = null,
    val videoId: String? = null
)

data class Items(
    val snippet: Snippet? = null,
    val kind: String? = null,
    val etag: String? = null,
    val id: Id? = null
)

data class JsonMemberDefault(
    val width: Int? = null,
    val url: String? = null,
    val height: Int? = null
)

data class Thumbnails(
    val jsonMemberDefault: JsonMemberDefault? = null,
    val high: High? = null,
    val medium: Medium? = null
)

data class High(
    val width: Int? = null,
    val url: String? = null,
    val height: Int? = null
)

data class PageInfo(
    val totalResults: Int? = null,
    val resultsPerPage: Int? = null
)
