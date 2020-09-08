package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Tag(
    @Expose
    @SerializedName("pivot")
    var pivot: Pivot,

    @Expose
    @SerializedName("username")
    var username: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Pivot::class.java.classLoader)!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeParcelable(pivot, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tag> {
        override fun createFromParcel(parcel: Parcel): Tag {
            return Tag(parcel)
        }

        override fun newArray(size: Int): Array<Tag?> {
            return arrayOfNulls(size)
        }
    }
}