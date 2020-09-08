package foodie.app.rubikkube.foodie.apiUtils

import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import okhttp3.RequestBody
import okhttp3.MultipartBody
import foodie.app.rubikkube.foodie.models.*


interface SOService {
    /// Finalized Requests
    @GET("/api/v1/posts")
    fun getFeeds(@HeaderMap header: Map<String, String>): Call<FeedResponse>

    /////////////////////////////////////////////////////////////////////////
    @POST("/api/auth/signup")
    fun signUp(@Body requestBody: RequestBody): Call<LoginSignUpResponse>

    @POST("/api/auth/login")
    fun signIn(@Body requestBody: RequestBody): Call<LoginSignUpResponse>

    @POST("/api/forgot-password")
    fun resetPassword(@Body requestBody: RequestBody): Call<Any>


    @GET("/api/v1/profile/{userId}") //no = sign needed
    fun  getProfile(@Path("userId") userId:String,@HeaderMap header: Map<String, String>): Call<MeResponse>


    @POST("/api/v1/fcm/token")
    fun updateFcmToken(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<UpdateFcmTokenResponse>


    @Multipart
    @POST("/api/v1/update/photo")
    fun uploadImage(@HeaderMap header: Map<String, String>,
                    @Part("type") type: RequestBody,
                    @Part userFile: MultipartBody.Part?
    ): Call<ImageUploadResp>

    @POST("/api/v1/profile/{userId}")
    fun updateProfile(@Path("userId") userId:Int,@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<UpdateProfileResp>

    @Multipart
    @POST("/api/v1/food")
    fun addFood(@HeaderMap header: Map<String, String>,
                @Part("name") type: RequestBody,
                @Part foodFile: MultipartBody.Part?
    ): Call<AddFoodResp>

    @GET("/api/v1/food")
    fun getMyFoodList(@HeaderMap header: Map<String, String>): Call<ArrayList<Food>>

    @POST("/api/v1/food/{food_id}")
    fun deleteMyFood(@HeaderMap header: Map<String, String>,@Path("food_id")food_id:String,@Body requestBody: RequestBody): Call<DeleteFoodAndPostResponse>

    @POST("/api/v1/food/{food_id}")
    fun updateFood(@HeaderMap header: Map<String, String>,
                   @Part("name") type: RequestBody,
                   @Part foodFile: MultipartBody.Part?,
                   @Path("food_id")food_id:String,
                   @Body requestBody: RequestBody):Call<AddFoodResp>

    @POST("/api/v1/lat/lng")
    fun sendCurrentLatLng(@HeaderMap header: Map<String, String>, @Body requestBody: RequestBody): Call<Any>

    @GET("/api/v1/search/user/")
    fun getSpecificFoodList(@HeaderMap header: Map<String, String>, @Query("food")food: String, @Query("contribution")contribution: String): Call<ArrayList<MeResponse>>

    @DELETE("/api/v1/profile/{id}")
    fun deleteProfile(@Path("id") profileId: String, @HeaderMap header: Map<String, String>): Call<Any>

    @DELETE("/api/v1/review/{id}")
    fun deleteReview(@Path("id") profileId: Int, @HeaderMap header: Map<String, String>): Call<Any>

    @Multipart
    @POST("/api/v1/posts")
    fun addNewPost(@HeaderMap header: Map<String, String>,@Part photos: List<MultipartBody.Part>?, @Part("content") content: RequestBody,@Part("tags[]") tags: ArrayList<Int>? , @Query("Lat")latitude:Double,@Query("lng")longitude:Double): Call<AddNewPostResponse>

    @Multipart
    @POST("/api/v1/posts/{postId}")
    fun updatePost(@Path("postId") userId:Int,@HeaderMap header: Map<String, String>,@Part photos: List<MultipartBody.Part>?, @Part("content") content: RequestBody,@Part("tags[]") tags: ArrayList<Int>? , @Query("Lat")latitude:Double,@Query("lng")longitude:Double, @Part("_method") method: RequestBody): Call<AddNewPostResponse>

    @POST("/api/v1/posts/{postId}")
    fun deletePost(@Path("postId") userId:Int,@HeaderMap header: Map<String, String>, @Body requestBody: RequestBody): Call<DeleteFoodAndPostResponse>

    @GET("/api/v1/posts")
    fun getTimelinePost(@HeaderMap header: Map<String, String>): Call<FeedResponse>

    @GET("/api/v1/featured")
    fun getFeaturedTimelinePost(@HeaderMap header: Map<String, String>): Call<FeedResponse>

    @GET("/api/v1/posts/{postId}")
    fun getPostById(@Path("postId") userId:String,@HeaderMap header: Map<String, String>): Call<FeedData>

    @POST("/api/v1/posts/delete/image")
    fun deletePostImage(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody):Call<LoginSignUpResponse>

    @POST("/api/v1/comments")
    fun addNewComment(@HeaderMap header: Map<String, String>, @Body requestBody: RequestBody): Call<CommentResponse>

    @POST("/api/v1/post/like/{postId}")
    fun likeAndUnlike(@Path("postId") userId:Int,@HeaderMap header: Map<String, String>): Call<LikeResponse>

    @POST("/api/v1/delete/thread/{threadId}")
    fun deleteThread(@Path("threadId") threadID:Int, @HeaderMap header: Map<String, String>): Call<SimpleResponse>

    @POST("/api/v1/notification/clearall")
    fun deleteAllNotifications(@HeaderMap header: Map<String, String>): Call<SimpleResponse>


    @POST("/api/v1/notification/delete/{notifId}")
    fun deleteSingleNotification(@Path("notifId") notifId:Int, @HeaderMap header: Map<String, String>): Call<SimpleResponse>

    @GET("/api/v1/comments/{postId}")
    fun getComments(@Path("postId") userId:Int,@HeaderMap header: Map<String, String>): Call<GetCommentResponse>

    @GET("/api/v1/find/peoples")
    fun searchUser(@HeaderMap header: Map<String, String>, @Query("username")food: String):Call<ArrayList<User>>

    @GET("/api/v1/timeline/{userId}")
    fun getMyPost(@Path("userId") userId:String,@HeaderMap header: Map<String, String>): Call<FeedResponse>

    @GET("/api/v1/chats")
    fun getInboxList(@HeaderMap header: Map<String, String>): Call<ArrayList<InboxListResponse>>

    @POST("/api/v1/send/message")
    fun sendMessage(@HeaderMap header: Map<String, String>, @Body requestBody: RequestBody):Call<MessageListResponse>

    @POST("/api/v1/add/review")
    fun addReview(@HeaderMap header: Map<String, String>, @Body requestBody: RequestBody):Call<SimpleResponse>

    @GET("/api/v1/messages/{from_id}/{to_id}")
    fun getMessageList(@HeaderMap header: Map<String, String>,@Path("from_id")from_id:String,@Path("to_id")to_id:String):Call<ArrayList<MessageListResponse>>

    @GET("/api/v1/notifications")
    fun getNotificationList(@HeaderMap header: Map<String, String>): Call<ArrayList<NotificationCenter>>

    @POST("/api/v1/comments/{commentId}")
    fun deleteComment(@Path("commentId") userId:Int,@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<SimpleResponse>

    @GET("/api/v1/who/liked/post/{postID}")
    fun getWhoLikeMyPost(@Path("postID") userId:Int,@HeaderMap header: Map<String, String>): Call<List<Like>>

    @GET("/api/v1/reviews/{userID}")
    fun getMyReviews(@Path("userID") userId:Int,@HeaderMap header: Map<String, String>): Call<Reviews>

    @GET("/api/v1/nearby")
    fun getNearByUsers(@Query("type") type: String?, @Query("name") name: String?, @HeaderMap header: Map<String, String>): Call<Array<NearByUser>>

    @POST("/api/v1/subscription/check")
    fun checkSubscription(@Body requestBody: RequestBody): Call<CheckSubscription>

    @POST("/api/v1/subscription/purchase")
    fun purchaseSubscription(@Body requestBody: RequestBody): Call<SuccessResponse>
}