package foodie.app.rubikkube.foodie.models.feed

import androidx.lifecycle.MutableLiveData
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.models.feed.FeedResponse
import foodie.app.rubikkube.foodie.services.RetrofitService
import foodie.app.rubikkube.foodie.utilities.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class FeedsRepository {
    companion object {
        var instance: FeedsRepository? = null
    }

    private var feedsApi: FeedsApi = RetrofitService.createService(FeedsApi::class.java)

    fun getNewFeeds(flag: Boolean = false): MutableLiveData<FeedResponse?>? {
        val data = MutableLiveData<FeedResponse?>()

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "")

        (if (flag) this.feedsApi.getFeaturedFeeds(1, hm)
        else this.feedsApi.getFeeds(1, hm)).enqueue(object: Callback<FeedResponse?> {
            override fun onFailure(call: Call<FeedResponse?>?, t: Throwable?) {
                data.value = null
            }

            override fun onResponse(call: Call<FeedResponse?>?,
                                    response: Response<FeedResponse?>?) {
                if (response?.isSuccessful != null) {
                    data.value = response.body()
                }
            }
        })

        return data
    }
}