package foodie.app.rubikkube.foodie.models.feed

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import foodie.app.rubikkube.foodie.models.feed.FeedResponse
import retrofit2.http.Query

interface FeedsApi {
    @GET("/api/v1/posts")
    fun getFeeds(@Query("page") page: Long, @HeaderMap header: Map<String, String>): Call<FeedResponse>

    @GET("/api/v1/featured")
    fun getFeaturedFeeds(@Query("page") page: Long, @HeaderMap header: Map<String, String>): Call<FeedResponse>
}