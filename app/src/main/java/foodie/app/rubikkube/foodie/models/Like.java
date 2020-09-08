package foodie.app.rubikkube.foodie.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Like {

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
private Object phone;
@SerializedName("lat")
@Expose
private Object lat;
@SerializedName("lng")
@Expose
private Object lng;
@SerializedName("email_confirm")
@Expose
private Integer emailConfirm;
@SerializedName("device_token")
@Expose
private String deviceToken;
@SerializedName("timezone")
@Expose
private String timezone;
@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("updated_at")
@Expose
private String updatedAt;
@SerializedName("total_notifications")
@Expose
private Integer totalNotifications;
@SerializedName("profile_link")
@Expose
private String profileLink;
@SerializedName("pivot")
@Expose
private Pivot pivot;
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

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public Object getPhone() {
return phone;
}

public void setPhone(Object phone) {
this.phone = phone;
}

public Object getLat() {
return lat;
}

public void setLat(Object lat) {
this.lat = lat;
}

public Object getLng() {
return lng;
}

public void setLng(Object lng) {
this.lng = lng;
}

public Integer getEmailConfirm() {
return emailConfirm;
}

public void setEmailConfirm(Integer emailConfirm) {
this.emailConfirm = emailConfirm;
}

public String getDeviceToken() {
return deviceToken;
}

public void setDeviceToken(String deviceToken) {
this.deviceToken = deviceToken;
}

public String getTimezone() {
return timezone;
}

public void setTimezone(String timezone) {
this.timezone = timezone;
}

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

public Integer getTotalNotifications() {
return totalNotifications;
}

public void setTotalNotifications(Integer totalNotifications) {
this.totalNotifications = totalNotifications;
}

public String getProfileLink() {
return profileLink;
}

public void setProfileLink(String profileLink) {
this.profileLink = profileLink;
}

public Pivot getPivot() {
return pivot;
}

public void setPivot(Pivot pivot) {
this.pivot = pivot;
}

public Profile getProfile() {
return profile;
}

public void setProfile(Profile profile) {
this.profile = profile;
}

}

