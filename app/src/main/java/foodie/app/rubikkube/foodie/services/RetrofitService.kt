package foodie.app.rubikkube.foodie.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://18.237.88.70"/*"http://34.220.151.44/api/"*/)
            .build()

    fun <S> createService(serviceClass: Class<S>): S = retrofit.create(serviceClass)
}
