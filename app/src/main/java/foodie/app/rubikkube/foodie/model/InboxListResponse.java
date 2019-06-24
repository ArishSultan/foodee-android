package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InboxListResponse {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("from_id")
@Expose
private Integer fromId;
@SerializedName("to_id")
@Expose
private Integer toId;
@SerializedName("avatar")
@Expose
private String avatar;
@SerializedName("username")
@Expose
private String username;
@SerializedName("message")
@Expose
private String message;
@SerializedName("created_at")
@Expose
private String created_at;
@SerializedName("message_id")
@Expose
private String message_id;
@SerializedName("user_id")
@Expose
private Integer userId;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getFromId() {
return fromId;
}

public void setFromId(Integer fromId) {
this.fromId = fromId;
}

public Integer getToId() {
return toId;
}

public void setToId(Integer toId) {
this.toId = toId;
}

public String getAvatar() {
return avatar;
}

public void setAvatar(String avatar) {
this.avatar = avatar;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public String getMessage() { return message; }

public void setMessage(String message) { this.message = message; }

public String getCreated_at() { return created_at; }

public void setCreated_at(String created_at) { this.created_at = created_at; }

public String getMessage_id() { return message_id; }

public void setMessage_id(String message_id) { this.message_id = message_id; }

public Integer getUserId() {
return userId;
}

public void setUserId(Integer userId) {
this.userId = userId;
}

}