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

    @GET("/api/v1/me")
    fun me(@HeaderMap header: Map<String, String>): Call<MeResponse>

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

    @POST("/api/v1/lat/lng")
    fun sendCurrentLatLng(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<LatLngResponse>

    @GET("/api/v1/search/user/")
    fun getSpecificFoodList(@HeaderMap header: Map<String, String>, @Query("food")food: String, @Query("contribution")contribution: String): Call<ArrayList<MeResponse>>



}