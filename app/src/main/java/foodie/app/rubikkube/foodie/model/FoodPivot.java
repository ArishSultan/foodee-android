package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FoodPivot implements Serializable {
    @SerializedName("profile_id")
    @Expose
    private String profileId;
    @SerializedName("food_id")
    @Expose
    private String foodID;

    public String getProfileId() { return profileId; }

    public void setProfileId(String profileId) { this.profileId = profileId; }

    public String getFoodID() { return foodID; }

    public void setFoodID(String foodID) { this.foodID = foodID; }
}
