package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @Expose
    @SerializedName("id")
    var id: Int? = null,

    @Expose
    @SerializedName("username")
    var username: String? = null,

    @Expose
    @SerializedName("email")
    var email: String? = null,

    @Expose
    @SerializedName("phone")
    var phone: String? = null,

    @Expose
    @SerializedName("lat")
    var lat: String? = null,

    @Expose
    @SerializedName("lng")
    var lng: String? = null,

    @Expose
    @SerializedName("email_confirm")
    var emailConfirm: Int? = null,

    @Expose
    @SerializedName("profile_link")
    var link: String? = null,

    @Expose
    @SerializedName("created_at")
    var createdAt: String? = null,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @Expose
    @SerializedName("profile")
    var profile: Profile? = null,

    @Expose
    @SerializedName("device_token")
    var deviceToken: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Profile::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeValue(emailConfirm)
        parcel.writeString(link)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeParcelable(profile, flags)
        parcel.writeString(deviceToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}