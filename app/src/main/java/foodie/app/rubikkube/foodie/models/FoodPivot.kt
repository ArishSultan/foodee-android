package foodie.app.rubikkube.foodie.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FoodPivot(
    @Expose
    @SerializedName("food_id")
    var foodId: String,

    @Expose
    @SerializedName("profile_id")
    var profileId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodId)
        parcel.writeString(profileId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodPivot> {
        override fun createFromParcel(parcel: Parcel): FoodPivot {
            return FoodPivot(parcel)
        }

        override fun newArray(size: Int): Array<FoodPivot?> {
            return arrayOfNulls(size)
        }
    }
}