package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Food(
    @Expose
    @SerializedName("id")
    var id: Int,

    @Expose
    @SerializedName("name")
    var name: String,

    @Expose
    @SerializedName("photo")
    var photo: String,

    @Expose
    @SerializedName("created_at")
    var createdAt: String,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String,

    @Expose
    @SerializedName("pivot")
    var pivot: FoodPivot
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader)!! as Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(FoodPivot::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(photo)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeParcelable(pivot, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }
}