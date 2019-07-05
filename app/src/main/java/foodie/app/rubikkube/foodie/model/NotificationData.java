package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.SerializedName;

public class NotificationData {




    @SerializedName("avatar")
    String mMyProfilePicture;


    @SerializedName("fromUserId")
    String mFromUserID;

    @SerializedName("for")
    String mFor;

    @SerializedName("toUserId")
    String mToUserId;

    @SerializedName("body")
    String mBody;

    @SerializedName("title")
    String mTitle;

    @SerializedName("badge")
    int mBadge;

    @SerializedName("click_action")
    String mClickAction;

    @SerializedName("sound")
    String mSound;

    @SerializedName("content-available")
    int mContent;


    public String getmBody() {
        return mBody;
    }

    public void setmBody(String mBody) {
        this.mBody = mBody;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmBadge() {
        return mBadge;
    }

    public void setmBadge(int mBadge) {
        this.mBadge = mBadge;
    }

    public String getmSound() {
        return mSound;
    }

    public void setmSound(String mSound) {
        this.mSound = mSound;
    }

    public int getmContent() {
        return mContent;
    }

    public void setmContent(int mContent) {
        this.mContent = mContent;
    }

    public String getmClickAction() {
        return mClickAction;
    }

    public void setmClickAction(String mClickAction) {
        this.mClickAction = mClickAction;
    }

    public String getmFor() {
        return mFor;
    }

    public void setmFor(String mFor) {
        this.mFor = mFor;
    }

    public String getmToUserId() {
        return mToUserId;
    }

    public void setmToUserId(String mToUserId) {
        this.mToUserId = mToUserId;
    }


    public String getmMyProfilePicture() {
        return mMyProfilePicture;
    }

    public void setmMyProfilePicture(String mMyProfilePicture) {
        this.mMyProfilePicture = mMyProfilePicture;
    }

    public String getmFromUserID() {
        return mFromUserID;
    }

    public void setmFromUserID(String mFromUserID) {
        this.mFromUserID = mFromUserID;
    }
}

