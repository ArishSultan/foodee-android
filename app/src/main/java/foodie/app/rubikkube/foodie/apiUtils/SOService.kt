package app.wi.lakhanipilgrimage.api


import foodie.app.rubikkube.foodie.model.LoginResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Multipart



interface SOService {

    @POST("auth/signup")
    fun signup(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<LoginResponse>

    @POST("auth/login")
    fun login(@HeaderMap header: Map<String, String>,@Body requestBody: RequestBody): Call<LoginResponse>

}