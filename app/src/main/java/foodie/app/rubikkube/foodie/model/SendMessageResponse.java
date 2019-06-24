package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMessageResponse {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("data")
@Expose
private MessageListResponse data;

public Boolean getSuccess() {
return success;
}

public void setSuccess(Boolean success) {
this.success = success;
}

public MessageListResponse getData() {
return data;
}

public void setData(MessageListResponse data) {
this.data = data;
}

}