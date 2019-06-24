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
}