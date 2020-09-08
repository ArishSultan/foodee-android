package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Profile(
    @Expose
    @SerializedName("id")
    var id: Int? = null,

    @Expose
    @SerializedName("user_id")
    var userId: Int? = null,

    @Expose
    @SerializedName("message")
    var message: String? = null,

    @Expose
    @SerializedName("location")
    var location: String? = null,

    @Expose
    @SerializedName("interest")
    var interest: String? = null,

    @Expose
    @SerializedName("ages")
    var ages: String? = null,

    @Expose
    @SerializedName("age")
    var age: Int? = null,

    @Expose
    @SerializedName("is_age_private")
    var isAgePrivate: Boolean = false,

    @Expose
    @SerializedName("min_age")
    var minAge: String? = null,

    @Expose
    @SerializedName("max_age")
    var maxAge: String? = null,

    @Expose
    @SerializedName("gender")
    var gender: String? = null,

    @Expose
    @SerializedName("contribution")
    var contribution: String? = null,

    @Expose
    @SerializedName("avatar")
    var avatar: String? = null,

    @Expose
    @SerializedName("cover")
    var cover: String? = null,

    @Expose
    @SerializedName("notification")
    var notification: Int? = null,

    @Expose
    @SerializedName("created_at")
    var createdAt: String? = null,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @Expose
    @SerializedName("foods")
    val foods: ArrayList<Food> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Food.CREATOR) ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeString(message)
        parcel.writeString(location)
        parcel.writeString(interest)
        parcel.writeString(ages)
        parcel.writeValue(age)
        parcel.writeByte(if (isAgePrivate) 1 else 0)
        parcel.writeString(minAge)
        parcel.writeString(maxAge)
        parcel.writeString(gender)
        parcel.writeString(contribution)
        parcel.writeString(avatar)
        parcel.writeString(cover)
        parcel.writeValue(notification)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeTypedList(foods)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}