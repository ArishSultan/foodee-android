package app.wi.lakhanipilgrimage.api


import foodie.app.rubikkube.foodie.model.LoginSignUpResponse
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.model.ImageUploadResp
import foodie.app.rubikkube.foodie.model.UpdateProfileResp
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

    @POST("/api/v1/profile/3")
    fun updateProfile(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<UpdateProfileResp>
}