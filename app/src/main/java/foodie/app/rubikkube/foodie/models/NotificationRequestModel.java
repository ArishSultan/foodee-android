package foodie.app.rubikkube.foodie.models;

import com.google.gson.annotations.SerializedName;

public class NotificationRequestModel {

    @SerializedName("data")
     NotificationData mData;
    @SerializedName("to")
    String mTo;

    @SerializedName("priority")
    String mPrioriy;

    public NotificationData getData() {
        return mData;
    }

    public void setData(NotificationData data) {
        mData = data;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public String getmPrioriy() {
        return mPrioriy;
    }

    public void setmPrioriy(String mPrioriy) {
        this.mPrioriy = mPrioriy;
    }
}