package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("username")
@Expose
private String username;
@SerializedName("email")
@Expose
private String email;
@SerializedName("phone")
@Expose
private String phone;
@SerializedName("lat")
@Expose
private String lat;
@SerializedName("lng")
@Expose
private String lng;
@SerializedName("email_confirm")
@Expose
private int email_confirm;
@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("updated_at")
@Expose
private String updatedAt;
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

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public String getPhone() {
return phone;
}

public void setPhone(String phone) {
this.phone = phone;
}

public String getLat() { return lat; }

public void setLat(String lat) { this.lat = lat; }

public String getLng() { return lng; }

public void setLng(String lng) { this.lng = lng; }

public int getEmail_confirm() { return email_confirm; }

public void setEmail_confirm(int email_confirm) { this.email_confirm = email_confirm; }

public String getCreatedAt() {
return createdAt;
}

public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

public String getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(String updatedAt) {
this.updatedAt = updatedAt;
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