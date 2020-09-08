package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommentData(
    @Expose
    @SerializedName("id")
    var id: Int? = null,

    @Expose
    @SerializedName("post_id")
    var postId: Int? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @Expose
    @SerializedName("content")
    var content: String? = null,

    @Expose
    @SerializedName("type")
    var type: String? = null,

    @Expose
    @SerializedName("status")
    var status: String? = null,

    @Expose
    @SerializedName("created_at")
    var createdAt: String? = null,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @Expose
    @SerializedName("user")
    var user: User? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(postId)
        parcel.writeValue(userId)
        parcel.writeString(content)
        parcel.writeString(type)
        parcel.writeString(status)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommentData> {
        override fun createFromParcel(parcel: Parcel): CommentData {
            return CommentData(parcel)
        }

        override fun newArray(size: Int): Array<CommentData?> {
            return arrayOfNulls(size)
        }
    }
}