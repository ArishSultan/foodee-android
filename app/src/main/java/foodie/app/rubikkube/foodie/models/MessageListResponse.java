package foodie.app.rubikkube.foodie.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageListResponse {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("message_id")
@Expose
private Integer messageId;
@SerializedName("recipient_id")
@Expose
private Integer recipientId;
@SerializedName("sender_id")
@Expose
private Integer sender_id;
@SerializedName("message")
@Expose
private String message;
@SerializedName("type")
@Expose
private Object type;
@SerializedName("is_read")
@Expose
private Integer isRead;
@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("updated_at")
@Expose
private String updatedAt;
@SerializedName("sender")
@Expose
private MessageReceiver messageSender;
@SerializedName("receiver")
@Expose
private MessageReceiver messageReceiver;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getMessageId() {
return messageId;
}

public void setMessageId(Integer messageId) {
this.messageId = messageId;
}

public Integer getRecipientId() {
return recipientId;
}

public void setRecipientId(Integer recipientId) {
this.recipientId = recipientId;
}

public Integer getSender_id() { return sender_id; }

public void setSender_id(Integer sender_id) { this.sender_id = sender_id; }

public String getMessage() { return message; }

public void setMessage(String message) {
this.message = message;
}

public Object getType() {
return type;
}

public void setType(Object type) {
this.type = type;
}

public Integer getIsRead() {
return isRead;
}

public void setIsRead(Integer isRead) {
this.isRead = isRead;
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

public MessageReceiver getMessageSender() { return messageSender; }

public void setMessageSender(MessageReceiver messageSender) { this.messageSender = messageSender; }

public MessageReceiver getMessageReceiver() { return messageReceiver; }

public void setMessageReceiver(MessageReceiver messageReceiver) { this.messageReceiver = messageReceiver; }

public MessageReceiver getReceiver() {
return messageReceiver;
}

public void setReceiver(MessageReceiver messaReceiver) {
this.messageReceiver = messaReceiver;
}

}