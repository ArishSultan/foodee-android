package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pivot(
    @Expose
    @SerializedName("post_id")
    var postId: Int,

    @Expose
    @SerializedName("user_id")
    var userId: Int,

    @Expose
    @SerializedName("mode")
    var mode: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(postId)
        parcel.writeValue(userId)
        parcel.writeString(mode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pivot> {
        override fun createFromParcel(parcel: Parcel): Pivot {
            return Pivot(parcel)
        }

        override fun newArray(size: Int): Array<Pivot?> {
            return arrayOfNulls(size)
        }
    }
}