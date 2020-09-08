package foodie.app.rubikkube.foodie.models.feed

import android.util.Log
import android.widget.Toast
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import androidx.paging.PageKeyedDataSource
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.models.feed.Feed
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.models.feed.FeedResponse
import foodie.app.rubikkube.foodie.services.RetrofitService

class FeedDataSource(private val fetchFeatured: Boolean = false) : PageKeyedDataSource<Long, Feed>() {

    override fun loadInitial(params: LoadInitialParams<Long?>, callback: LoadInitialCallback<Long?, Feed?>) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "")

        val service = RetrofitService.createService(FeedsApi::class.java)

        (if (this.fetchFeatured) service.getFeeds(1, hm)
        else service.getFeaturedFeeds(1, hm))
            .enqueue(object: Callback<FeedResponse> {
                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val feeds: ArrayList<Feed>? = response  .body()?.data
                    callback.onResult(feeds?.toList() ?: Collections.emptyList(), null, 2L)
                }

                override fun onFailure(call: Call<FeedResponse>, t: Throwable?) {
                    Log.d("Failed", t?.message ?: "No Message")
                }
            })
    }

    override fun loadAfter(params: LoadParams<Long?>, callback: LoadCallback<Long?, Feed?>) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "")

        val service = RetrofitService.createService(FeedsApi::class.java)

        (if (this.fetchFeatured) service.getFeeds(params.key, hm)
        else service.getFeaturedFeeds(params.key, hm))
            .enqueue(object: Callback<FeedResponse> {
                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val feeds: ArrayList<Feed>? = response.body().data
                    callback.onResult(feeds?.toList() ?: Collections.emptyList(), params.key + 1)
                }

                override fun onFailure(call: Call<FeedResponse>, t: Throwable?) {}
            })
    }

    override fun loadBefore(params: LoadParams<Long?>, callback: LoadCallback<Long?, Feed?>) {}
}
