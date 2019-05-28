package foodie.app.rubikkube.foodie.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("user")
    @Expose
    private User user;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getPostId() { return postId; }

    public void setPostId(Integer postId) { this.postId = postId; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Object getStatus() { return status; }

    public void setStatus(Object status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
