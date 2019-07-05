package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageReceiver {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("username")
@Expose
private String username;
@SerializedName("profile")
@Expose
private Profile profile;

@SerializedName("device_token")
@Expose
private String device_token;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public Profile getProfile() { return profile; }

public void setProfile(Profile profile) { this.profile = profile; }


    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}