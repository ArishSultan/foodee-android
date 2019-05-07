package foodie.app.rubikkube.foodie.model

import android.os.Parcel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Profile() : Serializable {

        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("user_id")
        @Expose
        var userId: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("location")
        @Expose
        var location: String? = null

        @SerializedName("interest")
        @Expose
        var interest: String? = null

        @SerializedName("ages")
        @Expose
        var ages: String? = null

        @SerializedName("age")
        @Expose
        var age: Int? = null

        @SerializedName("min_age")
        @Expose
        var minAge: Any? = null

        @SerializedName("max_age")
        @Expose
        var maxAge: Any? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

        @SerializedName("contribution")
        @Expose
        var contribution: String? = null

        @SerializedName("avatar")
        @Expose
        var avatar: String? = null

        @SerializedName("cover")
        @Expose
        var cover: String? = null

        @SerializedName("notification")
        @Expose
        var notification: Int? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null

        @SerializedName("foods")
        @Expose
        val foods: List<Food>? = null

}