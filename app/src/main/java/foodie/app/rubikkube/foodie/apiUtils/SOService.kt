package app.wi.lakhanipilgrimage.api


import foodie.app.rubikkube.foodie.model.LoginSignUpResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST


interface SOService {

    @POST("auth/signup")
    fun signup(@Body requestBody: RequestBody): Call<LoginSignUpResponse>

    @POST("auth/login")
    fun login(@Body requestBody: RequestBody): Call<LoginSignUpResponse>

}