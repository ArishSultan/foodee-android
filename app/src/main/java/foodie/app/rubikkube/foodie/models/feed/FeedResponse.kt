package foodie.app.rubikkube.foodie.models.feed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @Expose
    @SerializedName("current_page")
    val currentPage: Int? = null,

    @Expose
    @SerializedName("data")
    val data: ArrayList<Feed>? = null,

    @Expose
    @SerializedName("first_page_url")
    val firstPageUrl: String? = null,

    @Expose
    @SerializedName("from")
    val from: Int? = null,

    @Expose
    @SerializedName("last_page")
    val lastPage: Int? = null,

    @Expose
    @SerializedName("last_page_url")
    val lastPageUrl: String? = null,

    @Expose
    @SerializedName("next_page_url")
    val nextPageUrl: Any? = null,

    @Expose
    @SerializedName("path")
    val path: String? = null,

    @Expose
    @SerializedName("per_page")
    val perPage: Int? = null,

    @Expose
    @SerializedName("prev_page_url")
    val prevPageUrl: Any? = null,

    @Expose
    @SerializedName("to")
    val to: Int? = null,

    @Expose
    @SerializedName("total")
    val total: Int? = null
)