package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

@SerializedName("status")
@Expose
private Boolean status;
@SerializedName("access_token")
@Expose
private String accessToken;
@SerializedName("token_type")
@Expose
private String tokenType;
@SerializedName("user")
@Expose
private User user;
@SerializedName("message")
@Expose
private String message;
@SerializedName("status_code")
@Expose
private Integer statusCode;

public Boolean getStatus() {
return status;
}

public void setStatus(Boolean status) {
this.status = status;
}

public String getAccessToken() {
return accessToken;
}

public void setAccessToken(String accessToken) {
this.accessToken = accessToken;
}

public String getTokenType() {
return tokenType;
}

public void setTokenType(String tokenType) {
this.tokenType = tokenType;
}

public User getUser() {
return user;
}

public void setUser(User user) {
this.user = user;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public Integer getStatusCode() {
return statusCode;
}

public void setStatusCode(Integer statusCode) {
this.statusCode = statusCode;
}

}