package foodie.app.rubikkube.foodie.apiUtils

/**
 * An utility object that acts as a Factory for the
 * REST API client.
 *
 * @author arish
 */
object ApiUtils {
//    const val BASE_URL: String = "http://tecra-r850:8000"
    const val BASE_URL: String = "http://18.237.88.70"

    fun getSOService(): SOService? {
        return RetrofitClient.getClient(this.BASE_URL)?.create(SOService::class.java)
    }
}
