package foodie.app.rubikkube.foodie.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NearByUser(
    @Expose
    @SerializedName("id")
    var id: Int? = null,

    @Expose
    @SerializedName("distance")
    var distance: Double? = null,

    @Expose
    @SerializedName("username")
    var username: String? = null,

    @Expose
    @SerializedName("avatar")
    var avatar: String? = null,

    @Expose
    @SerializedName("device_token")
    var deviceToken: String? = null
)
