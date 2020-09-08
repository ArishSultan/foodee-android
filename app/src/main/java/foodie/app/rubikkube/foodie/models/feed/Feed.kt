package foodie.app.rubikkube.foodie.models.feed

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import foodie.app.rubikkube.foodie.models.CommentData
import foodie.app.rubikkube.foodie.models.Tag
import foodie.app.rubikkube.foodie.models.User


data class Feed(
    @Expose
    @SerializedName("id")
    var id: Int,

    @Expose
    @SerializedName("user_id")
    var userId: Int,

    @Expose
    @SerializedName("content")
    var content: String,

    @Expose
    @SerializedName("photos")
    var photos: ArrayList<String>?,

    @Expose
    @SerializedName("type")
    var type: String,

    @Expose
    @SerializedName("status")
    var status: String,

    @Expose
    @SerializedName("created_at")
    var createdAt: String,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String,

    @Expose
    @SerializedName("comments_count")
    var commentsCount: Int,

    @Expose
    @SerializedName("likes_count")
    var likesCount: Int,

    @Expose
    @SerializedName("is_liked")
    var isLiked: Boolean,

    @Expose
    @SerializedName("comments")
    var comments: ArrayList<CommentData>,

    @Expose
    @SerializedName("user")
    var user: User,

    @Expose
    @SerializedName("tags")
    var tags: ArrayList<Tag> = arrayListOf()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readValue(Boolean::class.java.classLoader)!! as Boolean,
        parcel.createTypedArrayList(CommentData.CREATOR)!!,
        parcel.readParcelable(User::class.java.classLoader)!!,
        parcel.createTypedArrayList(Tag.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeString(content)
        parcel.writeStringList(photos)
        parcel.writeString(type)
        parcel.writeString(status)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(commentsCount)
        parcel.writeValue(likesCount)
        parcel.writeValue(isLiked)
        parcel.writeTypedList(comments)
        parcel.writeParcelable(user, flags)
        parcel.writeTypedList(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR = object : Parcelable.Creator<Feed> {
            override fun createFromParcel(parcel: Parcel): Feed {
                return Feed(parcel)
            }

            override fun newArray(size: Int): Array<Feed?> {
                return arrayOfNulls(size)
            }
        }
        val CALLBACK: DiffUtil.ItemCallback<Feed> = object : DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean = true
        }
    }
}