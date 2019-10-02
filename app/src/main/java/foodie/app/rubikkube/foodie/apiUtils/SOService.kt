package app.wi.lakhanipilgrimage.api


import foodie.app.rubikkube.foodie.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST


interface SOService {

    @POST("/api/auth/signup")
    fun signup(@Body requestBody: RequestBody): Call<LoginSignUpResponse>



    @POST("/api/auth/login")
    fun login(@Body requestBody: RequestBody): Call<LoginSignUpResponse>


    @GET("/api/v1/profile/{userId}") //no = sign needed
    fun getProfile(@Path("userId") userId:String,@HeaderMap header: Map<String, String>): Call<MeResponse>


    @POST("/api/v1/fcm/token")
    fun updateFcmToken(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<UpdateFcmTokenResponse>


    @Multipart
    @POST("/api/v1/update/photo")
    fun uploadImage(@HeaderMap header: Map<String, String>,
                    @Part("type") type: RequestBody,
                    @Part userfile: MultipartBody.Part?
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
    fun sendCurrentLatLng(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<LatLngResponse>

    @GET("/api/v1/search/user/")
    fun getSpecificFoodList(@HeaderMap header: Map<String, String>, @Query("food")food: String, @Query("contribution")contribution: String): Call<ArrayList<MeResponse>>

    @GET("/api/v1/posts")
    fun getFeeds(@HeaderMap header: Map<String, String>): Call<FeedResponse>

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

    @GET("/api/v1/posts/{postId}")
    fun getPostById(@Path("postId") userId:String,@HeaderMap header: Map<String, String>): Call<FeedData>

    @POST("/api/v1/posts/delete/image")
    fun deletePostImage(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody):Call<LoginSignUpResponse>

    @POST("/api/v1/comments")
    fun addNewComment(@HeaderMap header: Map<String, String>, @Body requestBody: RequestBody): Call<CommentResponse>

    @POST("/api/v1/post/like/{postId}")
    fun likeAndUnlike(@Path("postId") userId:Int,@HeaderMap header: Map<String, String>): Call<LikeResponse>

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

    @GET("/api/v1/messages/{from_id}/{to_id}")
    fun getMessageList(@HeaderMap header: Map<String, String>,@Path("from_id")from_id:String,@Path("to_id")to_id:String):Call<ArrayList<MessageListResponse>>

    @GET("/api/v1/notifications")
    fun getNotificationList(@HeaderMap header: Map<String, String>): Call<ArrayList<NotificationCenter>>

}