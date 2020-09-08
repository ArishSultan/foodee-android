package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MeResponse(
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
    @SerializedName("created_at")
    var createdAt: String? = null,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @Expose
    @SerializedName("profile")
    var profile: Profile? = null,

    @Expose
    @SerializedName("profile_link")
    var link: String? = null,

    @Expose
    @SerializedName("device_token")
    var device_token: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Profile::class.java.classLoader),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeParcelable(profile, flags)
        parcel.writeString(link)
        parcel.writeString(device_token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeResponse> {
        override fun createFromParcel(parcel: Parcel): MeResponse {
            return MeResponse(parcel)
        }

        override fun newArray(size: Int): Array<MeResponse?> {
            return arrayOfNulls(size)
        }
    }
}
